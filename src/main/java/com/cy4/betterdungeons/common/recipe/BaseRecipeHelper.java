package com.cy4.betterdungeons.common.recipe;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

public class BaseRecipeHelper<T extends RecipeData> {
    IRecipeType<T> recipeType;

    public BaseRecipeHelper(IRecipeType<T> type) {
        this.recipeType = type;
    }

    public boolean hasRecipes(RecipeManager manager) {
        Map<ResourceLocation, IRecipe<?>> recipes = getRecipes(manager);
        return recipes != null && recipes.size() > 0;
    }

    public int getRecipeCount(RecipeManager manager) {
        Map<ResourceLocation, IRecipe<?>> recipes = getRecipes(manager);
        return recipes != null ? recipes.size() : 0;
    }

    @SuppressWarnings("unchecked")
	public T getRecipe(RecipeManager manager, ResourceLocation id) {
        Map<ResourceLocation, IRecipe<?>> recipes = getRecipes(manager);
        if(recipes == null) {
            return null;
        }

        return (T) recipes.getOrDefault(id, null);
    }

    @SuppressWarnings("unchecked")
	public Stream<T> getRecipeStream(RecipeManager manager) {
        return getRecipes(manager).values().stream().map(r -> (T)r);
    }

    public Map<ResourceLocation, IRecipe<?>> getRecipes(RecipeManager manager) {
        return RecipeHelper.getRecipes(manager, recipeType);
    }

    public List<T> getRecipesList(RecipeManager manager) {
        return getRecipeStream(manager).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
	public T getRandomRecipe(RecipeManager manager, Random rand) {
        Map<ResourceLocation, IRecipe<?>> recipes = getRecipes(manager);
        if(recipes == null || recipes.size() == 0) {
            return null;
        }
        Set<ResourceLocation> ids = recipes.keySet();
        ResourceLocation randomId = (ResourceLocation) ids.toArray()[rand.nextInt(ids.size())];
        return (T) recipes.get(randomId);

    }

}