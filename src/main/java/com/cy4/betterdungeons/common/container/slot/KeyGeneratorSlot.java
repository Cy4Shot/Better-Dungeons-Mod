package com.cy4.betterdungeons.common.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class KeyGeneratorSlot extends Slot {

	public KeyGeneratorSlot(IInventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
		super(inventoryIn, slotIndex, xPosition, yPosition);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
//		return stack.isEmpty() || stack.getItem() == ItemInit.DUNGEON_KEY.get();
		return false;
	}
}