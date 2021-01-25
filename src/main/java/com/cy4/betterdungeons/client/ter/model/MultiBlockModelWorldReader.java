package com.cy4.betterdungeons.client.ter.model;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;

public class MultiBlockModelWorldReader implements IBlockReader, BiomeManager.IBiomeReader {
	private MultiblockBlockModel model;

	private IWorldReader blockWorld;
	private BlockPos blockPos;

	public MultiBlockModelWorldReader(MultiblockBlockModel model) {
		this.model = model;
	}

	public MultiBlockModelWorldReader(MultiblockBlockModel model, IWorldReader blockWorld, BlockPos blockPos) {
		this.model = model;
		this.blockWorld = blockWorld;
		this.blockPos = blockPos;
	}

	public Biome getBiome(BlockPos pos) {
		return blockWorld.getBiome(blockPos);
	}

	public IWorldReader getContextWorld() {
		return blockWorld;
	}

	public BlockPos getContextPos() {
		return blockPos;
	}

	@Nullable
	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		return null;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		if (model.blocks.get(pos) != null) {
			return model.blocks.get(pos);
		}
		return Blocks.AIR.getDefaultState();
	}

	@Override
	public Biome getNoiseBiome(int x, int y, int z) {
		return null;
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return null;
	}
}