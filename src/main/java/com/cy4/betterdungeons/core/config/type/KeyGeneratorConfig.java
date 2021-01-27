package com.cy4.betterdungeons.core.config.type;

import com.cy4.betterdungeons.core.config.Config;
import com.google.gson.annotations.Expose;

public class KeyGeneratorConfig extends Config {

	@Expose
	public int GENERATION_TIME;

	@Override
	public String getName() {
		return "key_generator";
	}

	@Override
	protected void reset() {
		GENERATION_TIME = 28800; // 800 seconds
	}

	public long genTimeMillis() {
		return ((long) GENERATION_TIME) * 1000L;
	}

}
