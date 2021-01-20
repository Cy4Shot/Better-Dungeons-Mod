package com.cy4.betterdungeons.common.world.gen;

import java.util.function.Supplier;

import com.cy4.betterdungeons.BetterDungeons;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.MarginedStructureStart;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.ProcessorLists;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class DungeonStructure extends Structure<DungeonStructure.Config> {

	public DungeonStructure(Codec<DungeonStructure.Config> codec) {
		super(codec);
	}

	@Override
	public IStartFactory<DungeonStructure.Config> getStartFactory() {
		return (p_242778_1_, p_242778_2_, p_242778_3_, p_242778_4_, p_242778_5_, p_242778_6_) -> new Start(this,
				p_242778_2_, p_242778_3_, p_242778_4_, p_242778_5_, p_242778_6_);
	}

	@Override
	public Decoration getDecorationStage() {
		return Decoration.UNDERGROUND_STRUCTURES;
	}

	public static class Start extends MarginedStructureStart<DungeonStructure.Config> {
		@SuppressWarnings("unused")
		private final DungeonStructure structure;

		public Start(DungeonStructure structure, int chunkX, int chunkZ, MutableBoundingBox box, int references,
				long worldSeed) {
			super(structure, chunkX, chunkZ, box, references, worldSeed);
			this.structure = structure;
		}

		public void func_230364_a_(DynamicRegistries registry, ChunkGenerator gen, TemplateManager manager, int chunkX,
				int chunkZ, Biome biome, DungeonStructure.Config config) {
			BetterDungeons.LOGGER.debug(chunkX + ", " + chunkZ);
			BlockPos blockpos = new BlockPos(chunkX * 16, 128, chunkZ * 16);
			DungeonStructure.Pools.init();
			JigsawGenerator.func_242837_a(registry, config.toVillageConfig(), AbstractVillagePiece::new, gen, manager,
					blockpos, this.components, this.rand, false, false);
			this.recalculateStructureSize();

			BetterDungeons.LOGGER.debug("ATTENTION!    Dungeon at blockpos: " + blockpos.getX() + ", " + blockpos.getY()
					+ ", " + blockpos.getZ());
		}
	}

	public static class Config implements IFeatureConfig {
		public static final Codec<DungeonStructure.Config> CODEC = RecordCodecBuilder.create(builder -> {
			return builder.group(
					JigsawPattern.field_244392_b_.fieldOf("start_pool")
							.forGetter(DungeonStructure.Config::getStartPool),
					Codec.intRange(0, Integer.MAX_VALUE).fieldOf("size").forGetter(DungeonStructure.Config::getSize))
					.apply(builder, DungeonStructure.Config::new);
		});

		private final Supplier<JigsawPattern> startPool;
		private final int size;

		public Config(Supplier<JigsawPattern> startPool, int size) {
			this.startPool = startPool;
			this.size = size;
		}

		public int getSize() {
			return this.size;
		}

		public Supplier<JigsawPattern> getStartPool() {
			return this.startPool;
		}

		public VillageConfig toVillageConfig() {
			return new VillageConfig(this.getStartPool(), this.getSize());
		}

	}

	public static class Pools {
		public static final JigsawPattern START = JigsawPatternRegistry.func_244094_a(new JigsawPattern(
				new ResourceLocation(BetterDungeons.MOD_ID, "dungeon/starts"), new ResourceLocation("empty"),
				ImmutableList.of(Pair.of(JigsawPiece.func_242861_b(BetterDungeons.MOD_ID + ":dungeon/rooms/start1",
						ProcessorLists.field_244101_a), 1)),
				JigsawPattern.PlacementBehaviour.RIGID));

		public static void init() {

		}
	}
}
