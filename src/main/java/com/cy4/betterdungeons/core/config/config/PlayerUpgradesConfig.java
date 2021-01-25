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
	public UpgradeGroup<EffectUpgrade> HASTE;
	@Expose
	public UpgradeGroup<EffectUpgrade> REGENERATION;
	@Expose
	public UpgradeGroup<EffectUpgrade> RESISTANCE;
	@Expose
	public UpgradeGroup<EffectUpgrade> STRENGTH;

	@Override
	public String getName() {
		return "upgrades";
	}

	public List<UpgradeGroup<?>> getAll() {
		return Arrays.asList(HASTE, REGENERATION, RESISTANCE, STRENGTH);
	}

	public UpgradeGroup<?> getByName(String name) {
		return this.getAll().stream().filter(group -> group.getParentName().equals(name)).findFirst()
				.orElseThrow(() -> new IllegalStateException("Unknown talent with name " + name));
	}

	@Override
	protected void reset() {
		this.HASTE = UpgradeGroup.ofEffect("Haste", Effects.HASTE, EffectUpgrade.Type.ICON_ONLY, 6, i -> { if(i < 3)return 2; else if(i == 3)return 3; else return 4; });
        this.REGENERATION = UpgradeGroup.ofEffect("Regeneration", Effects.REGENERATION, EffectUpgrade.Type.ICON_ONLY, 3, i -> i == 0 ? 10 : 5);
        this.RESISTANCE = UpgradeGroup.ofEffect("Resistance", Effects.RESISTANCE, EffectUpgrade.Type.ICON_ONLY, 2, i -> 3);
        this.STRENGTH = UpgradeGroup.ofEffect("Strength", Effects.STRENGTH, EffectUpgrade.Type.ICON_ONLY, 2, i -> 3);
	}
}
