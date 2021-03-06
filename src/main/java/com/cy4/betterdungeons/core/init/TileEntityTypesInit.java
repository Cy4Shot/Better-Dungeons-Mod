package com.cy4.betterdungeons.core.init;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.te.BonsaiPotTileEntity;
import com.cy4.betterdungeons.common.te.BossBlockTileEntity;
import com.cy4.betterdungeons.common.te.DungeonCrateTileEntity;
import com.cy4.betterdungeons.common.te.DungeonMerchantTileEntity;
import com.cy4.betterdungeons.common.te.HoppingBonsaiPotTileEntity;
import com.cy4.betterdungeons.common.te.KeyCreationTableTileEntity;
import com.cy4.betterdungeons.common.te.KeyGeneratorTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityTypesInit {

	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES,
			BetterDungeons.MOD_ID);

	public static final RegistryObject<TileEntityType<KeyCreationTableTileEntity>> KEY_CREATION_TABLE_TILE_ENTITY_TYPE = TILE_ENTITY_TYPES
			.register("key_creation_table",
					() -> TileEntityType.Builder.create(KeyCreationTableTileEntity::new, BlockInit.KEY_CREATION_TABLE.get()).build(null));
	
	public static final RegistryObject<TileEntityType<DungeonCrateTileEntity>> DUNGEON_CRATE_TILE_ENTITY_TYPE = TILE_ENTITY_TYPES
			.register("dungeon_crate",
					() -> TileEntityType.Builder.create(DungeonCrateTileEntity::new, BlockInit.DUNGEON_CRATE.get()).build(null));

	public static final RegistryObject<TileEntityType<DungeonMerchantTileEntity>> DUNGEON_MERCHANT_TILE_ENTITY_TYPE = TILE_ENTITY_TYPES
			.register("dungeon_merchant",
					() -> TileEntityType.Builder.create(DungeonMerchantTileEntity::new, BlockInit.DUNGEON_MERCHANT.get()).build(null));

	public static final RegistryObject<TileEntityType<BonsaiPotTileEntity>> BONSAI_POT_TILE_ENTITY_TYPE = TILE_ENTITY_TYPES
			.register("bonsai_pot", () -> TileEntityType.Builder.create(BonsaiPotTileEntity::new, BlockInit.BONSAI_POT.get()).build(null));
	
	public static final RegistryObject<TileEntityType<BossBlockTileEntity>> BOSS_BLOCK_TILE_ENTITY_TYPE = TILE_ENTITY_TYPES
			.register("boss_block", () -> TileEntityType.Builder.create(BossBlockTileEntity::new, BlockInit.BOSS_BLOCK.get()).build(null));
	
	public static final RegistryObject<TileEntityType<KeyGeneratorTileEntity>> KEY_GENERATOR_TILE_ENTITY_TYPE = TILE_ENTITY_TYPES
			.register("key_generator", () -> TileEntityType.Builder.create(KeyGeneratorTileEntity::new, BlockInit.KEY_GENERATOR.get()).build(null));

	public static final RegistryObject<TileEntityType<HoppingBonsaiPotTileEntity>> HOPPING_BONSAI_POT_TILE_ENTITY_TYPE = TILE_ENTITY_TYPES
			.register("hopping_bonsai_pot",
					() -> TileEntityType.Builder.create(HoppingBonsaiPotTileEntity::new, BlockInit.HOPPING_BONSAI_POT.get()).build(null));
}
