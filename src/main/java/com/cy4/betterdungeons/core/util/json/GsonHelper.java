package com.cy4.betterdungeons.core.util.json;

import com.cy4.betterdungeons.client.ter.model.MultiblockBlockModel;
import com.cy4.betterdungeons.core.util.nbt.MultiBlockModelSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHelper {

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization()
			.registerTypeAdapter(MultiblockBlockModel.class, new MultiBlockModelSerializer()).create();

}
