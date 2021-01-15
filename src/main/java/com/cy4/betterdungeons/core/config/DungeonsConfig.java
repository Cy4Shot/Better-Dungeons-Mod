package com.cy4.betterdungeons.core.config;

public class DungeonsConfig {
//	public static DungeonMobsConfig DUNGEON_MOBS;
	public static LevelMetaConfig LEVELS_META;
	public static KeyCreationTableConfig KEY_CREATION_TABLE;
	public static KeyGeneratorConfig KEY_GENERATOR;

	public static void register() {
//        DUNGEON_MOBS = (DungeonMobsConfig) new DungeonMobsConfig().readConfig();
		LEVELS_META = (LevelMetaConfig) new LevelMetaConfig().readConfig();
		KEY_CREATION_TABLE = (KeyCreationTableConfig) new KeyCreationTableConfig().readConfig();
		KEY_GENERATOR = (KeyGeneratorConfig) new KeyGeneratorConfig().readConfig();
		
	}
}
