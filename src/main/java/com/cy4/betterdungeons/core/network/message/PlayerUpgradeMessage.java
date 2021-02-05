package com.cy4.betterdungeons.core.network.message;

import java.util.function.Supplier;

import com.cy4.betterdungeons.common.upgrade.UpgradeGroup;
import com.cy4.betterdungeons.common.upgrade.UpgradeNode;
import com.cy4.betterdungeons.common.upgrade.UpgradeTree;
import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.cy4.betterdungeons.core.init.ItemInit;
import com.cy4.betterdungeons.core.network.DungeonsNetwork;
import com.cy4.betterdungeons.core.network.NetcodeUtils;
import com.cy4.betterdungeons.core.network.data.PlayerUpgradeData;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class PlayerUpgradeMessage {

	public String upgradeName;
	public int cost;

	public PlayerUpgradeMessage() {
	}

	public PlayerUpgradeMessage(String upgradeName, int c) {
		this.upgradeName = upgradeName;
		this.cost = c;
	}

	public static void encode(PlayerUpgradeMessage message, PacketBuffer buffer) {
		buffer.writeString(message.upgradeName, 32767);
		buffer.writeInt(message.cost);
	}

	public static PlayerUpgradeMessage decode(PacketBuffer buffer) {
		PlayerUpgradeMessage message = new PlayerUpgradeMessage();
		message.upgradeName = buffer.readString(32767);
		message.cost = buffer.readInt();
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
			shrinkOrbs(message.cost, sender);
			sender.openContainer.detectAndSendChanges();
			sender.container.detectAndSendChanges();
			sender.sendContainerToPlayer(sender.container);
			sender.sendContainerToPlayer(sender.openContainer);

			NetcodeUtils.runIfPresent(sender.server, sender.getUniqueID(), player -> {
				DungeonsNetwork.CHANNEL.sendTo(new SyncTreeMessage(), player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
			});
		});

		context.setPacketHandled(true);
	}

	public static void shrinkOrbs(int cost, ServerPlayerEntity sender) {
		int used = 0;
		for (ItemStack stack : sender.inventory.mainInventory) {
			int slot = sender.inventory.getSlotFor(stack);
			if (stack.getItem() == ItemInit.PHAT_ORB.get()) {
				if (stack.getCount() <= cost - used) {
					used += stack.getCount();
					stack.shrink(stack.getCount());
				} else {
					stack.shrink(cost - used);
					used += cost - used;
				}
				if (sender.connection != null)
					sender.connection.sendPacket(new SSetSlotPacket(sender.container.windowId, slot, stack));
			}
			if (used == cost)
				break;
		}
	}

}
