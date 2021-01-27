package com.cy4.betterdungeons.core.util;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class SideOnlyFixer {

	public static int getSlotFor(PlayerInventory inventory, ItemStack stack) {
		for (int i = 0; i < inventory.mainInventory.size(); ++i) {
			if (!inventory.mainInventory.get(i).isEmpty() && stackEqualExact(stack, inventory.mainInventory.get(i))) {
				return i;
			}
		}

		return -1;
	}

	private static boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
		return stack1.getItem() == stack2.getItem() && ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

}
