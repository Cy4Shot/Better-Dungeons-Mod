package com.cy4.betterdungeons.core.network.stats;

import java.util.UUID;

import net.minecraft.nbt.CompoundNBT;

public class PlayerTimeStats {
	
	private final UUID player;
	private long time = 0;
	
	public PlayerTimeStats(UUID uuid) {
        this.player = uuid;
    }

    public PlayerTimeStats(UUID uuid, long time) {
        this.player = uuid;
        this.time = time;
    }
    
    public static PlayerTimeStats deserialize(CompoundNBT nbt) {
        UUID player = nbt.getUniqueId("player");
        long canPlace = nbt.getLong("time");
        return new PlayerTimeStats(player, canPlace);
    }

    public static CompoundNBT serialize(PlayerTimeStats stats) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putUniqueId("player", stats.getPlayer());
        nbt.putLong("time", stats.time);
        return nbt;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public long getTime() {
    	return this.time;
    }
    
    public long setTime(long time) {
    	return this.time = time;
    }

}
