package com.cy4.betterdungeons.core.config.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.cy4.betterdungeons.core.config.Config;
import com.cy4.betterdungeons.core.util.list.SingleItemEntry;
import com.google.gson.annotations.Expose;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class BossTreasureLegendaryConfig extends Config {

	@Expose
	public List<SingleItemEntry> ITEMS = new ArrayList<>();

	@Override
	public String getName() {
		return "boss_treasure_legendary";
	}

	@Override
	protected void reset() {
		ITEMS.add(new SingleItemEntry("minecraft:golden_apple", "{display:{Name:'{\"text\":\"Fancier Apple\"}'}}"));
		ITEMS.add(new SingleItemEntry("minecraft:iron_sword", "{Enchantments:[{id:\"minecraft:sharpness\",lvl:10s}]}"));
	}

	public ItemStack getRandom() {
		Random rand = new Random();
		ItemStack stack = ItemStack.EMPTY;

		SingleItemEntry singleItemEntry = ITEMS.get(rand.nextInt(ITEMS.size()));

		try {
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(singleItemEntry.ITEM));
			stack = new ItemStack(item);
			CompoundNBT nbt = JsonToNBT.getTagFromJson(singleItemEntry.NBT);
			stack.setTag(nbt);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return stack;
	}

}