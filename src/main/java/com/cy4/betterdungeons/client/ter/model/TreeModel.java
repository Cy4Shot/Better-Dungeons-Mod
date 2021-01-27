package com.cy4.betterdungeons.client.ter.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.annotation.Nullable;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.core.util.json.GsonHelper;
import com.google.gson.stream.JsonReader;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class TreeModel {
	
	private static HashMap<ResourceLocation, MultiblockBlockModel> treeModels;

    public static void init() {
        Collection<ResourceLocation> resources = Minecraft.getInstance().getResourceManager().getAllResourceLocations("models/tree", p -> p.endsWith(".json"));

        ArrayList<MultiblockBlockModel> models = new ArrayList<>();
        for (ResourceLocation resource : resources) {
            try {
                InputStream is = Minecraft.getInstance().getResourceManager().getResource(resource).getInputStream();
                MultiblockBlockModel model = GsonHelper.GSON.fromJson(new JsonReader(new InputStreamReader(is)), MultiblockBlockModel.class);
                if (model != null) {
                    models.add(model);
                } else {
                	BetterDungeons.LOGGER.warn("Unable to read model from: {}", resource);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        treeModels = new HashMap<>();
        for(MultiblockBlockModel model : models) {
            if(treeModels.containsKey(model.id)) {
                BetterDungeons.LOGGER.warn("Duplicate model for tree: {}.", model.id);
            }

            treeModels.put(model.id, model);
        }
        BetterDungeons.LOGGER.info("Found {} tree models.", models.size());
    }

    @Nullable
    public static MultiblockBlockModel get(ResourceLocation treeId) {
        return treeModels.get(treeId);
    }

}
