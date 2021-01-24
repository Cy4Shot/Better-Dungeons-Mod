package com.cy4.betterdungeons.core.init;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.block.DropSelfBlock;
import com.cy4.betterdungeons.common.block.DungeonMerchantBlock;
import com.cy4.betterdungeons.common.block.DungeonPortalBlock;
import com.cy4.betterdungeons.common.block.KeyCreationTable;
import com.cy4.betterdungeons.common.block.KeyGeneratorBlock;
import com.cy4.betterdungeons.common.block.ShardBlock;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockInit {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BetterDungeons.MOD_ID);

	public static final RegistryObject<Block> DUNGEON_PORTAL_FRAME = BLOCKS.register("dungeon_portal_frame",
			() -> new Block(AbstractBlock.Properties.create(Material.IRON, MaterialColor.GRAY).hardnessAndResistance(5f, 10f)
					.harvestTool(ToolType.PICKAXE).sound(SoundType.METAL)));

	public static final RegistryObject<Block> TOUGH_STONE = BLOCKS.register("tough_stone",
			() -> new Block(AbstractBlock.Properties.from(Blocks.BEDROCK)));

	public static final RegistryObject<Block> DUNGEON_PORTAL = BLOCKS.register("dungeon_portal", () -> new DungeonPortalBlock());
	public static final RegistryObject<Block> KEY_CREATION_TABLE = BLOCKS.register("key_creation_table", () -> new KeyCreationTable());
	public static final RegistryObject<Block> KEY_GENERATOR = BLOCKS.register("key_generator", () -> new KeyGeneratorBlock());
	public static final RegistryObject<Block> DUNGEON_MERCHANT = BLOCKS.register("dungeon_merchant", () -> new DungeonMerchantBlock());

	public static final RegistryObject<Block> NIAZITE_SHARD = BLOCKS.register("niazite_shard", () -> new ShardBlock());
	public static final RegistryObject<Block> IDLITE_SHARD = BLOCKS.register("idlite_shard", () -> new ShardBlock());
	public static final RegistryObject<Block> THALAMITE_SHARD = BLOCKS.register("thalamite_shard", () -> new ShardBlock());
	public static final RegistryObject<Block> DIGINITE_SHARD = BLOCKS.register("diginite_shard", () -> new ShardBlock());
	public static final RegistryObject<Block> BLOCITE_SHARD = BLOCKS.register("blocite_shard", () -> new ShardBlock());
	public static final RegistryObject<Block> GRINDITE_SHARD = BLOCKS.register("grindite_shard", () -> new ShardBlock());
	public static final RegistryObject<Block> TURNITE_SHARD = BLOCKS.register("turnite_shard", () -> new ShardBlock());
	public static final RegistryObject<Block> SOULITE_SHARD = BLOCKS.register("soulite_shard", () -> new ShardBlock());

	public static final RegistryObject<Block> PHAT_CRYSTAL = BLOCKS.register("phat_crystal", () -> new ShardBlock());

	public static final RegistryObject<Block> NIAZITE_BLOCK = BLOCKS.register("niazite_block",
			() -> new DropSelfBlock(AbstractBlock.Properties.create(Material.IRON, MaterialColor.IRON).setRequiresTool()
					.hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)));
	public static final RegistryObject<Block> IDLITE_BLOCK = BLOCKS.register("idlite_block",
			() -> new DropSelfBlock(AbstractBlock.Properties.create(Material.IRON, MaterialColor.IRON).setRequiresTool()
					.hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)));
	public static final RegistryObject<Block> THALAMITE_BLOCK = BLOCKS.register("thalamite_block",
			() -> new DropSelfBlock(AbstractBlock.Properties.create(Material.IRON, MaterialColor.IRON).setRequiresTool()
					.hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)));
	public static final RegistryObject<Block> DIGINITE_BLOCK = BLOCKS.register("diginite_block",
			() -> new DropSelfBlock(AbstractBlock.Properties.create(Material.IRON, MaterialColor.IRON).setRequiresTool()
					.hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)));
	public static final RegistryObject<Block> BLOCITE_BLOCK = BLOCKS.register("blocite_block",
			() -> new DropSelfBlock(AbstractBlock.Properties.create(Material.IRON, MaterialColor.IRON).setRequiresTool()
					.hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)));
	public static final RegistryObject<Block> GRINDITE_BLOCK = BLOCKS.register("grindite_block",
			() -> new DropSelfBlock(AbstractBlock.Properties.create(Material.IRON, MaterialColor.IRON).setRequiresTool()
					.hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)));
	public static final RegistryObject<Block> TURNITE_BLOCK = BLOCKS.register("turnite_block",
			() -> new DropSelfBlock(AbstractBlock.Properties.create(Material.IRON, MaterialColor.IRON).setRequiresTool()
					.hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)));
	public static final RegistryObject<Block> SOULITE_BLOCK = BLOCKS.register("soulite_block",
			() -> new DropSelfBlock(AbstractBlock.Properties.create(Material.IRON, MaterialColor.IRON).setRequiresTool()
					.hardnessAndResistance(5.0F, 6.0F).sound(SoundType.METAL)));

	public static Boolean neverAllowSpawn(BlockState state, IBlockReader reader, BlockPos pos, EntityType<?> entity) {
		return (boolean) false;
	}

}
