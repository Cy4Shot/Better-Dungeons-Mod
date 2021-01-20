package com.cy4.betterdungeons.common.upgrade;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.cy4.betterdungeons.common.upgrade.type.PlayerUpgrade;
import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.cy4.betterdungeons.core.network.NetcodeUtils;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class UpgradeTree implements INBTSerializable<CompoundNBT> {

	private final UUID uuid;
	private List<UpgradeNode<?>> nodes = new ArrayList<>();

	public UpgradeTree(UUID uuid) {
		this.uuid = uuid;
		this.add(null, DungeonsConfig.UPGRADES.getAll().stream().map(upgradeGroup -> new UpgradeNode<>(upgradeGroup, 0))
				.toArray(UpgradeNode<?>[]::new));
	}

	public List<UpgradeNode<?>> getNodes() {
		return this.nodes;
	}

	public UpgradeNode<?> getNodeOf(UpgradeGroup<?> upgradeGroup) {
		return getNodeByName(upgradeGroup.getParentName());
	}

	public UpgradeNode<?> getNodeByName(String name) {
		Optional<UpgradeNode<?>> upgradeWrapped = this.nodes.stream().filter(node -> node.getGroup().getParentName().equals(name))
				.findFirst();
		if (!upgradeWrapped.isPresent()) {
			UpgradeNode<?> upgradeNode = new UpgradeNode<>(DungeonsConfig.UPGRADES.getByName(name), 0);
			this.nodes.add(upgradeNode);
			return upgradeNode;
		}
		return upgradeWrapped.get();
	}

	/* ------------------------------------ */

	public UpgradeTree upgradeUpgrade(MinecraftServer server, UpgradeNode<?> upgradeNode) {
		this.remove(server, upgradeNode);

		UpgradeGroup<?> upgradeGroup = DungeonsConfig.UPGRADES.getByName(upgradeNode.getGroup().getParentName());
		UpgradeNode<?> upgradedUpgradeNode = new UpgradeNode<>(upgradeGroup, upgradeNode.getLevel() + 1);
		this.add(server, upgradedUpgradeNode);

		return this;
	}

	/* ------------------------------------ */

	public UpgradeTree add(MinecraftServer server, UpgradeNode<?>... nodes) {
		for (UpgradeNode<?> node : nodes) {
			NetcodeUtils.runIfPresent(server, this.uuid, player -> {
				if (node.isLearned()) {
					node.getUpgrade().onAdded(player);
				}
			});
			this.nodes.add(node);
		}

		return this;
	}

	public UpgradeTree tick(MinecraftServer server) {
		NetcodeUtils.runIfPresent(server, this.uuid, player -> {
			this.nodes.stream().filter(UpgradeNode::isLearned).forEach(node -> node.getUpgrade().tick(player));
		});
		return this;
	}

	public UpgradeTree remove(MinecraftServer server, UpgradeNode<?>... nodes) {
		for (UpgradeNode<?> node : nodes) {
			NetcodeUtils.runIfPresent(server, this.uuid, player -> {
				if (node.isLearned()) {
					node.getUpgrade().onRemoved(player);
				}
			});
			this.nodes.remove(node);
		}

		return this;
	}

	/* ------------------------------------ */

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();

		ListNBT list = new ListNBT();
		this.nodes.stream().map(UpgradeNode::serializeNBT).forEach(list::add);
		nbt.put("Nodes", list);

		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		ListNBT list = nbt.getList("Nodes", Constants.NBT.TAG_COMPOUND);
		this.nodes.clear();
		for (int i = 0; i < list.size(); i++) {
			this.add(null, UpgradeNode.fromNBT(list.getCompound(i), PlayerUpgrade.class));
		}
	}
}
