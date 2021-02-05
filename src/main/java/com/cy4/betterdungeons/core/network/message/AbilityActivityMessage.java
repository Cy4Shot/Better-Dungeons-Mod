package com.cy4.betterdungeons.core.network.message;

import java.util.function.Supplier;

import com.cy4.betterdungeons.client.overlay.AbilitiesOverlay;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class AbilityActivityMessage {

	public int abilityIndex;
	public int cooldownTicks;
	public int activeFlag;

	public AbilityActivityMessage() {
	}
	
	public AbilityActivityMessage(int abilityIndex, int cooldownTicks, int activeFlag) {
		this.cooldownTicks = cooldownTicks;
		this.activeFlag = activeFlag;
		this.abilityIndex = abilityIndex;
	}

	public static void encode(AbilityActivityMessage message, PacketBuffer buffer) {
		buffer.writeInt(message.abilityIndex);
		buffer.writeInt(message.cooldownTicks);
		buffer.writeInt(message.activeFlag);
	}

	public static AbilityActivityMessage decode(PacketBuffer buffer) {
		AbilityActivityMessage message = new AbilityActivityMessage();
		message.abilityIndex = buffer.readInt();
		message.cooldownTicks = buffer.readInt();
		message.activeFlag = buffer.readInt();
		return message;
	}

	public static void handle(AbilityActivityMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			AbilitiesOverlay.cooldowns.put(message.abilityIndex, message.cooldownTicks);
			if (message.activeFlag != 0) {
				AbilitiesOverlay.active = message.activeFlag != 1;
			}
		});
		context.setPacketHandled(true);
	}

}
