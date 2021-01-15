package com.cy4.betterdungeons.common.world.gen.feature;

import java.util.List;

import com.cy4.betterdungeons.BetterDungeons;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;

import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class DungeonStructure extends Structure<NoFeatureConfig> {

//	public DungeonStructure(Codec<DungeonStructure.Config> codec) {
//		super(codec);
//	}
//
//	@Override
//	public IStartFactory<DungeonStructure.Config> getStartFactory() {
//		return (p_242778_1_, p_242778_2_, p_242778_3_, p_242778_4_, p_242778_5_, p_242778_6_) -> new Start(this,
//				p_242778_2_, p_242778_3_, p_242778_4_, p_242778_5_, p_242778_6_);
//	}
//
//	@Override
//	public Decoration getDecorationStage() {
//		return Decoration.UNDERGROUND_STRUCTURES;
//	}
//
//	public static class Start extends MarginedStructureStart<DungeonStructure.Config> {
//		@SuppressWarnings("unused")
//		private final DungeonStructure structure;
//
//		public Start(DungeonStructure structure, int chunkX, int chunkZ, MutableBoundingBox box, int references,
//				long worldSeed) {
//			super(structure, chunkX, chunkZ, box, references, worldSeed);
//			this.structure = structure;
//		}
//
//		public void func_230364_a_(DynamicRegistries registry, ChunkGenerator gen, TemplateManager manager, int chunkX,
//				int chunkZ, Biome biome, DungeonStructure.Config config) {
//			BetterDungeons.LOGGER.debug(chunkX + ", " + chunkZ);
//			BlockPos blockpos = new BlockPos(chunkX * 16, 128, chunkZ * 16);
//			DungeonStructure.Pools.init();
//			JigsawGenerator.func_242837_a(registry, config.toVillageConfig(), AbstractVillagePiece::new, gen, manager,
//					blockpos, this.components, this.rand, false, false);
//			this.recalculateStructureSize();
//
//			BetterDungeons.LOGGER.debug("ATTENTION!    Dungeon at blockpos: " + blockpos.getX() + ", " + blockpos.getY()
//					+ ", " + blockpos.getZ());
//		}
//	}
//
//	public static class Config implements IFeatureConfig {
//		public static final Codec<DungeonStructure.Config> CODEC = RecordCodecBuilder.create(builder -> {
//			return builder.group(
//					JigsawPattern.field_244392_b_.fieldOf("start_pool")
//							.forGetter(DungeonStructure.Config::getStartPool),
//					Codec.intRange(0, Integer.MAX_VALUE).fieldOf("size").forGetter(DungeonStructure.Config::getSize))
//					.apply(builder, DungeonStructure.Config::new);
//		});
//
//		private final Supplier<JigsawPattern> startPool;
//		private final int size;
//
//		public Config(Supplier<JigsawPattern> startPool, int size) {
//			this.startPool = startPool;
//			this.size = size;
//		}
//
//		public int getSize() {
//			return this.size;
//		}
//
//		public Supplier<JigsawPattern> getStartPool() {
//			return this.startPool;
//		}
//
//		public VillageConfig toVillageConfig() {
//			return new VillageConfig(this.getStartPool(), this.getSize());
//		}
//
//	}
//
//	public static class Pools {
//		public static final JigsawPattern START = JigsawPatternRegistry.func_244094_a(new JigsawPattern(
//				new ResourceLocation(BetterDungeons.MOD_ID, "dungeon/starts"), new ResourceLocation("empty"),
//				ImmutableList.of(Pair.of(JigsawPiece.func_242861_b(BetterDungeons.MOD_ID + ":dungeon/rooms/starts",
//						ProcessorLists.field_244101_a), 1)),
//				JigsawPattern.PlacementBehaviour.RIGID));
//
//		public static void init() {
//
//		}
//	}

	public DungeonStructure(Codec<NoFeatureConfig> codec) {
		super(codec);
	}

	/**
	 * This is how the worldgen code knows what to call when it is time to create
	 * the pieces of the structure for generation.
	 */
	@Override
	public IStartFactory<NoFeatureConfig> getStartFactory() {
		return DungeonStructure.Start::new;
	}

	/**
	 * Generation stage for when to generate the structure. there are 10 stages you
	 * can pick from! This surface structure stage places the structure before
	 * plants and ores are generated.
	 */
	@Override
	public GenerationStage.Decoration getDecorationStage() {
		return GenerationStage.Decoration.SURFACE_STRUCTURES;
	}

	/**
	 * || ONLY WORKS IN FORGE 34.1.12+ ||
	 *
	 * This method allows us to have mobs that spawn naturally over time in our
	 * structure. No other mobs will spawn in the structure of the same entity
	 * classification. The reason you want to match the classifications is so that
	 * your structure's mob will contribute to that classification's cap. Otherwise,
	 * it may cause a runaway spawning of the mob that will never stop.
	 *
	 * NOTE: getDefaultSpawnList is for monsters only and
	 * getDefaultCreatureSpawnList is for creatures only. If you want to add
	 * entities of another classification, use the StructureSpawnListGatherEvent to
	 * add water_creatures, water_ambient, ambient, or misc mobs. Use that event to
	 * add/remove mobs from structures that are not your own.
	 */
	private static final List<MobSpawnInfo.Spawners> STRUCTURE_MONSTERS = ImmutableList.of(
			new MobSpawnInfo.Spawners(EntityType.ILLUSIONER, 100, 4, 9),
			new MobSpawnInfo.Spawners(EntityType.VINDICATOR, 100, 4, 9));

	@Override
	public List<MobSpawnInfo.Spawners> getDefaultSpawnList() {
		return STRUCTURE_MONSTERS;
	}

	private static final List<MobSpawnInfo.Spawners> STRUCTURE_CREATURES = ImmutableList.of(
			new MobSpawnInfo.Spawners(EntityType.SHEEP, 30, 10, 15),
			new MobSpawnInfo.Spawners(EntityType.RABBIT, 100, 1, 2));

	@Override
	public List<MobSpawnInfo.Spawners> getDefaultCreatureSpawnList() {
		return STRUCTURE_CREATURES;
	}

	/*
	 * This is where extra checks can be done to determine if the structure can
	 * spawn here. This only needs to be overridden if you're adding additional
	 * spawn conditions.
	 * 
	 * Notice how the biome is also passed in. Though, you are not going to do any
	 * biome checking here as you should've added this structure to the biomes you
	 * wanted already with the biome load event.
	 * 
	 * Basically, this method is used for determining if the land is at a suitable
	 * height, if certain other structures are too close or not, or some other
	 * restrictive condition.
	 *
	 * For example, Pillager Outposts added a check to make sure it cannot spawn
	 * within 10 chunk of a Village. (Bedrock Edition seems to not have the same
	 * check)
	 * 
	 * 
	 * Also, please for the love of god, do not do dimension checking here. If you
	 * do and another mod's dimension is trying to spawn your structure, the locate
	 * command will make minecraft hang forever and break the game.
	 *
	 * Instead, use the addDimensionalSpacing method in StructureTutorialMain class.
	 * If you check for the dimension there and do not add your structure's spacing
	 * into the chunk generator, the structure will not spawn in that dimension!
	 */
//    @Override
//    protected boolean func_230363_a_(ChunkGenerator chunkGenerator, BiomeProvider biomeSource, long seed, SharedSeedRandom chunkRandom, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos, NoFeatureConfig featureConfig) {
//        int landHeight = chunkGenerator.getNoiseHeight(chunkX << 4, chunkZ << 4, Heightmap.Type.WORLD_SURFACE_WG);
//        return landHeight > 100;
//    }

	/**
	 * Handles calling up the structure's pieces class and height that structure
	 * will spawn at.
	 */
	public static class Start extends StructureStart<NoFeatureConfig> {
		public Start(Structure<NoFeatureConfig> structureIn, int chunkX, int chunkZ,
				MutableBoundingBox mutableBoundingBox, int referenceIn, long seedIn) {
			super(structureIn, chunkX, chunkZ, mutableBoundingBox, referenceIn, seedIn);
		}

		@Override
		public void func_230364_a_(DynamicRegistries dynamicRegistryManager, ChunkGenerator chunkGenerator,
				TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, NoFeatureConfig config) {

			// Turns the chunk coordinates into actual coordinates we can use. (Gets center
			// of that chunk)
			int x = (chunkX << 4) + 7;
			int z = (chunkZ << 4) + 7;
			BlockPos blockpos = new BlockPos(x, 0, z);

			// All a structure has to do is call this method to turn it into a jigsaw based
			// structure!
			JigsawManager.func_242837_a(dynamicRegistryManager,
					new VillageConfig(() -> dynamicRegistryManager.getRegistry(Registry.JIGSAW_POOL_KEY)
							// The path to the starting Template Pool JSON file to read.
							//
							// Note, this is "structure_tutorial:run_down_house/start_pool" which means
							// the game will automatically look into the following path for the template
							// pool:
							// "resources/data/structure_tutorial/worldgen/template_pool/run_down_house/start_pool.json"
							// This is why your pool files must be in
							// "data/<modid>/worldgen/template_pool/<the path to the pool here>"
							// because the game automatically will check in worldgen/template_pool for the
							// pools.
							.getOrDefault(new ResourceLocation(BetterDungeons.MOD_ID, "dungeon/start_pool")),

							// How many pieces outward from center can a recursive jigsaw structure spawn.
							// Our structure is only 1 block out and isn't recursive so any value of 1 or
							// more doesn't change anything.
							// However, I recommend you keep this a high value so people can use datapacks
							// to add additional pieces to your structure easily.
							50),
					AbstractVillagePiece::new, chunkGenerator, templateManagerIn, blockpos, // Position of the
																							// structure. Y value is
																							// ignored if last parameter
																							// is set to true.
					this.components, // The list that will be populated with the jigsaw pieces after this method.
					this.rand, true, // Allow intersecting jigsaw pieces. If false, villages cannot generate houses.
										// I recommend to keep this to true.
					true); // Place at heightmap (top land). Set this to false for structure to be place at
							// blockpos's y value instead

			// Right here, you can do interesting stuff with the pieces in this.components
			// such as offset the
			// center piece by 50 blocks up for no reason, remove repeats of a piece or add
			// a new piece so
			// only 1 of that piece exists, etc. But you do not have access to the piece's
			// blocks as this list
			// holds just the piece's size and positions. Blocks will be placed later in
			// JigsawManager.
			//
			// In this case, we offset the pieces up 1 so that the doorstep is not lower
			// than the original
			// terrain and then we extend the bounding box down by 1 to force down the land
			// by 1 block that the
			// Structure.field_236384_t_ field will place at bottom of the house. By lifting
			// the house up by 1 and
			// lowering the bounding box, the land at bottom of house will now stay in place
			// instead of also being
			// raise by 1 block because the land is based on the bounding box itself.
			this.components.forEach(piece -> piece.offset(0, 1, 0));
			this.components.forEach(piece -> piece.getBoundingBox().minY -= 1);

			// Sets the bounds of the structure once you are finished.
			this.recalculateStructureSize();

			// I use to debug and quickly find out if the structure is spawning or not and
			// where it is.
			BetterDungeons.LOGGER
					.debug("Dungeon House at " + (blockpos.getX()) + " " + blockpos.getY() + " " + (blockpos.getZ()));
		}

	}
}
