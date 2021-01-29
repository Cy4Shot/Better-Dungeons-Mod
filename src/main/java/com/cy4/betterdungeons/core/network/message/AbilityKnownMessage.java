package com.cy4.betterdungeons.core.network.message;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import com.cy4.betterdungeons.client.overlay.AbilitiesOverlay;
import com.cy4.betterdungeons.common.upgrade.UpgradeNode;
import com.cy4.betterdungeons.common.upgrade.UpgradeTree;
import com.cy4.betterdungeons.common.upgrade.type.ability.PlayerAbility;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkEvent;

public class AbilityKnownMessage {

	public List<UpgradeNode<?>> learnedAbilities;

	public AbilityKnownMessage() {
	}

	public AbilityKnownMessage(UpgradeTree abilityTree) {
		this(abilityTree.learnedAbilityNodes());
	}

	public AbilityKnownMessage(List<UpgradeNode<?>> learnedAbilities) {
		this.learnedAbilities = learnedAbilities;
	}

	public static void encode(AbilityKnownMessage message, PacketBuffer buffer) {
		CompoundNBT nbt = new CompoundNBT();
		ListNBT abilities = new ListNBT();
		message.learnedAbilities.stream().map(UpgradeNode::serializeNBT).forEach(abilities::add);
		nbt.put("LearnedAbilities", abilities);
		buffer.writeCompoundTag(nbt);
	}

	public static AbilityKnownMessage decode(PacketBuffer buffer) {
		AbilityKnownMessage message = new AbilityKnownMessage();
		message.learnedAbilities = new LinkedList<>();
		CompoundNBT nbt = buffer.readCompoundTag();
		ListNBT learnedAbilities = nbt.getList("LearnedAbilities", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < learnedAbilities.size(); i++) {
			message.learnedAbilities.add(UpgradeNode.fromNBT(learnedAbilities.getCompound(i), PlayerAbility.class));
		}
		return message;
	}

	public static void handle(AbilityKnownMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			System.out.println("Received tree! " + message.learnedAbilities.size());
			AbilitiesOverlay.learnedAbilities = message.learnedAbilities;
		});
		context.setPacketHandled(true);
	}

}
