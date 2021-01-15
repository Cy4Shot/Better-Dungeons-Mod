//package com.cy4.betterdungeons.core.init;
//
//import com.cy4.betterdungeons.BetterDungeons;
//import com.cy4.betterdungeons.common.world.gen.feature.DungeonStructure;
//
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.registry.WorldGenRegistries;
//import net.minecraft.world.gen.feature.IFeatureConfig;
//import net.minecraft.world.gen.feature.StructureFeature;
//import net.minecraft.world.gen.feature.structure.Structure;
//
//public class FeatureInit {
//
//	public static StructureFeature<DungeonStructure.Config, ? extends Structure<DungeonStructure.Config>> DUNGEON;
//
//	public static void registerStructureFeatures() {
//		DUNGEON = register("dungeon", StructureInit.DUNGEON.get()
//				.withConfiguration(new DungeonStructure.Config(() -> DungeonStructure.Pools.START, 6)));
//	}
//
//	private static <FC extends IFeatureConfig, F extends Structure<FC>> StructureFeature<FC, F> register(String name,
//			StructureFeature<FC, F> feature) {
//		return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE,
//				new ResourceLocation(BetterDungeons.MOD_ID, name), feature);
//	}
//
//}
