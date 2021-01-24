package com.cy4.betterdungeons.client.helper;

import com.cy4.betterdungeons.BetterDungeons;

import net.minecraft.util.ResourceLocation;

public enum UpgradeFrame {

	STAR(new ResourceBoundary(new ResourceLocation(BetterDungeons.MOD_ID, "textures/gui/skill-widget.png"), 0, 31, 30, 30)),
	RECTANGULAR(new ResourceBoundary(new ResourceLocation(BetterDungeons.MOD_ID, "textures/gui/skill-widget.png"), 30, 31, 30, 30));

	ResourceBoundary resourceBoundary;

	UpgradeFrame(ResourceBoundary resourceBoundary) {
		this.resourceBoundary = resourceBoundary;
	}

	public ResourceBoundary getResourceBoundary() {
		return resourceBoundary;
	}

}
