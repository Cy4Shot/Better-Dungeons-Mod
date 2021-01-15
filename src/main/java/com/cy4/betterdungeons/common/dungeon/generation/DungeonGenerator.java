package com.cy4.betterdungeons.common.dungeon.generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.cy4.betterdungeons.common.dungeon.generation.astar.AStar;
import com.cy4.betterdungeons.common.dungeon.generation.astar.Maze;
import com.cy4.betterdungeons.common.dungeon.generation.astar.Vec2i;

public class DungeonGenerator {

//	public static void main(String[] args) {
//		new DungeonGenerator(6).buildDungeon();
//	}

	private static Vec2i[] DIRECTIONS = new Vec2i[] { new Vec2i(1, 0), new Vec2i(-1, 0), new Vec2i(0, 1),
			new Vec2i(0, -1) };

	private Random r;
	private int gridSize;
	private int reducedSize;
	private DungeonTileType[][] dungeon;

	private float chanceForBossRoom = 0.1f;
	private float chanceForRoom = 0.9f;
	private float chanceForTunnel = 0.7f;

	public DungeonGenerator(int size) {

		// Init Variables
		this.r = new Random();
		this.reducedSize = size;
		this.gridSize = 2 * size - 1;

		// Create array
		this.dungeon = new DungeonTileType[this.gridSize][this.gridSize];
		for (DungeonTileType[] row : this.dungeon)
			Arrays.fill(row, DungeonTileType.NONE);
	}

	public DungeonTileType[][] buildDungeon() {
		int startX = nextEvenNumber(0, this.gridSize);
		int startZ = nextEvenNumber(0, this.gridSize);
		this.dungeon[startX][startZ] = DungeonTileType.ENTRY;
		List<Vec2i> bossRooms = new ArrayList<Vec2i>();
		for (int i = 0; i < this.gridSize; i += 2) {
			for (int j = 0; j < this.gridSize; j += 2) {
				if (this.dungeon[i][j] != DungeonTileType.ENTRY) {
					if (this.r.nextFloat() < this.chanceForBossRoom) {
						this.dungeon[i][j] = DungeonTileType.BOSS;
						bossRooms.add(new Vec2i(i, j));
					} else if (this.r.nextFloat() < this.chanceForRoom)
						this.dungeon[i][j] = DungeonTileType.ROOM;
				}
			}
		}
		List<Vec2i> bossConnections = new ArrayList<Vec2i>();
		for (Vec2i bossRoom : bossRooms) {
			List<Vec2i> directions = Arrays.asList(DIRECTIONS);
			Collections.shuffle(directions);
			for (Vec2i dir : directions) {
				try {
					int newPosX = bossRoom.x + dir.x * 2;
					int newPosY = bossRoom.y + dir.y * 2;
					if (this.dungeon[newPosX][newPosY] == DungeonTileType.ROOM) {
						bossConnections.add(new Vec2i(newPosX, newPosY));
						dungeon[bossRoom.x + dir.x][bossRoom.y + dir.y] = dir.x == 0 ? DungeonTileType.TUNNEL_EW
								: DungeonTileType.TUNNEL_NS;
						break;
					}
				} catch (IndexOutOfBoundsException i) {
				}
			}
		}

		Vec2i initCoords = new Vec2i((int) startX / 2, (int) startZ / 2);

		String[][] nodes = new String[this.reducedSize][this.reducedSize];
		for (int i = 0; i < this.reducedSize; i++) {
			for (int j = 0; j < this.reducedSize; j++) {
				nodes[i][j] = asString(dungeon[i * 2][j * 2]);
			}
		}

		for (Vec2i bossConnection : bossConnections) {
			Vec2i destCoords = new Vec2i((int) bossConnection.x / 2, (int) bossConnection.y / 2);

			List<Vec2i> path = AStar.solve(new Maze(nodes, initCoords, destCoords));
			Vec2i prev = initCoords;
			for (Vec2i node : path) {
				if (node.equals(prev))
					continue;

				Vec2i direction = new Vec2i(node.x - prev.x, node.y - prev.y);
				prev = node;

				Vec2i pos = new Vec2i(node.x * 2 - direction.x, node.y * 2 - direction.y);
				dungeon[pos.x][pos.y] = direction.x == 0 ? DungeonTileType.TUNNEL_EW : DungeonTileType.TUNNEL_NS;
			}

		}

		for (int x = 0; x < dungeon.length; x++) {
			for (int z = 0; z < dungeon.length; z++) {
				if (((x & 1) != 0 ^ (z & 1) != 0) || ((z & 1) != 0 ^ (x & 1) != 0)) {
					if (dungeon[x][z] != DungeonTileType.TUNNEL_EW && dungeon[x][z] != DungeonTileType.TUNNEL_NS) {
						if (this.r.nextFloat() < this.chanceForTunnel) {
							try {
								if (r.nextInt(2) == 1) {
									if (dungeon[x + 1][z] == DungeonTileType.ROOM
											&& dungeon[x - 1][z] == DungeonTileType.ROOM) {
										dungeon[x][z] = DungeonTileType.TUNNEL_NS;
										continue;
									}
									if (dungeon[x][z + 1] == DungeonTileType.ROOM
											&& dungeon[x][z - 1] == DungeonTileType.ROOM) {
										dungeon[x][z] = DungeonTileType.TUNNEL_EW;
										continue;
									}
								}

								else {
									if (dungeon[x][z + 1] == DungeonTileType.ROOM
											&& dungeon[x][z - 1] == DungeonTileType.ROOM) {
										dungeon[x][z] = DungeonTileType.TUNNEL_EW;
										continue;
									}
									if (dungeon[x + 1][z] == DungeonTileType.ROOM
											&& dungeon[x - 1][z] == DungeonTileType.ROOM) {
										dungeon[x][z] = DungeonTileType.TUNNEL_NS;
										continue;
									}
								}
							}

							catch (IndexOutOfBoundsException e) {

							}
						}
					}
				}
			}
		}

		printDungeon();

		return dungeon;
	}

	private int nextEvenNumber(int min, int max) {
		min = min % 2 == 1 ? min + 1 : min;
		max = max % 2 == 1 ? max - 1 : max;
		int randomNum = ((this.r.nextInt((max - min)) + min) + 1) / 2;
		return randomNum * 2;
	}

	public static String asString(DungeonTileType t) {
		switch (t) {
		case BOSS:
			return "B";
		case ENTRY:
			return "E";
		case ROOM:
			return "R";
		case TUNNEL_EW:
			return "-";
		case TUNNEL_NS:
			return "|";
		default:
			return " ";
		}
	}

	public void printDungeon() {
		for (DungeonTileType[] x : dungeon) {
			for (DungeonTileType y : x) {
				System.out.print(asString(y) + " ");
			}
			System.out.println();
		}
	}

	public enum DungeonTileType {
		NONE, ROOM, ENTRY, BOSS, TUNNEL_NS, TUNNEL_EW;
	}

}
