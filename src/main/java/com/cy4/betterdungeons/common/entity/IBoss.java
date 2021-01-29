package com.cy4.betterdungeons.common.entity;

import com.cy4.betterdungeons.core.network.stats.DungeonRun;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;

public interface IBoss {
	void spawnInTheWorld(DungeonRun run, ServerWorld world, BlockPos pos);
    ServerBossInfo getServerBossInfo();
}
