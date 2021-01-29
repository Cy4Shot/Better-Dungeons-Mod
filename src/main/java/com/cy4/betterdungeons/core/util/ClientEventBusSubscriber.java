package com.cy4.betterdungeons.core.util;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.client.entity.PhatSlimeRenderer;
import com.cy4.betterdungeons.client.overlay.AbilitiesOverlay;
import com.cy4.betterdungeons.client.overlay.DungeonLevelOverlay;
import com.cy4.betterdungeons.client.screen.DungeonMerchantScreen;
import com.cy4.betterdungeons.client.screen.KeyGeneratorScreen;
import com.cy4.betterdungeons.client.screen.PlayerRewardScreen;
import com.cy4.betterdungeons.client.screen.UpgradeTreeScreen;
import com.cy4.betterdungeons.client.ter.BonsaiPotTileEntityRenderer;
import com.cy4.betterdungeons.client.ter.KeyCreationTableRenderer;
import com.cy4.betterdungeons.core.init.BlockInit;
import com.cy4.betterdungeons.core.init.ContainerTypesInit;
import com.cy4.betterdungeons.core.init.EntityTypesInit;
import com.cy4.betterdungeons.core.init.KeybindInit;
import com.cy4.betterdungeons.core.init.TileEntityTypesInit;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BetterDungeons.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		// Keybindings
		KeybindInit.register(event);

		// Overlays
		MinecraftForge.EVENT_BUS.register(DungeonLevelOverlay.class);
		MinecraftForge.EVENT_BUS.register(AbilitiesOverlay.class);

		// Screens
		ScreenManager.registerFactory(ContainerTypesInit.KEY_GENERATOR_CONTAINER.get(), KeyGeneratorScreen::new);
		ScreenManager.registerFactory(ContainerTypesInit.UPGRADE_CONTAINER.get(), PlayerRewardScreen::new);
		ScreenManager.registerFactory(ContainerTypesInit.DUNGEON_MERCHANT_CONTAINER.get(), DungeonMerchantScreen::new);
		ScreenManager.registerFactory(ContainerTypesInit.UPGRADE_TREE_CONTAINER.get(), UpgradeTreeScreen::new);
		
		//Entities
		RenderingRegistry.registerEntityRenderingHandler(EntityTypesInit.PHAT_SLIME.get(), PhatSlimeRenderer::new);

		// Render Layers
		RenderTypeLookup.setRenderLayer(BlockInit.DUNGEON_PORTAL.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(BlockInit.KEY_CREATION_TABLE.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(BlockInit.KEY_GENERATOR.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(BlockInit.NIAZITE_SHARD.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(BlockInit.IDLITE_SHARD.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(BlockInit.THALAMITE_SHARD.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(BlockInit.DIGINITE_SHARD.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(BlockInit.BLOCITE_SHARD.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(BlockInit.GRINDITE_SHARD.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(BlockInit.TURNITE_SHARD.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(BlockInit.SOULITE_SHARD.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(BlockInit.PHAT_CRYSTAL.get(), RenderType.getCutout());

		// TERs
		ClientRegistry.bindTileEntityRenderer(TileEntityTypesInit.KEY_CREATION_TABLE_TILE_ENTITY_TYPE.get(), KeyCreationTableRenderer::new);
		ClientRegistry.bindTileEntityRenderer(TileEntityTypesInit.BONSAI_POT_TILE_ENTITY_TYPE.get(), BonsaiPotTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(TileEntityTypesInit.HOPPING_BONSAI_POT_TILE_ENTITY_TYPE.get(),
				BonsaiPotTileEntityRenderer::new);
	}
}
