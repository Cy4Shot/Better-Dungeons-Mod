package com.cy4.betterdungeons.core.network.message;

import java.util.function.Supplier;

import com.cy4.betterdungeons.client.overlay.DungeonLevelOverlay;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class DungeonsLevelMessage {

	public int dungeonLevel;
	public int dungeonExp, tnl;

	public DungeonsLevelMessage() {
	}

	public DungeonsLevelMessage(int dungeonLevel, int dungeonExp, int tnl) {
		this.dungeonLevel = dungeonLevel;
		this.dungeonExp = dungeonExp;
		this.tnl = tnl;
	}

	public static void encode(DungeonsLevelMessage message, PacketBuffer buffer) {
		buffer.writeInt(message.dungeonLevel);
		buffer.writeInt(message.dungeonExp);
		buffer.writeInt(message.tnl);
	}

	public static DungeonsLevelMessage decode(PacketBuffer buffer) {
		DungeonsLevelMessage message = new DungeonsLevelMessage();
		message.dungeonLevel = buffer.readInt();
		message.dungeonExp = buffer.readInt();
		message.tnl = buffer.readInt();
		return message;
	}

	public static void handle(DungeonsLevelMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			if (message.dungeonLevel != DungeonLevelOverlay.dungeonLevel) {
				// Levelled up!
			}
			DungeonLevelOverlay.dungeonLevel = message.dungeonLevel;
			DungeonLevelOverlay.dungeonExp = message.dungeonExp;
			DungeonLevelOverlay.tnl = message.tnl;

			DungeonLevelOverlay.expGainedAnimation.reset();
			DungeonLevelOverlay.expGainedAnimation.play();
		});
		context.setPacketHandled(true);
	}

}
