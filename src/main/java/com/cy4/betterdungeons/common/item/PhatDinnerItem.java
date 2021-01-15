package com.cy4.betterdungeons.common.item;

import com.cy4.betterdungeons.core.network.data.PlayerDungeonData;
import com.cy4.betterdungeons.core.network.stats.PlayerDungeonStats;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SoupItem;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class PhatDinnerItem extends SoupItem {

	public static Food FOOD = new Food.Builder().saturation(0).hunger(0).fastToEat().setAlwaysEdible().build();

	public PhatDinnerItem(Properties builder) {
		super(builder);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entity) {
		if(entity instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity)entity;
			PlayerDungeonData statsData = PlayerDungeonData.get((ServerWorld) world);
			PlayerDungeonStats stats = statsData.getDungeonStats(player);
			statsData.addDungeonExp(player, (int)(stats.getTnl() / 10));
		}

		return super.onItemUseFinish(stack, world, entity);
	}

}