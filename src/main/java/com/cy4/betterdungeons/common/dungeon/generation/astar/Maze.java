package com.cy4.betterdungeons.common.dungeon.generation.astar;

import java.util.Arrays;

import com.cy4.betterdungeons.common.dungeon.generation.DungeonGenerator;
import com.cy4.betterdungeons.common.dungeon.generation.DungeonGenerator.DungeonTileType;

public class Maze {
	private String[][] data;
	private boolean[][] visited;
	private Vec2i entry;
	private Vec2i exit;

	public Maze(String[][] graph, Vec2i entry, Vec2i exit) {
		this.data = graph;
		this.visited = new boolean[graph.length][graph.length];
		this.setEntry(entry);
		this.setExit(exit);
	}

	public void reset() {
		for (boolean[] row : this.visited)
			Arrays.fill(row, false);
	}

	public void setVisited(int i, int j, boolean b) {
		this.visited[i][j] = b;
	}

	public boolean isValidLocation(int i, int j) {
		return i >= 0 && i <= this.data.length - 1 && j >= 0 && j <= this.data.length - 1;
	}

	public boolean isWall(int i, int j) {
		return this.data[i][j] == DungeonGenerator.asString(DungeonTileType.NONE)
				|| this.data[i][j] == DungeonGenerator.asString(DungeonTileType.BOSS);
	}
	
	public boolean isExplored(int i, int j) {
		return this.visited[i][j];
	}
	
	public boolean isExit(int i, int j) {
		return this.exit.x == i && this.exit.y == j;
	}

	public Vec2i getEntry() {
		return entry;
	}

	public void setEntry(Vec2i entry) {
		this.entry = entry;
	}

	public Vec2i getExit() {
		return exit;
	}

	public void setExit(Vec2i exit) {
		this.exit = exit;
	}
}
