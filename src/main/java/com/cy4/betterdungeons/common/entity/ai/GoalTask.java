package com.cy4.betterdungeons.common.entity.ai;

import java.util.Random;

import org.apache.commons.lang3.ObjectUtils;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.World;

public abstract class GoalTask<T extends LivingEntity> extends Goal {

	private final T entity;

	public GoalTask(T entity) {
		this.entity = entity;
	}

	public T getEntity() {
		return this.entity;
	}

	public World getWorld() {
		return this.getEntity().world;
	}

	public Random getRandom() {
		return ObjectUtils.firstNonNull(this.getWorld().getRandom(), this.getEntity().getRNG());
	}

}