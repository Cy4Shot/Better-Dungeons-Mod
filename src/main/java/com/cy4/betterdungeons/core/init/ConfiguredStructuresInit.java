package com.cy4.betterdungeons.core.init;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.world.gen.DungeonStructure;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.feature.StructureFeature;

public class ConfiguredStructuresInit {

	public static StructureFeature<?, ?> CONFIGURED_DUNGEON = StructureInit.DUNGEON.get()
			.withConfiguration(new DungeonStructure.Config(() -> DungeonStructure.Pools.START, 6));

	public static void registerConfiguredStructures() {
		Registry<StructureFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE;
		Registry.register(registry, new ResourceLocation(BetterDungeons.MOD_ID, "dungeon"),
				CONFIGURED_DUNGEON);
		FlatGenerationSettings.STRUCTURES.put(StructureInit.DUNGEON.get(), CONFIGURED_DUNGEON);
	}

}
