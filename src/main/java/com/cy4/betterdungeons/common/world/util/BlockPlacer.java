package com.cy4.betterdungeons.common.world.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

@FunctionalInterface
public interface BlockPlacer {

	BlockState getState(BlockPos pos, Direction facing);

}
