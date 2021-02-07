package com.cy4.betterdungeons.core.network.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.core.network.stats.PlayerTimeStats;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class KeyGeneratorTimeData extends WorldSavedData {

	protected static final String DATA_NAME = BetterDungeons.MOD_ID + "_PlayerKeyGeneratorTimeg";

	private Map<UUID, PlayerTimeStats> playerMap = new HashMap<>();

	public KeyGeneratorTimeData() {
		super(DATA_NAME);
	}

	public KeyGeneratorTimeData(String name) {
		super(name);
	}

	public PlayerTimeStats getPlaceStats(PlayerEntity player) {
		return getPlaceStats(player.getUniqueID());
	}

	public PlayerTimeStats getPlaceStats(UUID uuid) {
		return this.playerMap.computeIfAbsent(uuid, PlayerTimeStats::new);
	}

	@SuppressWarnings("static-access")
	@Override
	public void read(CompoundNBT nbt) {
		ListNBT playerList = nbt.getList("PlayerEntries", Constants.NBT.TAG_STRING);
		ListNBT statEntries = nbt.getList("StatEntries", Constants.NBT.TAG_COMPOUND);

		if (playerList.size() != statEntries.size()) {
			throw new IllegalStateException("Map doesn't have the same amount of keys as values");
		}

		for (int i = 0; i < playerList.size(); i++) {
			UUID playerUUID = UUID.fromString(playerList.getString(i));
			this.getPlaceStats(playerUUID).deserialize(statEntries.getCompound(i));
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		ListNBT playerList = new ListNBT();
		ListNBT statsList = new ListNBT();

		this.playerMap.forEach((uuid, stats) -> {
			playerList.add(StringNBT.valueOf(uuid.toString()));
			statsList.add(stats.serialize(stats));
		});

		nbt.put("PlayerEntries", playerList);
		nbt.put("StatEntries", statsList);

		return nbt;
	}

	public static KeyGeneratorTimeData get(ServerWorld world) {
		return world.getServer().func_241755_D_().getSavedData().getOrCreate(KeyGeneratorTimeData::new, DATA_NAME);
	}

	public KeyGeneratorTimeData setCanPlace(PlayerEntity player, long c) {
		this.playerMap.put(player.getUniqueID(), new PlayerTimeStats(player.getUniqueID(), c));

		markDirty();
		return this;
	}

}