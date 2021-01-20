package com.cy4.betterdungeons.core.network;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.core.network.message.DungeonRunTickMessage;
import com.cy4.betterdungeons.core.network.message.DungeonsLevelMessage;
import com.cy4.betterdungeons.core.network.message.OpenUpgradeMenuMessage;
import com.cy4.betterdungeons.core.network.message.RewardMessage;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class DungeonsNetwork {

	private static final String NETWORK_VERSION = "0.19.0";

	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(BetterDungeons.MOD_ID, "network"),
			() -> NETWORK_VERSION, version -> version.equals(NETWORK_VERSION), // Client acceptance predicate
			version -> version.equals(NETWORK_VERSION) // Server acceptance predicate
	);

	public static void initialize() {
		CHANNEL.registerMessage(0, DungeonsLevelMessage.class, DungeonsLevelMessage::encode, DungeonsLevelMessage::decode,
				DungeonsLevelMessage::handle);

		CHANNEL.registerMessage(1, OpenUpgradeMenuMessage.class, OpenUpgradeMenuMessage::encode, OpenUpgradeMenuMessage::decode,
				OpenUpgradeMenuMessage::handle);
		
		CHANNEL.registerMessage(2, RewardMessage.class, RewardMessage::encode, RewardMessage::decode,
				RewardMessage::handle);
		
		CHANNEL.registerMessage(3, DungeonRunTickMessage.class, DungeonRunTickMessage::encode, DungeonRunTickMessage::decode,
				DungeonRunTickMessage::handle);
	}

}
