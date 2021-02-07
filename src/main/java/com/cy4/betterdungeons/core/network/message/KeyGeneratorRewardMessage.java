package com.cy4.betterdungeons.core.network.message;

import java.util.function.Supplier;

import com.cy4.betterdungeons.core.init.ItemInit;
import com.cy4.betterdungeons.core.network.data.KeyGeneratorTimeData;
import com.cy4.betterdungeons.core.network.stats.PlayerTimeStats;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

public class KeyGeneratorRewardMessage {
	
	public long time;

	public KeyGeneratorRewardMessage() {
	}
	
	public KeyGeneratorRewardMessage(long time) {
		this.time = time;
	}

	public static void encode(KeyGeneratorRewardMessage message, PacketBuffer buffer) {
		buffer.writeLong(message.time);
	}

	public static KeyGeneratorRewardMessage decode(PacketBuffer buffer) {
		return new KeyGeneratorRewardMessage(buffer.readLong());
	}
	
	public static void handle(KeyGeneratorRewardMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			ServerPlayerEntity sender = context.getSender();

			if (sender == null)
				return;

			KeyGeneratorTimeData timeData = KeyGeneratorTimeData.get((ServerWorld) sender.world);
			PlayerTimeStats timeStats = timeData.getPlaceStats(sender);
			sender.inventory.addItemStackToInventory(new ItemStack(ItemInit.DUNGEON_KEY.get()));
			timeStats.setTime(message.time);
		});
		context.setPacketHandled(true);
	}
}
