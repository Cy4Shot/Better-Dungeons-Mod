package com.cy4.betterdungeons.core.network.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.core.network.stats.PlayerDungeonStats;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class PlayerDungeonData extends WorldSavedData {

    protected static final String DATA_NAME = BetterDungeons.MOD_ID + "_PlayerDungeonLevels";

    private Map<UUID, PlayerDungeonStats> playerMap = new HashMap<>();

    public PlayerDungeonData() {
        super(DATA_NAME);
    }

    public PlayerDungeonData(String name) {
        super(name);
    }

    public PlayerDungeonStats getDungeonStats(PlayerEntity player) {
        return getDungeonStats(player.getUniqueID());
    }

    public PlayerDungeonStats getDungeonStats(UUID uuid) {
        return this.playerMap.computeIfAbsent(uuid, PlayerDungeonStats::new);
    }

    /* ------------------------------- */

    public PlayerDungeonData setDungeonLevel(ServerPlayerEntity player, int level) {
        this.getDungeonStats(player).setDungeonLevel(player.getServer(), level);

        markDirty();
        return this;
    }

    public PlayerDungeonData addDungeonExp(ServerPlayerEntity player, int exp) {
        this.getDungeonStats(player).addDungeonExp(player.getServer(), exp);

        markDirty();
        return this;
    }

    public PlayerDungeonData reset(ServerPlayerEntity player) {
        this.getDungeonStats(player).reset(player.getServer());

        markDirty();
        return this;
    }

    /* ------------------------------- */

    @Override
    public void read(CompoundNBT nbt) {
        ListNBT playerList = nbt.getList("PlayerEntries", Constants.NBT.TAG_STRING);
        ListNBT statEntries = nbt.getList("StatEntries", Constants.NBT.TAG_COMPOUND);

        if (playerList.size() != statEntries.size()) {
            throw new IllegalStateException("Map doesn't have the same amount of keys as values");
        }

        for (int i = 0; i < playerList.size(); i++) {
            UUID playerUUID = UUID.fromString(playerList.getString(i));
            this.getDungeonStats(playerUUID).deserializeNBT(statEntries.getCompound(i));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        ListNBT playerList = new ListNBT();
        ListNBT statsList = new ListNBT();

        this.playerMap.forEach((uuid, stats) -> {
            playerList.add(StringNBT.valueOf(uuid.toString()));
            statsList.add(stats.serializeNBT());
        });

        nbt.put("PlayerEntries", playerList);
        nbt.put("StatEntries", statsList);

        return nbt;
    }

    public static PlayerDungeonData get(ServerWorld world) {
        return world.getServer().func_241755_D_()
                .getSavedData().getOrCreate(PlayerDungeonData::new, DATA_NAME);
    }

}