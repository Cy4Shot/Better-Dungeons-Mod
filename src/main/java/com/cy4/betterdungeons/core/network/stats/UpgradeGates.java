package com.cy4.betterdungeons.core.network.stats;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.cy4.betterdungeons.common.upgrade.UpgradeGroup;
import com.cy4.betterdungeons.common.upgrade.UpgradeTree;
import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.google.gson.annotations.Expose;

public class UpgradeGates {

	@Expose
	private Map<String, Entry> entries;

	public UpgradeGates() {
		this.entries = new HashMap<>();
	}

	public void addEntry(String skillName, Entry entry) {
		this.entries.put(skillName, entry);
	}

	public List<UpgradeGroup<?>> getDependencyUpgrades(String abilityName) {
		List<UpgradeGroup<?>> abilities = new LinkedList<>();
		Entry entry = entries.get(abilityName);
		if (entry == null)
			return abilities;
		entry.dependsOn.forEach(dependencyName -> {
			UpgradeGroup<?> dependency = DungeonsConfig.UPGRADES.getByName(dependencyName);
			abilities.add(dependency);
		});
		return abilities;
	}

	public List<UpgradeGroup<?>> getLockedUpgrades(String abilityName) {
		List<UpgradeGroup<?>> abilities = new LinkedList<>();
		Entry entry = entries.get(abilityName);
		if (entry == null)
			return abilities;
		entry.lockedBy.forEach(dependencyName -> {
			UpgradeGroup<?> dependency = DungeonsConfig.UPGRADES.getByName(dependencyName);
			abilities.add(dependency);
		});
		return abilities;
	}

	public boolean isLocked(UpgradeGroup<?> talent, UpgradeTree talentTree) {
		UpgradeGates gates = DungeonsConfig.UPGRADE_GATES.getGates();

		for (UpgradeGroup<?> dependencyTalent : gates.getDependencyUpgrades(talent.getParentName())) {
			if (!talentTree.getNodeOf(dependencyTalent).isLearned())
				return true;
		}

		for (UpgradeGroup<?> lockedByTalent : gates.getLockedUpgrades(talent.getParentName())) {
			if (talentTree.getNodeOf(lockedByTalent).isLearned())
				return true;
		}

		return false;
	}

	public static class Entry {
		@Expose
		private List<String> dependsOn;
		@Expose
		private List<String> lockedBy;

		public Entry() {
			this.dependsOn = new LinkedList<>();
			this.lockedBy = new LinkedList<>();
		}

		public void setDependsOn(String... skills) {
			dependsOn.addAll(Arrays.asList(skills));
		}

		public void setLockedBy(String... skills) {
			lockedBy.addAll(Arrays.asList(skills));
		}
	}

}
