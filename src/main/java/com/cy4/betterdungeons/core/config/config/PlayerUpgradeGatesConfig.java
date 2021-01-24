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
		gateEntry.setLockedBy(DungeonsConfig.UPGRADES.REGENERATION_1.getParentName());
		UPGRADE_GATES.addEntry(DungeonsConfig.UPGRADES.HASTE_1.getParentName(), gateEntry);

		// Researches
		gateEntry = new UpgradeGates.Entry();
		gateEntry.setDependsOn("Storage Noob");
		UPGRADE_GATES.addEntry("Storage Master", gateEntry);

		gateEntry = new UpgradeGates.Entry();
		gateEntry.setDependsOn("Storage Master");
		UPGRADE_GATES.addEntry("Storage Refined", gateEntry);

		gateEntry = new UpgradeGates.Entry();
		gateEntry.setDependsOn("Storage Refined");
		UPGRADE_GATES.addEntry("Storage Energistic", gateEntry);

		gateEntry = new UpgradeGates.Entry();
		gateEntry.setDependsOn("Storage Energistic");
		UPGRADE_GATES.addEntry("Storage Enthusiast", gateEntry);

		gateEntry = new UpgradeGates.Entry();
		gateEntry.setDependsOn("Decorator");
		UPGRADE_GATES.addEntry("Decorator Pro", gateEntry);

		gateEntry = new UpgradeGates.Entry();
		gateEntry.setDependsOn("Tech Freak");
		UPGRADE_GATES.addEntry("Nuclear Power", gateEntry);
	}

}
