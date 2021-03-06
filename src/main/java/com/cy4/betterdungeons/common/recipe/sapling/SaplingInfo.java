package com.cy4.betterdungeons.common.recipe.sapling;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.cy4.betterdungeons.common.recipe.RecipeData;
import com.cy4.betterdungeons.core.init.RecipesInit;
import com.cy4.betterdungeons.core.util.json.GsonHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class SaplingInfo extends RecipeData {
    private final ResourceLocation id;

    public Ingredient ingredient;
    public int baseTicks;

    public ItemStack sapling;
    public ArrayList<SaplingDrop> drops;
    public Set<String> tags;

    public SaplingInfo(ResourceLocation id, Ingredient ingredient, int baseTicks) {
        this.id = id;
        this.ingredient = ingredient;
        this.baseTicks = baseTicks;
        this.drops = new ArrayList<>();
        this.tags = new HashSet<>();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipesInit.saplingRecipeSerializer;
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipesInit.saplingRecipeType;
    }

    public int getRequiredTicks() {
        return baseTicks;
    }

    public void addDrop(SaplingDrop drop) {
        this.drops.add(drop);
        this.drops.sort((a, b) -> (int)(b.chance*1000) - (int)(a.chance*1000));
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public boolean isValidTag(String tag) {
        return this.tags.contains(tag);
    }

    public List<ItemStack> getRandomizedDrops(Random rand) {
        ArrayList<ItemStack> result = new ArrayList<>();
        for(SaplingDrop drop : this.drops) {
            ItemStack dropStack = drop.getRandomDrop(rand);
            if(dropStack.isEmpty()) {
                continue;
            }

            result.add(dropStack);
        }

        return result;
    }

    public String serializePretty() {
        JsonObject result = new JsonObject();
        result.addProperty("type", "betterdungeons:sapling");

        JsonObject saplingObj = new JsonObject();
        saplingObj.addProperty("item", this.sapling.getItem().getRegistryName().toString());
        result.add("sapling", saplingObj);

        JsonArray drops = new JsonArray();
        for(SaplingDrop drop : this.drops) {
            JsonObject itemObj = new JsonObject();
            itemObj.addProperty("item", drop.resultStack.getItem().getRegistryName().toString());

            JsonObject dropObj = new JsonObject();
            dropObj.add("result", itemObj);
            dropObj.addProperty("rolls", drop.rolls);
            dropObj.addProperty("chance", drop.chance / 100);
            drops.add(dropObj);
        }
        result.add("drops", drops);

        JsonArray soilTags = new JsonArray();
        tags.forEach(soilTags::add);
        result.add("compatibleSoilTags", soilTags);

        return GsonHelper.GSON.toJson(result);
    }
}