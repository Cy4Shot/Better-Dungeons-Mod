package com.cy4.betterdungeons.core.itemgroup;

import com.cy4.betterdungeons.core.init.ItemInit;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class BetterDungeonsItemGroup extends ItemGroup {

	public static final BetterDungeonsItemGroup BETTER_DUNGEONS = new BetterDungeonsItemGroup(ItemGroup.GROUPS.length,
			"better_dungeons");

	public BetterDungeonsItemGroup(int index, String label) {
		super(index, label);
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(ItemInit.DUNGEON_KEY.get());
	}

}
