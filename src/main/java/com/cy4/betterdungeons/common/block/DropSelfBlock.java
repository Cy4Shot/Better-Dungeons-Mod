package com.cy4.betterdungeons.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DropSelfBlock extends Block {

	public DropSelfBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!player.isCreative()) {
			ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(state.getBlock()));
			world.addEntity(entity);
		}
		super.onBlockHarvested(world, pos, state, player);
	}

}
