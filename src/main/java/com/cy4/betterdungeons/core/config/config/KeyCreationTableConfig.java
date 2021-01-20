package com.cy4.betterdungeons.core.config.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.cy4.betterdungeons.common.recipe.RequiredItem;
import com.cy4.betterdungeons.core.config.Config;
import com.google.gson.annotations.Expose;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

public class KeyCreationTableConfig extends Config {

	@Expose
	public List<AltarConfigItem> ITEMS = new ArrayList<>();
	@Expose
	public float PULL_SPEED;
	@Expose
	public double PLAYER_RANGE_CHECK;
	@Expose
	public double ITEM_RANGE_CHECK;
	@Expose
	public int INFUSION_TIME;

	private Random rand = new Random();

	@Override
	public String getName() {
		return "key_creation_table";
	}

	@Override
	protected void reset() {

		ITEMS.add(new AltarConfigItem("minecraft:cobblestone", 1000, 6000));
		ITEMS.add(new AltarConfigItem("minecraft:gold_ingot", 300, 900));
		ITEMS.add(new AltarConfigItem("minecraft:iron_ingot", 400, 1300));
		ITEMS.add(new AltarConfigItem("minecraft:diamond", 0, 100));
		ITEMS.add(new AltarConfigItem("minecraft:sugar_cane", 800, 1600));
		ITEMS.add(new AltarConfigItem("minecraft:oak_log", 400, 800));
		ITEMS.add(new AltarConfigItem("minecraft:spruce_log", 400, 800));
		ITEMS.add(new AltarConfigItem("minecraft:acacia_log", 400, 800));
		ITEMS.add(new AltarConfigItem("minecraft:jungle_log", 400, 800));
		ITEMS.add(new AltarConfigItem("minecraft:dark_oak_log", 400, 800));
		ITEMS.add(new AltarConfigItem("minecraft:apple", 400, 800));
		ITEMS.add(new AltarConfigItem("minecraft:redstone", 400, 1000));
		ITEMS.add(new AltarConfigItem("minecraft:ink_sac", 300, 600));
		ITEMS.add(new AltarConfigItem("minecraft:slime_ball", 200, 800));
		ITEMS.add(new AltarConfigItem("minecraft:rotten_flesh", 500, 1500));
		ITEMS.add(new AltarConfigItem("minecraft:blaze_rod", 80, 190));
		ITEMS.add(new AltarConfigItem("minecraft:brick", 500, 1500));
		ITEMS.add(new AltarConfigItem("minecraft:bone", 500, 1500));
		ITEMS.add(new AltarConfigItem("minecraft:spider_eye", 150, 400));
		ITEMS.add(new AltarConfigItem("minecraft:melon_slice", 1000, 5000));
		ITEMS.add(new AltarConfigItem("minecraft:pumpkin", 1000, 5000));
		ITEMS.add(new AltarConfigItem("minecraft:sand", 1000, 5000));
		ITEMS.add(new AltarConfigItem("minecraft:gravel", 1000, 5000));
		ITEMS.add(new AltarConfigItem("minecraft:wheat", 1000, 2000));
		ITEMS.add(new AltarConfigItem("minecraft:wheat_seeds", 1000, 2000));
		ITEMS.add(new AltarConfigItem("minecraft:carrot", 1000, 2000));
		ITEMS.add(new AltarConfigItem("minecraft:potato", 1000, 2000));
		ITEMS.add(new AltarConfigItem("minecraft:obsidian", 100, 300));
		ITEMS.add(new AltarConfigItem("minecraft:leather", 300, 800));
		ITEMS.add(new AltarConfigItem("minecraft:string", 500, 1200));
		ITEMS.add(new AltarConfigItem("minecraft:bucket_of_pufferfish", 1, 10));
		ITEMS.add(new AltarConfigItem("betterdungeons:niazite_shard", 1, 8));
		ITEMS.add(new AltarConfigItem("betterdungeons:idlite_shard", 1, 8));
		ITEMS.add(new AltarConfigItem("betterdungeons:thalamite_shard", 1, 8));
		ITEMS.add(new AltarConfigItem("betterdungeons:diginite_shard", 1, 8));
		ITEMS.add(new AltarConfigItem("betterdungeons:blocite_shard", 1, 8));
		ITEMS.add(new AltarConfigItem("betterdungeons:grindite_shard", 1, 8));
		ITEMS.add(new AltarConfigItem("betterdungeons:turnite_shard", 1, 8));
		ITEMS.add(new AltarConfigItem("betterdungeons:soulite_shard", 1, 8));

		PULL_SPEED = 1f;
		PLAYER_RANGE_CHECK = 32d;
		ITEM_RANGE_CHECK = 8d;
		INFUSION_TIME = 5;

	}

	public List<RequiredItem> getRequiredItemsFromConfig(ServerWorld world, PlayerEntity player) {

		List<RequiredItem> requiredItems = new ArrayList<>();
		List<AltarConfigItem> configItems = new ArrayList<>(ITEMS);
		for (int i = 0; i < 4; i++) {
			AltarConfigItem configItem = configItems.remove(rand.nextInt(configItems.size()));
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(configItem.ITEM_ID));
			requiredItems.add(new RequiredItem(new ItemStack(item), 0, getRandomInt(configItem.MIN, configItem.MAX)));
		}
		return requiredItems;
	}

	private int getRandomInt(int min, int max) {
		return (int) (Math.random() * (max - min) + min);
	}

	public class AltarConfigItem {

		@Expose
		public String ITEM_ID;
		@Expose
		public int MIN;
		@Expose
		public int MAX;

		public AltarConfigItem(String item, int min, int max) {
			ITEM_ID = item;
			MIN = min;
			MAX = max;
		}

	}

}