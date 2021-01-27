package com.cy4.betterdungeons.core.network.message;

import java.util.function.Supplier;

import com.cy4.betterdungeons.client.overlay.AbilitiesOverlay;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class AbilityFocusedMessage {
	public int focusedIndex;

    public AbilityFocusedMessage() { }

    public AbilityFocusedMessage(int focusedIndex) {
        this.focusedIndex = focusedIndex;
    }

    public static void encode(AbilityFocusedMessage message, PacketBuffer buffer) {
        buffer.writeInt(message.focusedIndex);
    }

    public static AbilityFocusedMessage decode(PacketBuffer buffer) {
        AbilityFocusedMessage message = new AbilityFocusedMessage();
        message.focusedIndex = buffer.readInt();
        return message;
    }

    public static void handle(AbilityFocusedMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            AbilitiesOverlay.focusedIndex = message.focusedIndex;
        });
        context.setPacketHandled(true);
    }
}
