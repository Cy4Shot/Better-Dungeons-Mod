package com.cy4.betterdungeons.common.recipe.sapling;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.core.util.json.MCJsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class SaplingSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SaplingInfo> {
    public SaplingSerializer() {
        this.setRegistryName(new ResourceLocation(BetterDungeons.MOD_ID, "sapling"));
    }

    private Marker mark = MarkerManager.getMarker("Serializer");

    private boolean isValidIngredient(JsonObject obj) {
        if(obj == null) {
            return false;
        }
        Item item = MCJsonUtils.getItem(obj, "item");
        if(item.getRegistryName().toString().equals("minecraft:air")) {
            return false;
        }

        return true;
    }

    @SuppressWarnings("unused")
	@Override
    public SaplingInfo read(ResourceLocation recipeId, JsonObject json) {
        if(!isValidIngredient(json.getAsJsonObject("sapling"))) {
            BetterDungeons.LOGGER.info(mark, "Skipping recipe '{}', contains unknown sapling.", recipeId);
            return null;
        }

        final Ingredient sapling = Ingredient.deserialize(json.getAsJsonObject("sapling"));

        int baseTicks = 200;
        if(json.has("ticks")) {
            baseTicks = json.get("ticks").getAsInt();
        }

        SaplingInfo result = new SaplingInfo(recipeId, sapling, baseTicks);
        if(json.has("drops")) {
            JsonArray dropsJson = json.getAsJsonArray("drops");
            for(JsonElement element : dropsJson) {
                if(!element.isJsonObject()) {
                    continue;
                }

                JsonObject dropObj = element.getAsJsonObject();
                if(!isValidIngredient(dropObj.getAsJsonObject("result"))) {
                	BetterDungeons.LOGGER.info(mark, "Skipping recipe '{}', contains unknown drop.", recipeId);
                    return null;
                }

                SaplingDrop drop = new SaplingDrop(element.getAsJsonObject());
                if(drop == null) {
                    continue;
                }

                result.addDrop(drop);
            }
        }

        if(json.has("compatibleSoilTags")) {
            JsonArray tagsJson = json.getAsJsonArray("compatibleSoilTags");
            for(JsonElement element : tagsJson) {
                if(!element.isJsonPrimitive()) {
                    continue;
                }

                String tag = element.getAsString();
                if(tag == null) {
                    continue;
                }

                result.addTag(tag);
            }
        }

        return result;
    }

    @Nullable
    @Override
    public SaplingInfo read(ResourceLocation recipeId, PacketBuffer buffer) {
        final Ingredient ingredient = Ingredient.read(buffer);
        final int baseTicks = buffer.readInt();

        SaplingInfo result = new SaplingInfo(recipeId, ingredient, baseTicks);

        final int dropCount = buffer.readInt();
        for(int i = 0; i < dropCount; i++) {
            result.addDrop(new SaplingDrop(buffer));
        }

        final int tagCount = buffer.readInt();
        for(int i = 0; i < tagCount; i++) {
            result.addTag(buffer.readString());
        }

        return result;
    }

    @Override
    public void write(PacketBuffer buffer, SaplingInfo sapling) {
        sapling.ingredient.write(buffer);
        buffer.writeInt(sapling.baseTicks);
        buffer.writeInt(sapling.drops.size());
        for(SaplingDrop drop : sapling.drops) {
            drop.write(buffer);
        }
        buffer.writeInt(sapling.tags.size());
        for(String tag : sapling.tags) {
            buffer.writeString(tag);
        }
    }
}