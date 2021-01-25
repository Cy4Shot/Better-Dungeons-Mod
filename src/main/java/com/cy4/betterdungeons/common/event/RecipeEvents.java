package com.cy4.betterdungeons.common.event;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.recipe.RecipeHelper;
import com.cy4.betterdungeons.common.recipe.sapling.SaplingRecipeHelper;
import com.cy4.betterdungeons.common.recipe.sapling.SaplingSerializer;
import com.cy4.betterdungeons.common.recipe.soil.SoilRecipeHelper;
import com.cy4.betterdungeons.common.recipe.soil.SoilSerializer;
import com.cy4.betterdungeons.core.init.RecipesInit;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RecipeEvents {

	@SubscribeEvent
	public static void onRecipeRegistry(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
		IForgeRegistry<IRecipeSerializer<?>> registry = event.getRegistry();

		RecipesInit.saplingRecipeType = RecipeHelper.registerRecipeType(new ResourceLocation(BetterDungeons.MOD_ID, "sapling"));
		RecipesInit.saplingRecipeSerializer = new SaplingSerializer();
		RecipesInit.saplingRecipeHelper = new SaplingRecipeHelper();
		registry.register(RecipesInit.saplingRecipeSerializer);

		RecipesInit.soilRecipeType = RecipeHelper.registerRecipeType(new ResourceLocation(BetterDungeons.MOD_ID, "soil"));
		RecipesInit.soilRecipeSerializer = new SoilSerializer();
		RecipesInit.soilRecipeHelper = new SoilRecipeHelper();
		registry.register(RecipesInit.soilRecipeSerializer);
	}
}