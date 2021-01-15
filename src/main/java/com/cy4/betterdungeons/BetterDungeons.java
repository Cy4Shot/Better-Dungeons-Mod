package com.cy4.betterdungeons;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.cy4.betterdungeons.core.init.BlockInit;
import com.cy4.betterdungeons.core.init.ConfiguredStructuresInit;
import com.cy4.betterdungeons.core.init.ContainerTypesInit;
import com.cy4.betterdungeons.core.init.DimensionInit;
import com.cy4.betterdungeons.core.init.ItemInit;
import com.cy4.betterdungeons.core.init.StructureInit;
import com.cy4.betterdungeons.core.init.TileEntityTypesInit;
import com.cy4.betterdungeons.core.itemgroup.BetterDungeonsItemGroup;
import com.cy4.betterdungeons.core.network.DungeonsNetwork;
import com.cy4.betterdungeons.core.network.data.PlayerDungeonData;

import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

@Mod("betterdungeons")
@Mod.EventBusSubscriber(modid = BetterDungeons.MOD_ID, bus = Bus.MOD)
public class BetterDungeons {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "betterdungeons";

	public BetterDungeons() {

		// Add mod loading bus
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::clientSetup);
		bus.addListener(this::commonSetup);

		// Register mod
		BlockInit.BLOCKS.register(bus);
		ItemInit.ITEMS.register(bus);
		TileEntityTypesInit.TILE_ENTITY_TYPES.register(bus);
		ContainerTypesInit.CONTAINER_TYPES.register(bus);
		StructureInit.STRUCTURES.register(bus);
		MinecraftForge.EVENT_BUS.register(this);

		// Add listners to event bus
		MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::addDimensionalSpacing);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::onPlayerLoggedIn);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::biomeModification);
	}

	@SubscribeEvent
	public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();
		BlockInit.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block -> {
			registry.register(new BlockItem(block, new Item.Properties().group(BetterDungeonsItemGroup.BETTER_DUNGEONS))
					.setRegistryName(block.getRegistryName()));
		});
	}

//	@SubscribeEvent
//	public static void onStructureRegister(RegistryEvent.Register<Structure<?>> event) {
//		FeatureInit.registerStructureFeatures();
//	}

	public void clientSetup(FMLClientSetupEvent event) {
		DimensionRenderInfo.field_239208_a_.put(DimensionInit.DUNGEON_DIMENSION.getLocation(),
				new DimensionRenderInfo(Float.NaN, false, DimensionRenderInfo.FogType.NONE, false, true) {
					@Override
					public Vector3d func_230494_a_(Vector3d vector3d, float sun) {
						return vector3d;
					}

					@Override
					public boolean func_230493_a_(int x, int y) {
						return false;
					}
				});
	}

	public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
		ServerWorld serverWorld = player.getServerWorld();
		MinecraftServer server = player.getServer();
		PlayerDungeonData.get(serverWorld).getDungeonStats(player).sync(server);
	}

	public void commonSetup(FMLCommonSetupEvent event) {
		DungeonsConfig.register();
		event.enqueueWork(() -> {
			StructureInit.setupStructures();
			ConfiguredStructuresInit.registerConfiguredStructures();
		});
		DungeonsNetwork.initialize();
	}

	public void biomeModification(final BiomeLoadingEvent event) {
		if (event.getName() == null)
			return;

		RegistryKey<Biome> biomeKey = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, event.getName());
		if (biomeKey != Biomes.THE_VOID)
			return;
		event.getGeneration().getStructures().add(() -> ConfiguredStructuresInit.CONFIGURED_DUNGEON);
	}

	@SuppressWarnings("resource")
	public void addDimensionalSpacing(final WorldEvent.Load event) {
		if (event.getWorld() instanceof ServerWorld) {
			ServerWorld serverWorld = (ServerWorld) event.getWorld();

			/*
			 * Prevent spawning our structure in Vanilla's superflat world as people seem to
			 * want their superflat worlds free of modded structures. Also that vanilla
			 * superflat is really tricky and buggy to work with in my experience.
			 */
			if (serverWorld.getChunkProvider().getChunkGenerator() instanceof FlatChunkGenerator
					&& serverWorld.getDimensionKey().equals(World.OVERWORLD)) {
				return;
			}

			if (!serverWorld.getDimensionKey().equals(DimensionInit.DUNGEON_WORLD))
				return;

			Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(
					serverWorld.getChunkProvider().generator.func_235957_b_().func_236195_a_());
			tempMap.put(StructureInit.DUNGEON.get(),
					DimensionStructuresSettings.field_236191_b_.get(StructureInit.DUNGEON.get()));
			serverWorld.getChunkProvider().generator.func_235957_b_().field_236193_d_ = tempMap;
		}
	}

}
