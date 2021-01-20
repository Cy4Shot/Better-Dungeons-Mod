package com.cy4.betterdungeons.common.block;

import com.cy4.betterdungeons.core.init.BlockInit;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class ShardBlock extends DropSelfBlock {

	public ShardBlock() {
		super(AbstractBlock.Properties.create(Material.GLASS, MaterialColor.QUARTZ).hardnessAndResistance(0.3F).sound(SoundType.GLASS)
				.hardnessAndResistance(0.3F).sound(SoundType.GLASS).notSolid().doesNotBlockMovement()
				.setAllowsSpawn(BlockInit::neverAllowSpawn));
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.down()).isSolid();
	}
}
