package com.cy4.betterdungeons.common.dungeon.generation.astar;

public class Vec2i {
	// Members
	public int x;
	public int y;

	// Constructors
	public Vec2i() {
		this.x = 0;
		this.y = 0;
	}

	public Vec2i(int x, int y) {
		this.x = x;
		this.y = y;
	}

	// Compare two vectors
	public boolean equals(Vec2i other) {
		return (this.x == other.x && this.y == other.y);
	}
	
	@Override
	public String toString() {
		return x + ", " + y;
	}
}