package com.cy4.betterdungeons.common.upgrade;

import com.cy4.betterdungeons.common.upgrade.type.PlayerUpgrade;
import com.cy4.betterdungeons.core.config.DungeonsConfig;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class UpgradeNode<T extends PlayerUpgrade> implements INBTSerializable<CompoundNBT> {

    private UpgradeGroup<T> group;
    private int level;

    public UpgradeNode(UpgradeGroup<T> group, int level) {
        this.group = group;
        this.level = level;
    }

    public UpgradeGroup<T> getGroup() {
        return this.group;
    }

    public int getLevel() {
        return this.level;
    }

    public T getUpgrade() {
        if (!isLearned()) return null;
        return this.getGroup().getUpgrade(this.getLevel());
    }

    public String getName() {
        return this.getGroup().getName(this.getLevel());
    }
    
    public String getNextName() {
        return this.getGroup().getName(this.getLevel() + 1);
    }

    public boolean isLearned() {
        return this.level != 0;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("Name", this.getGroup().getParentName());
        nbt.putInt("Level", this.getLevel());
        return nbt;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deserializeNBT(CompoundNBT nbt) {
        String groupName = nbt.getString("Name");
        this.group = (UpgradeGroup<T>) DungeonsConfig.UPGRADES.getByName(groupName);
        this.level = nbt.getInt("Level");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        UpgradeNode<?> that = (UpgradeNode<?>) other;

        return this.level == that.level &&
                this.group.getParentName().equals(that.group.getParentName());
    }

    /* ----------------------------------------- */

    @SuppressWarnings("unchecked")
	public static <T extends PlayerUpgrade> UpgradeNode<T> fromNBT(CompoundNBT nbt, Class<T> clazz) {
        UpgradeGroup<T> group = (UpgradeGroup<T>) DungeonsConfig.UPGRADES.getByName(nbt.getString("Name"));
        int level = nbt.getInt("Level");
        return new UpgradeNode<>(group, level);
    }

}