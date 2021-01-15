package com.cy4.betterdungeons.core.init;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.block.DungeonPortalBlock;
import com.cy4.betterdungeons.common.block.KeyCreationTable;
import com.cy4.betterdungeons.common.block.KeyGeneratorBlock;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockInit {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			BetterDungeons.MOD_ID);

	public static final RegistryObject<Block> DUNGEON_PORTAL_FRAME = BLOCKS.register("dungeon_portal_frame",
			() -> new Block(AbstractBlock.Properties.create(Material.IRON, MaterialColor.GRAY)
					.hardnessAndResistance(5f, 10f).harvestTool(ToolType.PICKAXE).sound(SoundType.METAL)));

	public static final RegistryObject<Block> DUNGEON_PORTAL = BLOCKS.register("dungeon_portal",
			() -> new DungeonPortalBlock());

	public static final RegistryObject<Block> KEY_CREATION_TABLE = BLOCKS.register("key_creation_table",
			() -> new KeyCreationTable());
	
	public static final RegistryObject<Block> KEY_GENERATOR = BLOCKS.register("key_generator",
			() -> new KeyGeneratorBlock());

}
