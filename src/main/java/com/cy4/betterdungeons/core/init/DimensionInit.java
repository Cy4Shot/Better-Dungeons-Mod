package com.cy4.betterdungeons.core.init;

import com.cy4.betterdungeons.BetterDungeons;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

public class DimensionInit {
	public static final RegistryKey<DimensionType> DUNGEON_DIMENSION = RegistryKey
			.getOrCreateKey(Registry.DIMENSION_TYPE_KEY, new ResourceLocation(BetterDungeons.MOD_ID, "dungeon"));
	
	public static final RegistryKey<World> DUNGEON_WORLD = RegistryKey.getOrCreateKey(Registry.WORLD_KEY,
			new ResourceLocation(BetterDungeons.MOD_ID, "dungeon"));
}
