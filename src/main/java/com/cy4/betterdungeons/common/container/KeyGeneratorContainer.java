package com.cy4.betterdungeons.common.container;

import com.cy4.betterdungeons.common.block.KeyGeneratorBlock;
import com.cy4.betterdungeons.common.te.KeyGeneratorTileEntity;
import com.cy4.betterdungeons.core.init.ContainerTypesInit;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KeyGeneratorContainer extends Container {

	protected KeyGeneratorTileEntity tileEntity;
	public PlayerEntity player;

	public KeyGeneratorContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
		this(windowId, playerInventory, player, KeyGeneratorBlock.getTile(world, pos, world.getBlockState(pos)));
	}

	public KeyGeneratorContainer(int windowId, PlayerInventory playerInventory, PlayerEntity player, KeyGeneratorTileEntity te) {
		super(ContainerTypesInit.KEY_GENERATOR_CONTAINER.get(), windowId);
		this.tileEntity = te;
		this.player = player;
	}

	public KeyGeneratorContainer(int windowId, PlayerInventory inv, PacketBuffer buffer) {
		this(windowId, inv.player.world, buffer.readBlockPos(), inv, inv.player);
	}

	public KeyGeneratorTileEntity getTileEntity() {
		return tileEntity;
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return true;
	}
}