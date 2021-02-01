package com.cy4.betterdungeons.common.upgrade.type.special;

import com.cy4.betterdungeons.common.upgrade.UpgradeNode;
import com.cy4.betterdungeons.common.upgrade.UpgradeTree;
import com.cy4.betterdungeons.common.upgrade.type.PlayerUpgrade;
import com.cy4.betterdungeons.core.network.DungeonsNetwork;
import com.cy4.betterdungeons.core.network.data.PlayerUpgradeData;
import com.cy4.betterdungeons.core.network.message.StepHeightMessage;
import com.google.gson.annotations.Expose;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkDirection;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StepUpgrade extends PlayerUpgrade {

	@Expose
	private final float stepHeightAddend;

	public StepUpgrade(int cost, float stepHeightAddend) {
		super(cost);
		this.stepHeightAddend = stepHeightAddend;
	}

	public float getStepHeightAddend() {
		return this.stepHeightAddend;
	}

	@Override
	public void onAdded(PlayerEntity player) {
		player.stepHeight += this.stepHeightAddend;
		set((ServerPlayerEntity) player, player.stepHeight + this.stepHeightAddend);
	}

	@Override
	public void onRemoved(PlayerEntity player) {
		set((ServerPlayerEntity) player, player.stepHeight - this.stepHeightAddend);
	}

	@SubscribeEvent
	public static void onEntityCreated(EntityJoinWorldEvent event) {
		if (event.getEntity().world.isRemote)
			return;
		if (!(event.getEntity() instanceof PlayerEntity))
			return;

		ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
		UpgradeTree abilities = PlayerUpgradeData.get(player.getServerWorld()).getUpgrades(player);
		float totalStepHeight = 0.0F;

		for (UpgradeNode<?> node : abilities.getNodes()) {
			if (!(node.getUpgrade() instanceof StepUpgrade))
				continue;
			StepUpgrade Upgrade = (StepUpgrade) node.getUpgrade();
			totalStepHeight += Upgrade.getStepHeightAddend();
		}

		if (totalStepHeight != 0.0F) {
			set(player, player.stepHeight + totalStepHeight);
		}
	}

	public static void set(ServerPlayerEntity player, float stepHeight) {
		DungeonsNetwork.CHANNEL.sendTo(new StepHeightMessage(stepHeight), player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		player.stepHeight = stepHeight;
	}

}