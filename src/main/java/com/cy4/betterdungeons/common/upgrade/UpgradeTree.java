package com.cy4.betterdungeons.common.upgrade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.cy4.betterdungeons.common.upgrade.type.PlayerUpgrade;
import com.cy4.betterdungeons.common.upgrade.type.Research;
import com.cy4.betterdungeons.common.upgrade.type.ability.PlayerAbility;
import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.cy4.betterdungeons.core.network.DungeonsNetwork;
import com.cy4.betterdungeons.core.network.NetcodeUtils;
import com.cy4.betterdungeons.core.network.message.AbilityActivityMessage;
import com.cy4.betterdungeons.core.network.message.AbilityFocusedMessage;
import com.cy4.betterdungeons.core.network.message.AbilityKnownMessage;
import com.cy4.betterdungeons.core.network.message.UpgradeTreeMessage;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.network.NetworkDirection;

public class UpgradeTree implements INBTSerializable<CompoundNBT> {

	private final UUID uuid;
	private List<UpgradeNode<?>> nodes = new ArrayList<>();
	private HashMap<Integer, Integer> cooldowns = new HashMap<>();
	protected List<String> researchesDone;

	private int focusedUpgradeIndex;
	private boolean active;

	private boolean swappingPerformed;
	private boolean swappingLocked;

	public UpgradeTree(UUID uuid) {
		this.uuid = uuid;
		this.add(null, DungeonsConfig.UPGRADES.getAll().stream().map(upgradeGroup -> new UpgradeNode<>(upgradeGroup, 0))
				.toArray(UpgradeNode<?>[]::new));
		this.researchesDone = new LinkedList<>();
	}

	public List<UpgradeNode<?>> getNodes() {
		return this.nodes;
	}
	
	public UUID getPlayerID() {
		return this.uuid;
	}

	public UpgradeNode<?> getFocusedUpgrade() {
		List<UpgradeNode<?>> learnedNodes = learnedAbilityNodes();
		if (learnedNodes.size() == 0)
			return null;
		return learnedNodes.get(focusedUpgradeIndex);
	}

	public boolean isActive() {
		return active;
	}

	public void setSwappingLocked(boolean swappingLocked) {
		this.swappingLocked = swappingLocked;
	}

	public UpgradeNode<?> getNodeOf(UpgradeGroup<?> upgradeGroup) {
		return getNodeByName(upgradeGroup.getParentName());
	}

	public List<UpgradeNode<?>> learnedNodes() {
		return nodes.stream().filter(UpgradeNode::isLearned).collect(Collectors.toList());
	}

