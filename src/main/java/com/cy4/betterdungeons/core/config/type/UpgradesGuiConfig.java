package com.cy4.betterdungeons.core.config.type;

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

		style = new UpgradeStyle(70 * 3, 70 * 1, 16 * 4, 0);
		style.frameType = UpgradeFrame.STAR;
		styles.put(DungeonsConfig.UPGRADES.VEIN_MINER.getParentName(), style);
		style = new UpgradeStyle(70 * 3, 70 * 0, 16 * 0, 0);
		style.frameType = UpgradeFrame.STAR;
		styles.put(DungeonsConfig.UPGRADES.HASTE.getParentName(), style);
		style = new UpgradeStyle(70 * 2, 70 * 0, 16 * 1, 0);
		style.frameType = UpgradeFrame.STAR;
		styles.put(DungeonsConfig.UPGRADES.REGENERATION.getParentName(), style);
		style = new UpgradeStyle(70 * 1, 70 * 0, 16 * 2, 0);
		style.frameType = UpgradeFrame.STAR;
		styles.put(DungeonsConfig.UPGRADES.RESISTANCE.getParentName(), style);
		style = new UpgradeStyle(70 * 0, 70 * 0, 16 * 3, 0);
		style.frameType = UpgradeFrame.STAR;
		styles.put(DungeonsConfig.UPGRADES.STRENGTH.getParentName(), style);
		style = new UpgradeStyle(70 * 2, 70 * 1, 16 * 5, 0);
		style.frameType = UpgradeFrame.STAR;
		styles.put(DungeonsConfig.UPGRADES.DASH.getParentName(), style);
		style = new UpgradeStyle(70 * 0, 70 * 2, 16 * 6, 0);
		style.frameType = UpgradeFrame.RECTANGULAR;
		styles.put(DungeonsConfig.UPGRADES.DARK_UTILITIES.getParentName(), style);
		style = new UpgradeStyle(70 * 4, 70 * 0, 16 * 7, 0);
		style.frameType = UpgradeFrame.STAR;
		styles.put(DungeonsConfig.UPGRADES.JUMP_BOOST.getParentName(), style);
		style = new UpgradeStyle(70 * 3, 70 * 2, 16 * 8, 0);
		style.frameType = UpgradeFrame.RECTANGULAR;
		styles.put(DungeonsConfig.UPGRADES.MASTER_BUILDER.getParentName(), style);
		style = new UpgradeStyle(70 * 4, 70 * 2, 16 * 9, 0);
		style.frameType = UpgradeFrame.RECTANGULAR;
		styles.put(DungeonsConfig.UPGRADES.ELEVATED.getParentName(), style);
		style = new UpgradeStyle(70 * 0, 70 * 3, 16 * 10, 0);
		style.frameType = UpgradeFrame.RECTANGULAR;
		styles.put(DungeonsConfig.UPGRADES.ENGINEER.getParentName(), style);
		style = new UpgradeStyle(70 * 1, 70 * 3, 16 * 11, 0);
		style.frameType = UpgradeFrame.RECTANGULAR;
		styles.put(DungeonsConfig.UPGRADES.MEKANIC.getParentName(), style);
		style = new UpgradeStyle(70 * 2, 70 * 3, 16 * 12, 0);
		style.frameType = UpgradeFrame.RECTANGULAR;
		styles.put(DungeonsConfig.UPGRADES.THERMIC.getParentName(), style);
		style = new UpgradeStyle(70 * 0, 70 * 4, 16 * 13, 0);
		style.frameType = UpgradeFrame.RECTANGULAR;
		styles.put(DungeonsConfig.UPGRADES.DANK.getParentName(), style);
		style = new UpgradeStyle(70 * 1, 70 * 4, 16 * 14, 0);
		style.frameType = UpgradeFrame.RECTANGULAR;
		styles.put(DungeonsConfig.UPGRADES.BACKPACKS.getParentName(), style);
		style = new UpgradeStyle(70 * 2, 70 * 4, 16 * 15, 0);
		style.frameType = UpgradeFrame.RECTANGULAR;
		styles.put(DungeonsConfig.UPGRADES.STORE_MY_ITEMS.getParentName(), style);
	}

}