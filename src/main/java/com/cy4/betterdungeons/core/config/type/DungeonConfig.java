package com.cy4.betterdungeons.core.config.type;

import java.util.ArrayList;
import java.util.List;

import com.cy4.betterdungeons.core.config.Config;
import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.cy4.betterdungeons.core.init.DimensionInit;
import com.google.gson.annotations.Expose;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DungeonConfig extends Config {
	
	@Expose private int TICK_COUNTER;
	@Expose private List<String> ITEM_BLACKLIST;
	@Expose private List<String> BLOCK_BLACKLIST;
	
	@Override
	public String getName() {
		return "dungeon_general";
	}

	public int getTickCounter() {
		return this.TICK_COUNTER;
	}

	@Override
	protected void reset() {
		this.TICK_COUNTER = 20 * 60 * 25; // 25 Minutes!

		this.ITEM_BLACKLIST = new ArrayList<>();
		this.ITEM_BLACKLIST.add(Items.ENDER_CHEST.getRegistryName().toString());

		this.BLOCK_BLACKLIST = new ArrayList<>();
		this.BLOCK_BLACKLIST.add(Blocks.ENDER_CHEST.getRegistryName().toString());
	}

	@SubscribeEvent
	public static void cancelItemInteraction(PlayerInteractEvent event) {
		if(event.getPlayer().world.getDimensionKey() != DimensionInit.DUNGEON_WORLD)return;

		if(DungeonsConfig.CONFIG.ITEM_BLACKLIST.contains(event.getItemStack().getItem().getRegistryName().toString())) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void cancelBlockInteraction(PlayerInteractEvent event) {
		if(event.getPlayer().world.getDimensionKey() != DimensionInit.DUNGEON_WORLD)return;
		BlockState state = event.getWorld().getBlockState(event.getPos());

		if(DungeonsConfig.CONFIG.BLOCK_BLACKLIST.contains(state.getBlock().getRegistryName().toString())) {
			event.setCanceled(true);
		}
	}

}
