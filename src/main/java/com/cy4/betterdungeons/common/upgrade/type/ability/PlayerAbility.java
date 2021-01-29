package com.cy4.betterdungeons.common.upgrade.type.ability;

import com.cy4.betterdungeons.common.upgrade.type.PlayerUpgrade;
import com.google.gson.annotations.Expose;

import net.minecraft.entity.player.PlayerEntity;

public abstract class PlayerAbility extends PlayerUpgrade {

	@Expose
	protected int cooldown;
	@Expose
	protected Behavior behavior;

	public PlayerAbility(int cost, Behavior b, int cooldown) {
		super(cost);
		this.behavior = b;
		this.cooldown = cooldown;
	}

	public void onAction(PlayerEntity player, boolean active) {
	}

	public void onTick(PlayerEntity player, boolean active) {
	}

	public Behavior getBehavior() {
		return behavior;
	}

	public int getCooldown() {
		return cooldown;
	}

	public enum Behavior {
		HOLD_TO_ACTIVATE, PRESS_TO_TOGGLE, RELEASE_TO_PERFORM;
	}

}
