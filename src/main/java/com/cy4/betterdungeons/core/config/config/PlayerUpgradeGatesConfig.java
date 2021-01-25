package com.cy4.betterdungeons.core.config.config;

import com.cy4.betterdungeons.core.config.Config;
import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.cy4.betterdungeons.core.network.stats.UpgradeGates;
import com.google.gson.annotations.Expose;

public class PlayerUpgradeGatesConfig extends Config {

	@Expose
	private UpgradeGates UPGRADE_GATES;

	@Override
	public String getName() {
		return "upgrade_gates";
	}

	public UpgradeGates getGates() {
		return UPGRADE_GATES;
	}

	@Override
	protected void reset() {
		UPGRADE_GATES = new UpgradeGates();
		UpgradeGates.Entry gateEntry;

		// Talents
		gateEntry = new UpgradeGates.Entry();
		gateEntry.setDependsOn(DungeonsConfig.UPGRADES.REGENERATION.getParentName());
		UPGRADE_GATES.addEntry(DungeonsConfig.UPGRADES.HASTE.getParentName(), gateEntry);
	}

}
