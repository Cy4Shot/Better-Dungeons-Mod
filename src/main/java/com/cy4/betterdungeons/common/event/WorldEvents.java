package com.cy4.betterdungeons.common.event;

import com.cy4.betterdungeons.core.init.DimensionInit;
import com.cy4.betterdungeons.core.network.data.DungeonRunData;

import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
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
}
