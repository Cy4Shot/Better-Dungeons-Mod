package com.cy4.betterdungeons.common.upgrade.type;

import net.minecraft.entity.player.PlayerEntity;

public abstract class PlayerUpgrade {
    public PlayerUpgrade() {
    }

    public void onAdded(PlayerEntity player) { }

    public void tick(PlayerEntity player) { }

    public void onRemoved(PlayerEntity player) { }
}
