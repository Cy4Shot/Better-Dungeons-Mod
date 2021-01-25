package com.cy4.betterdungeons.common.event;

import com.cy4.betterdungeons.client.ter.model.TreeModel;
import com.cy4.betterdungeons.common.compat.SoilCompatibility;

import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CompatRegistryEvents {

//	@SubscribeEvent(priority = EventPriority.LOW)
//	public void startServer(AddReloadListenerEvent  event) {
//		event.addListener((IFutureReloadListener) (stage, manager, p1, p2, e1, e2) -> {
//			SoilCompatibility.INSTANCE.update(Minecraft.getm);
//		});
//	}

	@SubscribeEvent
	public void recipesUpdated(RecipesUpdatedEvent event) {
		SoilCompatibility.INSTANCE.update(event.getRecipeManager().getRecipes());
		TreeModel.init();
	}

}
