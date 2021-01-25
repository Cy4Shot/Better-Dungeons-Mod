package com.cy4.betterdungeons.common.recipe.soil;

import java.util.HashSet;
import java.util.Set;

import com.cy4.betterdungeons.common.recipe.RecipeData;
import com.cy4.betterdungeons.core.init.RecipesInit;

import net.minecraft.block.BlockState;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class SoilInfo extends RecipeData {
    private final ResourceLocation id;

    public Ingredient ingredient;
    public BlockState renderState;

    public float tickModifier;

    public Set<String> tags;

    public SoilInfo(ResourceLocation id, Ingredient ingredient, BlockState renderState, float tickModifier) {
        this.id = id;
        this.ingredient = ingredient;
        this.renderState = renderState;
        this.tickModifier = tickModifier;
        this.tags = new HashSet<>();
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public boolean isValidTag(String tag) {
        return this.tags.contains(tag);
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipesInit.soilRecipeSerializer;
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipesInit.soilRecipeType;
    }

    public float getTickModifier() {
        return tickModifier;
    }
}