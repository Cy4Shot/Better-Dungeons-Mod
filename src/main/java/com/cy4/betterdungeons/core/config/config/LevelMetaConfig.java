package com.cy4.betterdungeons.core.config.config;

import java.util.LinkedList;
import java.util.List;

import com.cy4.betterdungeons.core.config.Config;
import com.google.gson.annotations.Expose;

public class LevelMetaConfig extends Config {
	@Expose
	public List<DungeonLevelMeta> levelMetas;

	@Override
	public String getName() {
		return "dungeon_levels";
	}

	public DungeonLevelMeta getLevelMeta(int level) {
		int maxLevelTNLAvailable = levelMetas.size() - 1;

		if (level < 0 || level > maxLevelTNLAvailable)
			return levelMetas.get(maxLevelTNLAvailable);

		return levelMetas.get(level);
	}

	@Override
	protected void reset() {
		levelMetas = new LinkedList<>();

		for (int i = 0; i < 80; i++) {
			DungeonLevelMeta dungeonLevel = new DungeonLevelMeta();
			dungeonLevel.level = i;
			dungeonLevel.tnl = defaultTNLFunction(i);
			levelMetas.add(dungeonLevel);
		}
	}

	public int defaultTNLFunction(int level) {
		return level * 1100 + 1000;
	}

	public static class DungeonLevelMeta {
		@Expose
		public int level;
		@Expose
		public int tnl;
	}
}
