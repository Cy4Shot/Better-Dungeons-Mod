package com.cy4.betterdungeons.core.network;

import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

public class NetcodeUtils {

	public static boolean runIfPresent(MinecraftServer server, UUID uuid, Consumer<ServerPlayerEntity> action) {
		if (server == null)
			return false;

		ServerPlayerEntity player = server.getPlayerList().getPlayerByUUID(uuid);

		if (player == null)
			return false;

		action.accept(player);

		return true;
	}

}
