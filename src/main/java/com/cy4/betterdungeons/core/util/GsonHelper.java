package com.cy4.betterdungeons.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHelper {

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();

}
