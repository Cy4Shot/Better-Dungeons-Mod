package com.cy4.betterdungeons.core.init;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.world.gen.LootChestFeature;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.event.RegistryEvent;

public class FeatureInit {

	public static ConfiguredFeature<?, ?> BREADCRUMB_CHEST;

	public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
		LootChestFeature.register(event);
		BREADCRUMB_CHEST = register("dungeon_chest", LootChestFeature.INSTANCE.withConfiguration(NoFeatureConfig.field_236559_b_));
	}

	private static <FC extends IFeatureConfig, F extends Feature<FC>> ConfiguredFeature<FC, F> register(String name,
			ConfiguredFeature<FC, F> feature) {
		return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(BetterDungeons.MOD_ID, name),
				feature);
	}

}
