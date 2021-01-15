package com.cy4.betterdungeons.core.network.stats;

import java.util.UUID;

import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.cy4.betterdungeons.core.network.DungeonsNetwork;
import com.cy4.betterdungeons.core.network.NetcodeUtils;
import com.cy4.betterdungeons.core.network.message.DungeonsLevelMessage;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.NetworkDirection;

public class PlayerDungeonStats implements INBTSerializable<CompoundNBT> {

	private final UUID uuid;
	private int dungeonLevel;
	private int exp;

	public PlayerDungeonStats(UUID uuid) {
		this.uuid = uuid;
	}

	public int getDungeonLevel() {
		return dungeonLevel;
	}

	public int getExp() {
		return exp;
	}

	public int getTnl() {
		return DungeonsConfig.LEVELS_META.getLevelMeta(this.dungeonLevel).tnl;
	}

	/* --------------------------------------- */

	public PlayerDungeonStats setDungeonLevel(MinecraftServer server, int level) {
		this.dungeonLevel = level;
		this.exp = 0;
		sync(server);

		return this;
	}
	
	public PlayerDungeonStats reset(MinecraftServer server) {
        this.dungeonLevel = 0;
        this.exp = 0;

        sync(server);

        return this;
    }

	public PlayerDungeonStats addDungeonExp(MinecraftServer server, int exp) {
		int tnl;
		this.exp += exp;

		int initialLevel = this.dungeonLevel;

		while (this.exp >= (tnl = getTnl())) {
			this.dungeonLevel++;
			this.exp -= tnl; // Carry extra exp to next level!
		}

		if (this.dungeonLevel > initialLevel) {
//            NetcodeUtils.runIfPresent(server, uuid, this::fancyLevelUpEffects);
		}

		sync(server);

		return this;
	}

	public void sync(MinecraftServer server) {
		NetcodeUtils.runIfPresent(server, this.uuid, player -> {
			DungeonsNetwork.CHANNEL.sendTo(
					new DungeonsLevelMessage(this.dungeonLevel, this.exp, this.getTnl()),
					player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		});
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("dungeonLevel", dungeonLevel);
		nbt.putInt("exp", exp);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.dungeonLevel = nbt.getInt("dungeonLevel");
		this.exp = nbt.getInt("exp");
	}
}