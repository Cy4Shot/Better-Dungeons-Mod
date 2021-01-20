package com.cy4.betterdungeons.core.network.message;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class DungeonRunTickMessage {
	
	 public int remainingTicks;

	    public DungeonRunTickMessage() { }

	    public DungeonRunTickMessage(int remainingTicks) {
	        this.remainingTicks = remainingTicks;
	    }

	    public static void encode(DungeonRunTickMessage message, PacketBuffer buffer) {
	        buffer.writeInt(message.remainingTicks);
	    }

	    public static DungeonRunTickMessage decode(PacketBuffer buffer) {
	        DungeonRunTickMessage message = new DungeonRunTickMessage();
	        message.remainingTicks = buffer.readInt();
	        return message;
	    }

	    public static void handle(DungeonRunTickMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
	        NetworkEvent.Context context = contextSupplier.get();
	        context.enqueueWork(() -> {
	            // Display time remaining here
	        });
	        context.setPacketHandled(true);
	    }

}
