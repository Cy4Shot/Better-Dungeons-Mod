package com.cy4.betterdungeons.common.event;

import com.cy4.betterdungeons.core.init.BlockInit;
import com.cy4.betterdungeons.core.init.DimensionInit;
import com.cy4.betterdungeons.core.network.data.DungeonRunData;
import com.cy4.betterdungeons.core.network.data.PlayerKeyCreationTablePlacingData;
import com.cy4.betterdungeons.core.network.data.PlayerKeyGeneratorPlacingData;
import com.cy4.betterdungeons.core.network.stats.PlayerPlacingStats;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(bus = Bus.FORGE)
public class WorldEvents {

	@SubscribeEvent
	public static void onTick(TickEvent.WorldTickEvent event) {
		if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.START
				&& event.world.getDimensionKey() == DimensionInit.DUNGEON_WORLD) {
			DungeonRunData.get((ServerWorld) event.world).tick((ServerWorld) event.world);
		}
	}

	@SubscribeEvent
	public static void onPlaced(BlockEvent.EntityPlaceEvent event) {
		if (event.getPlacedBlock().getBlock() == BlockInit.KEY_GENERATOR.get()) {
			if (event.getEntity() instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) event.getEntity();
				PlayerKeyGeneratorPlacingData data = PlayerKeyGeneratorPlacingData.get((ServerWorld) event.getWorld());
				PlayerPlacingStats stats = data.getPlaceStats(player);
				if (stats.canPlace()) {
					data.setCanPlace(player, false);
				} else {
					event.setCanceled(true);
					event.setResult(Result.DENY);
				}
			}
		}

		if (event.getPlacedBlock().getBlock() == BlockInit.KEY_CREATION_TABLE.get()) {
			if (event.getEntity() instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) event.getEntity();
				PlayerKeyCreationTablePlacingData data = PlayerKeyCreationTablePlacingData.get((ServerWorld) event.getWorld());
				PlayerPlacingStats stats = data.getPlaceStats(player);
				if (stats.canPlace()) {
					data.setCanPlace(player, false);
				} else {
					event.setCanceled(true);
					event.setResult(Result.DENY);
				}
			}
		}
	}
}
