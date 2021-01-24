package com.cy4.betterdungeons.core.network.message;

import java.util.function.Supplier;

import com.cy4.betterdungeons.common.upgrade.UpgradeGroup;
import com.cy4.betterdungeons.common.upgrade.UpgradeNode;
import com.cy4.betterdungeons.common.upgrade.UpgradeTree;
import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.cy4.betterdungeons.core.network.data.PlayerUpgradeData;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

public class PlayerUpgradeMessage {

	public String upgradeName;

	public PlayerUpgradeMessage() {
	}

	public PlayerUpgradeMessage(String upgradeName) {
		this.upgradeName = upgradeName;
	}

	public static void encode(PlayerUpgradeMessage message, PacketBuffer buffer) {
		buffer.writeString(message.upgradeName, 32767);
	}

	public static PlayerUpgradeMessage decode(PacketBuffer buffer) {
		PlayerUpgradeMessage message = new PlayerUpgradeMessage();
		message.upgradeName = buffer.readString(32767);
		return message;
	}

	public static void handle(PlayerUpgradeMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			ServerPlayerEntity sender = context.getSender();

			if (sender == null)
				return;

			UpgradeGroup<?> upgradeGroup = DungeonsConfig.UPGRADES.getByName(message.upgradeName);

			PlayerUpgradeData abilitiesData = PlayerUpgradeData.get((ServerWorld) sender.world);
			UpgradeTree upgradeTree = abilitiesData.getUpgrades(sender);

			if (DungeonsConfig.UPGRADE_GATES.getGates().isLocked(upgradeGroup, upgradeTree))
				return;

			UpgradeNode<?> upgradeNode = upgradeTree.getNodeByName(message.upgradeName);

			if (upgradeNode.getLevel() >= upgradeGroup.getMaxLevel())
				return;

			abilitiesData.upgradeUpgrade(sender, upgradeNode);
		});
		context.setPacketHandled(true);
	}

}
