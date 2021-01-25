package com.cy4.betterdungeons.core.config.config;

import java.util.HashMap;

import com.cy4.betterdungeons.client.helper.UpgradeFrame;
import com.cy4.betterdungeons.common.upgrade.UpgradeStyle;
import com.cy4.betterdungeons.core.config.Config;
import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.google.gson.annotations.Expose;

public class UpgradesGuiConfig extends Config {

	@Expose
	private HashMap<String, UpgradeStyle> styles;

	@Override
	public String getName() {
		return "upgrades_gui_styles";
	}

	public HashMap<String, UpgradeStyle> getStyles() {
		return styles;
	}

	@Override
	protected void reset() {
		UpgradeStyle style;
		this.styles = new HashMap<>();

		style = new UpgradeStyle(70 * 1, 0, 16 * 0, 0);
		style.frameType = UpgradeFrame.RECTANGULAR;
		styles.put(DungeonsConfig.UPGRADES.HASTE.getParentName(), style);
		style = new UpgradeStyle(70 * 0, 0, 16 * 1, 0);
		style.frameType = UpgradeFrame.RECTANGULAR;
		styles.put(DungeonsConfig.UPGRADES.REGENERATION.getParentName(), style);
		style = new UpgradeStyle(70 * 2, 0, 16 * 2, 0);
		style.frameType = UpgradeFrame.RECTANGULAR;
		styles.put(DungeonsConfig.UPGRADES.RESISTANCE.getParentName(), style);
		style = new UpgradeStyle(70 * 3, 0, 16 * 3, 0);
		style.frameType = UpgradeFrame.RECTANGULAR;
		styles.put(DungeonsConfig.UPGRADES.STRENGTH.getParentName(), style);
	}

}