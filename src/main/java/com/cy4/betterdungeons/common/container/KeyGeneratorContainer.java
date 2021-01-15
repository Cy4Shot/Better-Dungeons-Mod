package com.cy4.betterdungeons.common.container;

import java.util.Objects;

import com.cy4.betterdungeons.common.container.slot.KeyGeneratorSlot;
import com.cy4.betterdungeons.common.te.KeyGeneratorTileEntity;
import com.cy4.betterdungeons.core.init.BlockInit;
import com.cy4.betterdungeons.core.init.ContainerTypesInit;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;

public class KeyGeneratorContainer extends Container {
	public final KeyGeneratorTileEntity tileEntity;
	private final IWorldPosCallable canInteractWithCallable;

	public KeyGeneratorContainer(final int windowId, final PlayerInventory playerInventory,
			final KeyGeneratorTileEntity tileEntity) {
		super(ContainerTypesInit.KEY_GENERATOR_CONTAINER.get(), windowId);
		this.tileEntity = tileEntity;
		this.canInteractWithCallable = IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos());

		this.addSlot(new KeyGeneratorSlot((IInventory) tileEntity, 0, 80, 35));

		// Main Player Inventory
		for (int playerInvRow = 0; playerInvRow < 3; playerInvRow++) {
			for (int playerInvCol = 0; playerInvCol < 9; playerInvCol++) {
				this.addSlot(new Slot(playerInventory, playerInvCol + playerInvRow * 9 + 9, 8 + playerInvCol * 18,
						166 - (4 - playerInvRow) * 18 - 10));
			}

		}

		for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
			this.addSlot(new Slot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 142));
		}
	}

	private static KeyGeneratorTileEntity getTileEntity(final PlayerInventory playerInventory,
			final PacketBuffer data) {
		Objects.requireNonNull(playerInventory, "playerInvetory cannot be null");
		Objects.requireNonNull(data, "data cannot be null");
		final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
		if (tileAtPos instanceof KeyGeneratorTileEntity) {
			return (KeyGeneratorTileEntity) tileAtPos;
		}
		throw new IllegalStateException("TileEntity is not correct!" + tileAtPos);
	}

	public KeyGeneratorContainer(final int windowIn, final PlayerInventory playerInventory, final PacketBuffer data) {
		this(windowIn, playerInventory, getTileEntity(playerInventory, data));
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {

		return isWithinUsableDistance(canInteractWithCallable, playerIn, BlockInit.KEY_GENERATOR.get());
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack stack1 = slot.getStack();
			stack = stack1.copy();
			if (index < 36) {
				if (!this.mergeItemStack(stack1, 15, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(stack1, 0, 15, false)) {
				return ItemStack.EMPTY;
			}

			if (stack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}
		return stack;
	}
}