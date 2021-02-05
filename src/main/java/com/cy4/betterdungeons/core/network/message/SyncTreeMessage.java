package com.cy4.betterdungeons.core.network.message;

import java.util.function.Supplier;

import com.cy4.betterdungeons.client.screen.UpgradeTreeScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncTreeMessage {

	public SyncTreeMessage() {
	}

	public static void encode(SyncTreeMessage message, PacketBuffer buffer) {
	}

	public static SyncTreeMessage decode(PacketBuffer buffer) {
		return new SyncTreeMessage();
	}

	@SuppressWarnings("resource")
	public static void handle(SyncTreeMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			if (Minecraft.getInstance().currentScreen instanceof UpgradeTreeScreen) {
				((UpgradeTreeScreen)(Minecraft.getInstance().currentScreen)).getUpgradeDialog().refreshWidgets();
			}
		});
		context.setPacketHandled(true);
	}

}
