package com.cy4.betterdungeons.core.network.message;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

import com.cy4.betterdungeons.common.event.CraftingEvents;
import com.cy4.betterdungeons.common.upgrade.UpgradeTree;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpgradeTreeMessage {

	public UpgradeTree researchTree;
	public UUID playerUUID;

	public UpgradeTreeMessage() {
	}

	public UpgradeTreeMessage(UpgradeTree researchTree, UUID playerUUID) {
		this.researchTree = researchTree;
		this.playerUUID = playerUUID;
	}

	public static void encode(UpgradeTreeMessage message, PacketBuffer buffer) {
		buffer.writeUniqueId(message.playerUUID);
		buffer.writeCompoundTag(message.researchTree.serializeNBT());
	}

	public static UpgradeTreeMessage decode(PacketBuffer buffer) {
		UpgradeTreeMessage message = new UpgradeTreeMessage();
		message.researchTree = new UpgradeTree(buffer.readUniqueId());
		message.researchTree.deserializeNBT(Objects.requireNonNull(buffer.readCompoundTag()));
		return message;
	}

	public static void handle(UpgradeTreeMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			CraftingEvents.RESEARCH_TREE = message.researchTree;
		});
		context.setPacketHandled(true);
	}

}
