package com.cy4.betterdungeons.core.init;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.world.gen.feature.DungeonStructure;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class StructureInit {

	/*
	 * Awesome registry code, courtesy of TelepathicGrunt.
	 */

	public static final DeferredRegister<Structure<?>> STRUCTURES = DeferredRegister
			.create(ForgeRegistries.STRUCTURE_FEATURES, BetterDungeons.MOD_ID);

	public static final RegistryObject<Structure<NoFeatureConfig>> DUNGEON = STRUCTURES.register("dungeon",
			() -> new DungeonStructure(NoFeatureConfig.field_236558_a_));

	public static void setupStructures() {
		setupMapSpacingAndLand(DUNGEON.get(), new StructureSeparationSettings(10, 9, 1638), false);
	}

	public static <F extends Structure<?>> void setupMapSpacingAndLand(F structure,
			StructureSeparationSettings structureSeparationSettings, boolean transformSurroundingLand) {
		Structure.NAME_STRUCTURE_BIMAP.put(structure.getRegistryName().toString(), structure);

		if (transformSurroundingLand) {
			Structure.field_236384_t_ = ImmutableList.<Structure<?>>builder().addAll(Structure.field_236384_t_)
					.add(structure).build();
		}

		DimensionStructuresSettings.field_236191_b_ = ImmutableMap.<Structure<?>, StructureSeparationSettings>builder()
				.putAll(DimensionStructuresSettings.field_236191_b_).put(structure, structureSeparationSettings)
				.build();
	}
}
