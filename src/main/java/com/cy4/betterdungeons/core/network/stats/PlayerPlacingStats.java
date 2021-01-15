package com.cy4.betterdungeons.core.network.stats;

import java.util.UUID;

import net.minecraft.nbt.CompoundNBT;

public class PlayerPlacingStats {
	private final UUID player;
	private boolean canPlace = true;
	
	public PlayerPlacingStats(UUID uuid) {
        this.player = uuid;
    }

    public PlayerPlacingStats(UUID uuid, boolean canPlaceAltar) {
        this.player = uuid;
        this.canPlace = canPlaceAltar;
    }
    
    public static PlayerPlacingStats deserialize(CompoundNBT nbt) {
        UUID player = nbt.getUniqueId("player");
        boolean canPlace = nbt.getBoolean("canPlace");
        return new PlayerPlacingStats(player, canPlace);
    }

    public static CompoundNBT serialize(PlayerPlacingStats stats) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putUniqueId("player", stats.getPlayer());
        nbt.putBoolean("canPlace", stats.canPlace);
        return nbt;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public boolean canPlace() {
    	return this.canPlace;
    }
}
