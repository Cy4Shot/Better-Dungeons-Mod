package com.cy4.betterdungeons.common.event;

import com.cy4.betterdungeons.client.ter.model.TreeModel;
import com.cy4.betterdungeons.common.recipe.soil.SoilCompatibility;

import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CompatRegistryEvents {
	@SubscribeEvent
	public void recipesUpdated(RecipesUpdatedEvent event) {
		SoilCompatibility.INSTANCE.update(event.getRecipeManager().getRecipes());
		TreeModel.init();
	}
}
