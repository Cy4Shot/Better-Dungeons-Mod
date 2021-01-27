package com.cy4.betterdungeons.core.network.message;

import java.util.function.Supplier;

import com.cy4.betterdungeons.common.upgrade.UpgradeTree;
import com.cy4.betterdungeons.core.network.data.PlayerUpgradeData;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpgradeKeyMessage {

	public boolean keyUp;
	public boolean keyDown;
	public boolean scrollUp;
	public boolean scrollDown;
	public boolean shouldCancelDown;
	public int abilityIndex = -1;

	public UpgradeKeyMessage() {
	}

	public UpgradeKeyMessage(boolean keyUp, boolean keyDown, boolean scrollUp, boolean scrollDown) {
		this.keyUp = keyUp;
		this.keyDown = keyDown;
		this.scrollUp = scrollUp;
		this.scrollDown = scrollDown;
	}

	public UpgradeKeyMessage(boolean shouldCancelDown) {
		this.shouldCancelDown = shouldCancelDown;
	}

	public UpgradeKeyMessage(int selectAbilityIndex) {
		this.abilityIndex = selectAbilityIndex;
	}

	public static void encode(UpgradeKeyMessage message, PacketBuffer buffer) {
		buffer.writeBoolean(message.keyUp);
		buffer.writeBoolean(message.keyDown);
		buffer.writeBoolean(message.scrollUp);
		buffer.writeBoolean(message.scrollDown);
		buffer.writeBoolean(message.shouldCancelDown);
		buffer.writeInt(message.abilityIndex);
	}

	public static UpgradeKeyMessage decode(PacketBuffer buffer) {
		UpgradeKeyMessage message = new UpgradeKeyMessage();
		message.keyUp = buffer.readBoolean();
		message.keyDown = buffer.readBoolean();
		message.scrollUp = buffer.readBoolean();
		message.scrollDown = buffer.readBoolean();
		message.shouldCancelDown = buffer.readBoolean();
		message.abilityIndex = buffer.readInt();
		return message;
	}

	public static void handle(UpgradeKeyMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			ServerPlayerEntity sender = context.getSender();

			if (sender == null)
				return;

			PlayerUpgradeData abilitiesData = PlayerUpgradeData.get((ServerWorld) sender.world);
			UpgradeTree abilityTree = abilitiesData.getUpgrades(sender);

			if (message.scrollUp) {
				abilityTree.scrollUp(sender.server);
			} else if (message.scrollDown) {
				abilityTree.scrollDown(sender.server);
			} else if (message.keyUp) {
				abilityTree.keyUp(sender.server);
			} else if (message.keyDown) {
				abilityTree.keyDown(sender.server);
			} else if (message.shouldCancelDown) {
				abilityTree.cancelKeyDown(sender.server);
			} else if (message.abilityIndex != -1) {
				abilityTree.quickSelectUpgrade(sender.server, message.abilityIndex);
			}
		});
		context.setPacketHandled(true);
	}

}