	public List<UpgradeNode<?>> learnedAbilityNodes() {
		return nodes.stream().filter(UpgradeNode::isLearned).filter(UpgradeNode::isAbility).collect(Collectors.toList());
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

	public UpgradeTree scrollUp(MinecraftServer server) {
		List<UpgradeNode<?>> learnedNodes = learnedAbilityNodes();

		if (swappingLocked)
			return this;

		if (learnedNodes.size() != 0) {
			boolean prevActive = this.active;
			this.active = false;

			UpgradeNode<?> previouslyFocused = getFocusedUpgrade();
			NetcodeUtils.runIfPresent(server, this.uuid, player -> {
				if (prevActive && ((PlayerAbility) previouslyFocused.getUpgrade()).getBehavior() == PlayerAbility.Behavior.PRESS_TO_TOGGLE)
					((PlayerAbility) previouslyFocused.getUpgrade()).onAction(player, this.active);
			});

			if (prevActive && ((PlayerAbility) getFocusedUpgrade().getUpgrade()).getBehavior() != PlayerAbility.Behavior.HOLD_TO_ACTIVATE)
				putOnCooldown(server, focusedUpgradeIndex, ((PlayerAbility) getFocusedUpgrade().getUpgrade()).getCooldown());

			this.focusedUpgradeIndex++;
			if (this.focusedUpgradeIndex >= learnedNodes.size())
				this.focusedUpgradeIndex -= learnedNodes.size();

			swappingPerformed = true;
			syncFocusedIndex(server);
			notifyActivity(server);
		}

		return this;
	}

	public void putOnCooldown(MinecraftServer server, int abilityIndex, int cooldownTicks) {
		this.cooldowns.put(abilityIndex, cooldownTicks);
		notifyActivity(server, abilityIndex, cooldownTicks, 0);
	}

	public UpgradeTree scrollDown(MinecraftServer server) {
		List<UpgradeNode<?>> learnedNodes = learnedAbilityNodes();

		if (swappingLocked)
			return this;

		if (learnedNodes.size() != 0) {
			boolean prevActive = this.active;
			this.active = false;

			UpgradeNode<?> previouslyFocused = getFocusedUpgrade();
			NetcodeUtils.runIfPresent(server, this.uuid, player -> {
				if (prevActive && ((PlayerAbility) previouslyFocused.getUpgrade()).getBehavior() == PlayerAbility.Behavior.PRESS_TO_TOGGLE)
					((PlayerAbility) previouslyFocused.getUpgrade()).onAction(player, this.active);
			});

			if (prevActive && ((PlayerAbility) getFocusedUpgrade().getUpgrade()).getBehavior() != PlayerAbility.Behavior.HOLD_TO_ACTIVATE)
				putOnCooldown(server, focusedUpgradeIndex, ((PlayerAbility) getFocusedUpgrade().getUpgrade()).getCooldown());

			this.focusedUpgradeIndex--;
			if (this.focusedUpgradeIndex < 0)
				this.focusedUpgradeIndex += learnedNodes.size();

			swappingPerformed = true;
			syncFocusedIndex(server);
			notifyActivity(server);
		}

		return this;
	}

	public void keyDown(MinecraftServer server) {
		UpgradeNode<?> focusedUpgrade = getFocusedUpgrade();

		if (focusedUpgrade == null)
			return;

		PlayerAbility.Behavior behavior = ((PlayerAbility) focusedUpgrade.getUpgrade()).getBehavior();

		if (behavior == PlayerAbility.Behavior.HOLD_TO_ACTIVATE) {
			active = true;
			NetcodeUtils.runIfPresent(server, this.uuid, player -> {
				((PlayerAbility) focusedUpgrade.getUpgrade()).onAction(player, active);
			});
			notifyActivity(server, focusedUpgradeIndex, 0, active);
		}
	}

	public void keyUp(MinecraftServer server) {
		UpgradeNode<?> focusedUpgrade = getFocusedUpgrade();
		swappingLocked = false;

		if (focusedUpgrade == null)
			return;

		if (swappingPerformed) {
			swappingPerformed = false;
			return;
		}

		if (cooldowns.getOrDefault(focusedUpgradeIndex, 0) > 0)
			return;

		PlayerAbility.Behavior behavior = ((PlayerAbility) focusedUpgrade.getUpgrade()).getBehavior();

		System.out.println(behavior.toString());

		if (behavior == PlayerAbility.Behavior.PRESS_TO_TOGGLE) {
			active = !active;
			NetcodeUtils.runIfPresent(server, this.uuid, player -> {
				((PlayerAbility) focusedUpgrade.getUpgrade()).onAction(player, active);
			});
			putOnCooldown(server, focusedUpgradeIndex, ((PlayerAbility) getFocusedUpgrade().getUpgrade()).getCooldown());

		} else if (behavior == PlayerAbility.Behavior.HOLD_TO_ACTIVATE) {
			active = false;
			NetcodeUtils.runIfPresent(server, this.uuid, player -> {
				((PlayerAbility) focusedUpgrade.getUpgrade()).onAction(player, active);
			});
			notifyActivity(server);

		} else if (behavior == PlayerAbility.Behavior.RELEASE_TO_PERFORM) {
			System.out.println("Release " + this.uuid);
			NetcodeUtils.runIfPresent(server, this.uuid, player -> {
				System.out.println(this.uuid + player.toString());
				((PlayerAbility) focusedUpgrade.getUpgrade()).onAction(player, active);
			});
			putOnCooldown(server, focusedUpgradeIndex, ((PlayerAbility) getFocusedUpgrade().getUpgrade()).getCooldown());
		}
	}

	public void quickSelectUpgrade(MinecraftServer server, int abilityIndex) {
		List<UpgradeNode<?>> learnedNodes = learnedAbilityNodes();

		if (learnedNodes.size() != 0) {
			boolean prevActive = this.active;
			this.active = false;

			UpgradeNode<?> previouslyFocused = getFocusedUpgrade();
			NetcodeUtils.runIfPresent(server, this.uuid, player -> {
				if (prevActive && ((PlayerAbility) previouslyFocused.getUpgrade()).getBehavior() == PlayerAbility.Behavior.PRESS_TO_TOGGLE)
					((PlayerAbility) previouslyFocused.getUpgrade()).onAction(player, this.active);
			});

			if (prevActive && ((PlayerAbility) getFocusedUpgrade().getUpgrade()).getBehavior() != PlayerAbility.Behavior.HOLD_TO_ACTIVATE)
				putOnCooldown(server, focusedUpgradeIndex, ((PlayerAbility) getFocusedUpgrade().getUpgrade()).getCooldown());

			syncFocusedIndex(server);
		}
	}

	public void cancelKeyDown(MinecraftServer server) {
		UpgradeNode<?> focusedUpgrade = getFocusedUpgrade();

		if (focusedUpgrade == null)
			return;

		PlayerAbility.Behavior behavior = ((PlayerAbility) focusedUpgrade.getUpgrade()).getBehavior();

		if (behavior == PlayerAbility.Behavior.HOLD_TO_ACTIVATE) {
			active = false;
			swappingLocked = false;
			swappingPerformed = false;
		}

		notifyActivity(server);
	}

	public String restrictedBy(Item item, Restrictions.Type restrictionType) {
		for (UpgradeGroup<Research> research : DungeonsConfig.UPGRADES.getAllResearches()) {
			if (researchesDone.contains(research.getName(1)))
				continue;
			if (research.getUpgrade(1).restricts(item, restrictionType))
				return research.getName(1);
		}
		return null;
	}

	public String restrictedBy(Block block, Restrictions.Type restrictionType) {
		for (UpgradeGroup<Research> research : DungeonsConfig.UPGRADES.getAllResearches()) {
			if (researchesDone.contains(research.getName(1)))
				continue;
			if (research.getUpgrade(1).restricts(block, restrictionType))
				return research.getName(1);
		}
		return null;
	}

	public String restrictedBy(EntityType<?> entityType, Restrictions.Type restrictionType) {
		for (UpgradeGroup<Research> research : DungeonsConfig.UPGRADES.getAllResearches()) {
			if (researchesDone.contains(research.getName(1)))
				continue;
			if (research.getUpgrade(1).restricts(entityType, restrictionType))
				return research.getName(1);
		}
		return null;
	}

	/* ------------------------------------ */

	public UpgradeTree upgradeUpgrade(MinecraftServer server, UpgradeNode<?> upgradeNode) {
		this.remove(server, upgradeNode);

		UpgradeGroup<?> upgradeGroup = DungeonsConfig.UPGRADES.getByName(upgradeNode.getGroup().getParentName());
		UpgradeNode<?> upgradedUpgradeNode = new UpgradeNode<>(upgradeGroup, upgradeNode.getLevel() + 1);
		if (upgradedUpgradeNode.getGroup().getUpgrade(1) instanceof Research) {
			this.researchesDone.add(upgradedUpgradeNode.getGroup().getParentName());
		}
		this.add(server, upgradedUpgradeNode);
		return this;
	}

	public void notifyActivity(MinecraftServer server) {
		notifyActivity(server, this.focusedUpgradeIndex, this.cooldowns.getOrDefault(this.focusedUpgradeIndex, 0), this.active);
	}

	public void notifyCooldown(MinecraftServer server, int abilityIndex, int cooldown) {
		notifyActivity(server, abilityIndex, cooldown, 0);
	}

	public void notifyActivity(MinecraftServer server, int abilityIndex, int cooldown, boolean active) {
		notifyActivity(server, abilityIndex, cooldown, active ? 2 : 1);
	}

	public void notifyActivity(MinecraftServer server, int abilityIndex, int cooldown, int activeFlag) {
		NetcodeUtils.runIfPresent(server, this.uuid, player -> {
			DungeonsNetwork.CHANNEL.sendTo(new AbilityActivityMessage(abilityIndex, cooldown, activeFlag), player.connection.netManager,
					NetworkDirection.PLAY_TO_CLIENT);
		});
	}

	public void sync(MinecraftServer server) {
		syncTree(server);
		syncFocusedIndex(server);
		notifyActivity(server);
	}

	public void syncFocusedIndex(MinecraftServer server) {
		NetcodeUtils.runIfPresent(server, this.uuid, player -> {
			DungeonsNetwork.CHANNEL.sendTo(new AbilityFocusedMessage(this.focusedUpgradeIndex), player.connection.netManager,
					NetworkDirection.PLAY_TO_CLIENT);
		});
		NetcodeUtils.runIfPresent(server, this.uuid, player -> {
			DungeonsNetwork.CHANNEL.sendTo(new UpgradeTreeMessage(this, player.getUniqueID()), player.connection.netManager,
					NetworkDirection.PLAY_TO_CLIENT);
		});
	}

	public void syncTree(MinecraftServer server) {
		NetcodeUtils.runIfPresent(server, this.uuid, player -> {
			DungeonsNetwork.CHANNEL.sendTo(new AbilityKnownMessage(this), player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		});
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

	public void tick(TickEvent.PlayerTickEvent event) {
		UpgradeNode<?> focusedAbility = getFocusedUpgrade();

		if (focusedAbility != null) {
			((PlayerAbility) focusedAbility.getUpgrade()).onTick(event.player, isActive());
		}

		for (Integer abilityIndex : cooldowns.keySet()) {
			cooldowns.computeIfPresent(abilityIndex, (index, cooldown) -> cooldown - 1);
			notifyCooldown(event.player.getServer(), abilityIndex, cooldowns.getOrDefault(abilityIndex, 0));
		}
		cooldowns.entrySet().removeIf(cooldown -> cooldown.getValue() <= 0);
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

		ListNBT researches = new ListNBT();
		for (int i = 0; i < researchesDone.size(); i++) {
			CompoundNBT research = new CompoundNBT();
			research.putString("name", researchesDone.get(i));
			researches.add(i, research);
		}
		nbt.put("researches", researches);

		nbt.putInt("FocusedIndex", focusedUpgradeIndex);

		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		ListNBT list = nbt.getList("Nodes", Constants.NBT.TAG_COMPOUND);
		this.nodes.clear();
		for (int i = 0; i < list.size(); i++) {
			this.add(null, UpgradeNode.fromNBT(list.getCompound(i), PlayerUpgrade.class));
		}

		ListNBT researches = nbt.getList("researches", Constants.NBT.TAG_COMPOUND);
		this.researchesDone = new LinkedList<>();
		for (int i = 0; i < researches.size(); i++) {
			CompoundNBT researchNBT = researches.getCompound(i);
			String name = researchNBT.getString("name");
			this.researchesDone.add(name);
		}

		this.focusedUpgradeIndex = MathHelper.clamp(nbt.getInt("FocusedIndex"), 0, learnedNodes().size() - 1);
	}
}
