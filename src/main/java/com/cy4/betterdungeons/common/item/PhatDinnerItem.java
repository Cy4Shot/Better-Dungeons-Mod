package com.cy4.betterdungeons.common.item;

import com.cy4.betterdungeons.core.init.ItemInit;
import com.cy4.betterdungeons.core.network.data.PlayerDungeonData;
import com.cy4.betterdungeons.core.network.stats.PlayerDungeonStats;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SoupItem;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class PhatDinnerItem extends SoupItem {

	public PhatDinnerItem() {
		super(ItemInit.basicItem().maxStackSize(16).food(ItemInit.FOOD));
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entity) {
		if(entity instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity)entity;
			PlayerDungeonData statsData = PlayerDungeonData.get((ServerWorld) world);
			PlayerDungeonStats stats = statsData.getDungeonStats(player);
			statsData.addDungeonExp(player, (int)(stats.getTnl() / 2));
		}

		return super.onItemUseFinish(stack, world, entity);
	}

}