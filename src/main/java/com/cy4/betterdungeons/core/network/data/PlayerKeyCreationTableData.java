package com.cy4.betterdungeons.core.network.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.recipe.KeyCreationTableRecipe;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class PlayerKeyCreationTableData extends WorldSavedData {

	protected static final String DATA_NAME = BetterDungeons.MOD_ID + "_PlayerAltarRecipes";

	private Map<UUID, KeyCreationTableRecipe> playerMap = new HashMap<>();

	public PlayerKeyCreationTableData() {
		super(DATA_NAME);
	}

	public PlayerKeyCreationTableData(String name) {
		super(name);
	}

	public static PlayerKeyCreationTableData get(ServerWorld world) {
		return world.getServer().func_241755_D_().getSavedData().getOrCreate(PlayerKeyCreationTableData::new,
				DATA_NAME);
	}

	public KeyCreationTableRecipe getRecipe(PlayerEntity player) {
		return this.getRecipe(player.getUniqueID());
	}

	public KeyCreationTableRecipe getRecipe(UUID uuid) {
		return this.playerMap.computeIfAbsent(uuid, KeyCreationTableRecipe::new);
	}

	public Map<UUID, KeyCreationTableRecipe> getRecipes() {
		return this.playerMap;
	}

	/* ---------------------------------------------- */

	public PlayerKeyCreationTableData add(UUID uuid, KeyCreationTableRecipe recipe) {
		this.playerMap.put(uuid, recipe);

		markDirty();
		return this;
	}

	public PlayerKeyCreationTableData remove(UUID uuid) {
		this.playerMap.remove(uuid);

		markDirty();
		return this;
	}

	public PlayerKeyCreationTableData update(UUID id, KeyCreationTableRecipe recipe) {
		this.remove(id);
		this.add(id, recipe);

		markDirty();
		return this;
	}

	@Override
	public void read(CompoundNBT nbt) {
		ListNBT playerList = nbt.getList("PlayerEntries", Constants.NBT.TAG_STRING);
		ListNBT recipeList = nbt.getList("AltarRecipeEntries", Constants.NBT.TAG_COMPOUND);

		if (playerList.size() != recipeList.size()) {
			throw new IllegalStateException("Map doesn't have the same amount of keys as values");
		}

		for (int i = 0; i < playerList.size(); i++) {
			UUID playerUUID = UUID.fromString(playerList.getString(i));
			playerMap.put(playerUUID, KeyCreationTableRecipe.deserialize(recipeList.getCompound(i)));
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		ListNBT playerList = new ListNBT();
		ListNBT recipeList = new ListNBT();

		this.playerMap.forEach((uuid, recipe) -> {
			playerList.add(StringNBT.valueOf(uuid.toString()));
			recipeList.add(KeyCreationTableRecipe.serialize(recipe));
		});

		nbt.put("PlayerEntries", playerList);
		nbt.put("AltarRecipeEntries", recipeList);

		return nbt;
	}

}
