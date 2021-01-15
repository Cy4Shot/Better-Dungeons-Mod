package com.cy4.betterdungeons.core.init;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.te.KeyCreationTableTileEntity;
import com.cy4.betterdungeons.common.te.KeyGeneratorTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityTypesInit {

	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister
			.create(ForgeRegistries.TILE_ENTITIES, BetterDungeons.MOD_ID);

	public static final RegistryObject<TileEntityType<KeyCreationTableTileEntity>> KEY_CREATION_TABLE_TILE_ENTITY_TYPE = TILE_ENTITY_TYPES
			.register("key_creation_table", () -> TileEntityType.Builder
					.create(KeyCreationTableTileEntity::new, BlockInit.KEY_CREATION_TABLE.get()).build(null));

	public static final RegistryObject<TileEntityType<KeyGeneratorTileEntity>> KEY_GENERATOR_TILE_ENTITY_TYPE = TILE_ENTITY_TYPES
			.register("key_generator", () -> TileEntityType.Builder
					.create(KeyGeneratorTileEntity::new, BlockInit.KEY_GENERATOR.get()).build(null));
}
