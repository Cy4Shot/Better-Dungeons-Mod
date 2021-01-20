package com.cy4.betterdungeons.core.config.config;

import java.util.Arrays;
import java.util.List;

import com.cy4.betterdungeons.common.upgrade.UpgradeGroup;
import com.cy4.betterdungeons.common.upgrade.type.EffectUpgrade;
import com.cy4.betterdungeons.core.config.Config;
import com.google.gson.annotations.Expose;

import net.minecraft.potion.Effects;

public class PlayerUpgradesConfig extends Config {

	@Expose
	public UpgradeGroup<EffectUpgrade> HASTE_1;
	@Expose
	public UpgradeGroup<EffectUpgrade> REGENERATION_1;
	@Expose
	public UpgradeGroup<EffectUpgrade> RESISTANCE_1;
	@Expose
	public UpgradeGroup<EffectUpgrade> STRENGTH_1;

	@Override
	public String getName() {
		return "talents";
	}

	public List<UpgradeGroup<?>> getAll() {
		return Arrays.asList(HASTE_1, REGENERATION_1, RESISTANCE_1, STRENGTH_1);
	}

	public UpgradeGroup<?> getByName(String name) {
		return this.getAll().stream().filter(group -> group.getParentName().equals(name)).findFirst()
				.orElseThrow(() -> new IllegalStateException("Unknown talent with name " + name));
	}

	@Override
	protected void reset() {
		this.HASTE_1 = UpgradeGroup.ofEffect("Haste", Effects.HASTE, EffectUpgrade.Type.ICON_ONLY, 1);
		this.REGENERATION_1 = UpgradeGroup.ofEffect("Regeneration", Effects.REGENERATION, EffectUpgrade.Type.ICON_ONLY, 1);
		this.RESISTANCE_1 = UpgradeGroup.ofEffect("Resistance", Effects.RESISTANCE, EffectUpgrade.Type.ICON_ONLY, 1);
		this.STRENGTH_1 = UpgradeGroup.ofEffect("Strength", Effects.STRENGTH, EffectUpgrade.Type.ICON_ONLY, 1);
	}

}
