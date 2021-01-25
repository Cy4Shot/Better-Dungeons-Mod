package com.cy4.betterdungeons.core.init;

import com.cy4.betterdungeons.common.recipe.sapling.SaplingInfo;
import com.cy4.betterdungeons.common.recipe.sapling.SaplingRecipeHelper;
import com.cy4.betterdungeons.common.recipe.soil.SoilInfo;
import com.cy4.betterdungeons.common.recipe.soil.SoilRecipeHelper;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;

public class RecipesInit {
	
	public static IRecipeType<SoilInfo> soilRecipeType;
    public static IRecipeSerializer<SoilInfo> soilRecipeSerializer;
    public static SoilRecipeHelper soilRecipeHelper;

    public static IRecipeType<SaplingInfo> saplingRecipeType;
    public static IRecipeSerializer<SaplingInfo> saplingRecipeSerializer;
    public static SaplingRecipeHelper saplingRecipeHelper;

}
