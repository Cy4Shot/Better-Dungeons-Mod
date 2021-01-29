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
		this.styles = new HashMap<>();

		putUpgrade("Vein Miner", UpgradeFrame.STAR, 3, 0, 4, 0);
		putUpgrade("Haste", UpgradeFrame.STAR, 3, 1, 0, 0);
		putUpgrade("Regeneration", UpgradeFrame.STAR, 2, 0, 1, 0);
		putUpgrade("Resistance", UpgradeFrame.STAR, 1, 0, 2, 0);
		putUpgrade("Strength", UpgradeFrame.STAR, 0, 0, 3, 0);
		putUpgrade("Dash", UpgradeFrame.STAR, 2, 1, 5, 0);
		putUpgrade("Dark Utilities", UpgradeFrame.RECTANGULAR, 0, 2, 6, 0);
		putUpgrade("Jump Boost", UpgradeFrame.STAR, 4, 0, 7, 0);
		putUpgrade("Master Builder", UpgradeFrame.RECTANGULAR, 3, 2, 8, 0);
		putUpgrade("Elevated", UpgradeFrame.RECTANGULAR, 4, 2, 9, 0);
		putUpgrade("Engineer", UpgradeFrame.RECTANGULAR, 0, 3, 10, 0);
		putUpgrade("Mekanic", UpgradeFrame.RECTANGULAR, 1, 3, 11, 0);
		putUpgrade("Dank", UpgradeFrame.RECTANGULAR, 0, 4, 13, 0);
		putUpgrade("Backpacks", UpgradeFrame.RECTANGULAR, 1, 4, 14, 0);
		putUpgrade("Store My Items", UpgradeFrame.RECTANGULAR, 2, 4, 15, 0);
		putUpgrade("Vein Miner", UpgradeFrame.STAR, 4, 1, 0, 1);
	}

	protected void putUpgrade(String name, UpgradeFrame frame, int posX, int posY, int texX, int texY) {
		styles.put(DungeonsConfig.UPGRADES.getByName(name).getParentName(),
				new UpgradeStyle(70 * posX, 70 * posY, 16 * texX, 16 * texY, frame));
	}

}