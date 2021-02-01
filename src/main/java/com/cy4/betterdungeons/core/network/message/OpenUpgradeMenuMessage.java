package com.cy4.betterdungeons.core.network.message;

import java.util.Random;
import java.util.function.Supplier;

import com.cy4.betterdungeons.core.init.BlockInit;
import com.cy4.betterdungeons.core.init.SoundInit;

import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.network.NetworkEvent;

public class OpenUpgradeMenuMessage {
	
	public static final Block[] REWARDS = new Block[] { BlockInit.NIAZITE_SHARD.get(), BlockInit.IDLITE_SHARD.get(),
			BlockInit.THALAMITE_SHARD.get(), BlockInit.BLOCITE_SHARD.get(), BlockInit.GRINDITE_SHARD.get(), BlockInit.DIGINITE_SHARD.get(),
			BlockInit.TURNITE_SHARD.get(), BlockInit.SOULITE_SHARD.get() };

	public OpenUpgradeMenuMessage() {
	}

	public static void encode(OpenUpgradeMenuMessage message, PacketBuffer buffer) {
	}

	public static OpenUpgradeMenuMessage decode(PacketBuffer buffer) {
		return new OpenUpgradeMenuMessage();
	}

	public static void handle(OpenUpgradeMenuMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			ServerPlayerEntity sender = context.getSender();

			if (sender == null)
				return;
			sender.getEntityWorld().playSound(null, sender.getPositionVec().x, sender.getPositionVec().y, sender.getPositionVec().z,
					SoundInit.LEVEL_UP.get(), SoundCategory.PLAYERS, 1f, 1f);
			
			sender.addItemStackToInventory(new ItemStack(REWARDS[new Random().nextInt(REWARDS.length)]));

			
		});
		context.setPacketHandled(true);
	}
}
