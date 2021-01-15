package com.cy4.betterdungeons.common.dungeon.generation.astar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AStar {

	private static int[][] DIRECTIONS = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

	private static Vec2i getNextVec2i(int row, int col, int i, int j) {
		return new Vec2i(row + i, col + j);
	}

	public static List<Vec2i> solve(Maze maze) {
		List<Vec2i> path = new ArrayList<>();
		if (explore(maze, maze.getEntry().x, maze.getEntry().y, path)) {
			return path;
		}
		return Collections.emptyList();
	}

	private static boolean explore(Maze maze, int row, int col, List<Vec2i> path) {
		if (!maze.isValidLocation(row, col) || maze.isWall(row, col) || maze.isExplored(row, col)) {
			return false;
		}

		path.add(new Vec2i(row, col));
		maze.setVisited(row, col, true);

		if (maze.isExit(row, col)) {
			return true;
		}

		for (int[] direction : DIRECTIONS) {
			Vec2i Vec2i = getNextVec2i(row, col, direction[0], direction[1]);
			if (explore(maze, Vec2i.x, Vec2i.y, path)) {
				return true;
			}
		}

		path.remove(path.size() - 1);
		return false;
	}

}
