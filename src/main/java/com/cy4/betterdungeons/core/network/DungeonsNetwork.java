package com.cy4.betterdungeons.core.network;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.core.network.message.AbilityActivityMessage;
import com.cy4.betterdungeons.core.network.message.AbilityFocusedMessage;
import com.cy4.betterdungeons.core.network.message.AbilityKnownMessage;
import com.cy4.betterdungeons.core.network.message.DungeonRunTickMessage;
import com.cy4.betterdungeons.core.network.message.DungeonsLevelMessage;
import com.cy4.betterdungeons.core.network.message.MerchantUIMessage;
import com.cy4.betterdungeons.core.network.message.OpenUpgradeMenuMessage;
import com.cy4.betterdungeons.core.network.message.OpenUpgradeTreeMessage;
import com.cy4.betterdungeons.core.network.message.PlayerUpgradeMessage;
import com.cy4.betterdungeons.core.network.message.RewardMessage;
import com.cy4.betterdungeons.core.network.message.UpgradeKeyMessage;
import com.cy4.betterdungeons.core.network.message.UpgradeTreeMessage;

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

		CHANNEL.registerMessage(2, RewardMessage.class, RewardMessage::encode, RewardMessage::decode, RewardMessage::handle);

		CHANNEL.registerMessage(3, DungeonRunTickMessage.class, DungeonRunTickMessage::encode, DungeonRunTickMessage::decode,
				DungeonRunTickMessage::handle);

		CHANNEL.registerMessage(4, MerchantUIMessage.class, MerchantUIMessage::encode, MerchantUIMessage::decode,
				MerchantUIMessage::handle);

		CHANNEL.registerMessage(5, PlayerUpgradeMessage.class, PlayerUpgradeMessage::encode, PlayerUpgradeMessage::decode,
				PlayerUpgradeMessage::handle);

		CHANNEL.registerMessage(6, OpenUpgradeTreeMessage.class, OpenUpgradeTreeMessage::encode, OpenUpgradeTreeMessage::decode,
				OpenUpgradeTreeMessage::handle);

		CHANNEL.registerMessage(7, UpgradeKeyMessage.class, UpgradeKeyMessage::encode, UpgradeKeyMessage::decode,
				UpgradeKeyMessage::handle);

		CHANNEL.registerMessage(8, AbilityActivityMessage.class, AbilityActivityMessage::encode, AbilityActivityMessage::decode,
				AbilityActivityMessage::handle);

		CHANNEL.registerMessage(9, AbilityFocusedMessage.class, AbilityFocusedMessage::encode, AbilityFocusedMessage::decode,
				AbilityFocusedMessage::handle);

		CHANNEL.registerMessage(10, AbilityKnownMessage.class, AbilityKnownMessage::encode, AbilityKnownMessage::decode,
				AbilityKnownMessage::handle);
		
		CHANNEL.registerMessage(11, UpgradeTreeMessage.class, UpgradeTreeMessage::encode, UpgradeTreeMessage::decode,
				UpgradeTreeMessage::handle);
	}

}
