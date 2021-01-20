package com.cy4.betterdungeons;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cy4.betterdungeons.common.command.CommandInit;
import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.cy4.betterdungeons.core.init.BlockInit;
import com.cy4.betterdungeons.core.init.ConfiguredStructuresInit;
import com.cy4.betterdungeons.core.init.ContainerTypesInit;
import com.cy4.betterdungeons.core.init.FeatureInit;
import com.cy4.betterdungeons.core.init.ItemInit;
import com.cy4.betterdungeons.core.init.StructureInit;
import com.cy4.betterdungeons.core.init.TileEntityTypesInit;
import com.cy4.betterdungeons.core.itemgroup.BetterDungeonsItemGroup;
import com.cy4.betterdungeons.core.network.DungeonsNetwork;
import com.cy4.betterdungeons.core.network.data.PlayerDungeonData;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
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
		bus.addListener(this::commonSetup);

		// Register mod
		BlockInit.BLOCKS.register(bus);
		ItemInit.ITEMS.register(bus);
		TileEntityTypesInit.TILE_ENTITY_TYPES.register(bus);
		ContainerTypesInit.CONTAINER_TYPES.register(bus);
		StructureInit.STRUCTURES.register(bus);
		MinecraftForge.EVENT_BUS.register(this);

		// Add listners to event bus
		MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::onPlayerLoggedIn);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onBiomeLoad);
	}

	@SubscribeEvent
	public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();
		BlockInit.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block -> {
			registry.register(new BlockItem(block, new Item.Properties().group(BetterDungeonsItemGroup.BETTER_DUNGEONS))
					.setRegistryName(block.getRegistryName()));
		});
	}

	@SubscribeEvent
	public static void onFeatureRegister(RegistryEvent.Register<Feature<?>> event) {
		FeatureInit.registerFeatures(event);
	}

	public void onBiomeLoad(BiomeLoadingEvent event) {
		if (event.getName().equals(new ResourceLocation(MOD_ID, "dungeon"))) {
			event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, FeatureInit.BREADCRUMB_CHEST);
		}
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

	@SubscribeEvent
	public void onCommandsRegister(RegisterCommandsEvent event) {
		CommandInit.registerCommands(event);
	}

}
