package com.cy4.betterdungeons.common.container;

import com.cy4.betterdungeons.core.init.ContainerTypesInit;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class DungeonCrateContainer extends Container {

	private TileEntity tileEntity;

	public DungeonCrateContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory) {
		super(ContainerTypesInit.DUNGEON_CRATE_CONTAINER.get(), windowId);
		tileEntity = world.getTileEntity(pos);

		if (tileEntity != null) {
			tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
				for (int k = 0; k < 3; ++k) {
					for (int l = 0; l < 9; ++l) {
						this.addSlot(new SlotItemHandler(h, l + k * 9, 8 + l * 18, 18 + k * 18));
					}
				}

				for (int i1 = 0; i1 < 3; ++i1) {
					for (int k1 = 0; k1 < 9; ++k1) {
						this.addSlot(new Slot(playerInventory, k1 + i1 * 9 + 9, 8 + k1 * 18, 84 + i1 * 18));
					}
				}

				for (int j1 = 0; j1 < 9; ++j1) {
					this.addSlot(new Slot(playerInventory, j1, 8 + j1 * 18, 142));
				}
			});
		}
	}

	public DungeonCrateContainer(int windowId, PlayerInventory playerInventory, PacketBuffer buffer) {
		this(windowId, playerInventory.player.world, buffer.readBlockPos(), playerInventory);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack stackInSlot = slot.getStack();
			stack = stackInSlot.copy();
			if (index < 27) {
				if (!this.mergeItemStack(stackInSlot, 27, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(stackInSlot, 0, 27, false)) {
				return ItemStack.EMPTY;
			}

			if (stackInSlot.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}

		return stack;

	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}
}