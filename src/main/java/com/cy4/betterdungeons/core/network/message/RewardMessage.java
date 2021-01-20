package com.cy4.betterdungeons.core.network.message;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.network.NetworkEvent;

public class RewardMessage {

	public Block reward;

	public RewardMessage() {
	}

	public RewardMessage(Block reward) {
		this.reward = reward;
	}

	@SuppressWarnings("deprecation")
	public static void encode(RewardMessage message, PacketBuffer buffer) {
		buffer.writeInt(Registry.BLOCK.getId(message.reward));
	}

	@SuppressWarnings("deprecation")
	public static RewardMessage decode(PacketBuffer buffer) {
		RewardMessage message = new RewardMessage();
		message.reward = Registry.BLOCK.getByValue(buffer.readInt());
		return message;
	}

	public static void handle(RewardMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			ServerPlayerEntity sender = context.getSender();

			if (sender == null)
				return;

			sender.addItemStackToInventory(new ItemStack(message.reward));
		});
		context.setPacketHandled(true);
	}

}
