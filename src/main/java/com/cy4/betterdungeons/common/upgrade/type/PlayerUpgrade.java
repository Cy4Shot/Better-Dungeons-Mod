package com.cy4.betterdungeons.common.upgrade.type;

import com.google.gson.annotations.Expose;

import net.minecraft.entity.player.PlayerEntity;

public abstract class PlayerUpgrade {
	@Expose
	private int cost;

	public PlayerUpgrade(int cost) {
		this.cost = cost;
	}

	public int getCost() {
		return this.cost;
	}

	public void onAdded(PlayerEntity player) {
	}

	public void tick(PlayerEntity player) {
	}

	public void onRemoved(PlayerEntity player) {
	}
}
