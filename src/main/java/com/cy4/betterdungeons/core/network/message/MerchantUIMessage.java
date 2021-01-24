package com.cy4.betterdungeons.core.network.message;

import java.util.function.Supplier;

import com.cy4.betterdungeons.common.container.DungeonMerchantContainer;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class MerchantUIMessage {
	public enum Opcode {
		SELECT_TRADE
	}

	public Opcode opcode;
	public CompoundNBT payload;

	public MerchantUIMessage() {
	}

	public static void encode(MerchantUIMessage message, PacketBuffer buffer) {
		buffer.writeInt(message.opcode.ordinal());
		buffer.writeCompoundTag(message.payload);
	}

	public static MerchantUIMessage decode(PacketBuffer buffer) {
		MerchantUIMessage message = new MerchantUIMessage();
		message.opcode = MerchantUIMessage.Opcode.values()[buffer.readInt()];
		message.payload = buffer.readCompoundTag();
		return message;
	}

	public static void handle(MerchantUIMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			if (message.opcode == Opcode.SELECT_TRADE) {
				int index = message.payload.getInt("Index");
				ServerPlayerEntity sender = context.getSender();
				Container openContainer = sender.openContainer;
				if (openContainer instanceof DungeonMerchantContainer) {
					DungeonMerchantContainer vendingMachineContainer = (DungeonMerchantContainer) openContainer;
					vendingMachineContainer.selectTrade(index);
				}
			}
		});
		context.setPacketHandled(true);
	}

	public static MerchantUIMessage selectTrade(int index) {
		MerchantUIMessage message = new MerchantUIMessage();
		message.opcode = Opcode.SELECT_TRADE;
		message.payload = new CompoundNBT();
		message.payload.putInt("Index", index);
		return message;
	}
}
