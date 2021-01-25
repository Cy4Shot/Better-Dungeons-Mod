package com.cy4.betterdungeons.core.util.nbt;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.client.ter.model.MultiblockBlockModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class MultiBlockModelSerializer implements JsonDeserializer<MultiblockBlockModel> {

	@Override
	public MultiblockBlockModel deserialize(JsonElement root, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if (!root.isJsonObject()) {
			return null;
		}

		JsonObject rootObj = root.getAsJsonObject();

		int version = rootObj.has("version") ? rootObj.get("version").getAsInt() : 1;
		switch (version) {
		case 1: {
			BetterDungeons.LOGGER.warn("Invalid version in shape file: '%s', skipping shape! Shape files below v3 are not supported!",
					rootObj.get("version"));
			return null;
		}
		case 2: {
			BetterDungeons.LOGGER.warn("Invalid version in shape file: '%s', skipping shape! Shape files below v3 are not supported!",
					rootObj.get("version"));
			return null;
		}
		case 3: {
			return deserializeV3(rootObj, typeOfT, context);
		}
		}

		BetterDungeons.LOGGER.warn("Invalid version in shape file: '%s', skipping shape! Maybe the shape file is from a newer mod version?",
				rootObj.get("version"));
		return null;
	}

	private Map<String, BlockState> getReferenceMapV3(JsonObject jsonRefMap) {
		Map<String, BlockState> refMap = new HashMap<>();
		for (Map.Entry<String, JsonElement> jsonRefEntry : jsonRefMap.entrySet()) {
			JsonObject jsonBlockInfo = jsonRefEntry.getValue().getAsJsonObject();
			BlockState state = BlockStateSerializationHelper.deserializeBlockState(jsonBlockInfo);
			refMap.put(jsonRefEntry.getKey(), state);
		}

		return refMap;
	}

	private boolean hasUnknownBlocksInMap(JsonObject jsonRefMap) {
		for (Map.Entry<String, JsonElement> jsonRefEntry : jsonRefMap.entrySet()) {
			JsonObject jsonBlockInfo = jsonRefEntry.getValue().getAsJsonObject();
			if (!BlockStateSerializationHelper.isValidBlockState(jsonBlockInfo)) {
				return true;
			}
		}

		return false;
	}

	public MultiblockBlockModel deserializeV3(JsonObject root, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		// First get the name of the tree type
		if (!root.has("type")) {
			BetterDungeons.LOGGER.warn("Missing type name in multiblockmodel json");
			return null;
		}

		ResourceLocation treeType = new ResourceLocation(root.get("type").getAsString());

		if (hasUnknownBlocksInMap(root.getAsJsonObject("ref"))) {
			// Logz.warn("Unknown blocks in multiblockmodel json");
			return null;
		}

		// Get the reference map
		Map<String, BlockState> refMap = getReferenceMapV3(root.getAsJsonObject("ref"));

		// And use it to build the actual block map
		Map<BlockPos, BlockState> blocks = new HashMap<>();
		JsonArray jsonBlocks = root.getAsJsonArray("shape");

		int x = jsonBlocks.size() - 1;
		for (JsonElement zSliceElement : jsonBlocks) {
			int y = zSliceElement.getAsJsonArray().size() - 1;
			for (JsonElement ySliceElement : zSliceElement.getAsJsonArray()) {
				for (int z = 0; z < ySliceElement.getAsString().length(); z++) {
					String ref = ySliceElement.getAsString().charAt(z) + "";
					if (ref.equals(" ")) {
						continue;
					}

					if (!refMap.containsKey(ref)) {
						BetterDungeons.LOGGER.warn("Shape-Entry is using an unknown block reference '%s'! Skipping shape!", ref);
						return null;
					}

					blocks.put(new BlockPos(x, y, z), refMap.get(ref));
				}

				y--;
			}

			x--;
		}

		MultiblockBlockModel result = new MultiblockBlockModel(treeType);
		result.setBlocks(blocks);
		return result;
	}
}