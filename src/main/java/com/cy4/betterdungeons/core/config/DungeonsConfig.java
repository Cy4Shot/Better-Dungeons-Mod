package com.cy4.betterdungeons.core.config;

import com.cy4.betterdungeons.core.config.config.DungeonConfig;
import com.cy4.betterdungeons.core.config.config.DungeonMobsConfig;
import com.cy4.betterdungeons.core.config.config.KeyCreationTableConfig;
import com.cy4.betterdungeons.core.config.config.KeyGeneratorConfig;
import com.cy4.betterdungeons.core.config.config.LevelMetaConfig;
import com.cy4.betterdungeons.core.config.config.MerchantTradesConfig;
import com.cy4.betterdungeons.core.config.config.PlayerUpgradeGatesConfig;
import com.cy4.betterdungeons.core.config.config.PlayerUpgradesConfig;
import com.cy4.betterdungeons.core.config.config.UpgradeDescriptionsConfig;
import com.cy4.betterdungeons.core.config.config.UpgradesGuiConfig;

public class DungeonsConfig {
	public static DungeonMobsConfig DUNGEON_MOBS;
	public static DungeonConfig CONFIG;
	public static LevelMetaConfig LEVELS_META;
	public static KeyCreationTableConfig KEY_CREATION_TABLE;
	public static KeyGeneratorConfig KEY_GENERATOR;
	public static PlayerUpgradesConfig UPGRADES;
	public static UpgradesGuiConfig UPGRADES_GUI;
	public static UpgradeDescriptionsConfig UPGRADE_DESCRIPTIONS;
	public static PlayerUpgradeGatesConfig UPGRADE_GATES;
	public static MerchantTradesConfig MERCHANT_TRADES;

	public static void register() {
		DUNGEON_MOBS = (DungeonMobsConfig) new DungeonMobsConfig().readConfig();
		CONFIG = (DungeonConfig) new DungeonConfig().readConfig();
		LEVELS_META = (LevelMetaConfig) new LevelMetaConfig().readConfig();
		KEY_CREATION_TABLE = (KeyCreationTableConfig) new KeyCreationTableConfig().readConfig();
		KEY_GENERATOR = (KeyGeneratorConfig) new KeyGeneratorConfig().readConfig();
		UPGRADES = (PlayerUpgradesConfig) new PlayerUpgradesConfig().readConfig();
		MERCHANT_TRADES = (MerchantTradesConfig) new MerchantTradesConfig().readConfig();
		UPGRADE_GATES = (PlayerUpgradeGatesConfig) new PlayerUpgradeGatesConfig().readConfig();
		UPGRADES_GUI = (UpgradesGuiConfig) new UpgradesGuiConfig().readConfig();
		UPGRADE_DESCRIPTIONS = (UpgradeDescriptionsConfig) new UpgradeDescriptionsConfig().readConfig();

	}
}
