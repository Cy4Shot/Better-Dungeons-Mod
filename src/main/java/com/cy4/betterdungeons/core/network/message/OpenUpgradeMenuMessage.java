package com.cy4.betterdungeons.core.network.message;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.cy4.betterdungeons.common.container.UpgradeContainer;
import com.cy4.betterdungeons.common.upgrade.UpgradeTree;
import com.cy4.betterdungeons.core.network.data.PlayerUpgradeData;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

public class OpenUpgradeMenuMessage {

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

            if (sender == null) return;
            PlayerUpgradeData playerUpgradeData = PlayerUpgradeData.get((ServerWorld) sender.world);
            UpgradeTree upgradeTree = playerUpgradeData.getUpgrades(sender);

            NetworkHooks.openGui(
                    sender,
                    new INamedContainerProvider() {
                        @Override
                        public ITextComponent getDisplayName() {
                            return new TranslationTextComponent("container.betterdungeons.skill_tree");
                        }

                        @Nullable
                        @Override
                        public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                            return new UpgradeContainer(i, upgradeTree);
                        }
                    },
                    (buffer) -> {
                        buffer.writeCompoundTag(upgradeTree.serializeNBT());
                    }
            );
        });
        context.setPacketHandled(true);
	}
}
