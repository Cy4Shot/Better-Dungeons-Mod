package com.cy4.betterdungeons.core.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cy4.betterdungeons.core.init.DimensionInit;

import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

@Mixin(FallingBlock.class)
public abstract class MixinFallingBlock {

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand, CallbackInfo ci) {
		if (worldIn.getDimensionKey() == DimensionInit.DUNGEON_WORLD)
			ci.cancel();
	}
}
