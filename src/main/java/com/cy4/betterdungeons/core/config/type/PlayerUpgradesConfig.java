package com.cy4.betterdungeons.core.config.type;

import java.util.Arrays;
import java.util.List;

import com.cy4.betterdungeons.common.upgrade.UpgradeGroup;
import com.cy4.betterdungeons.common.upgrade.UpgradeNode;
import com.cy4.betterdungeons.common.upgrade.type.EffectUpgrade;
import com.cy4.betterdungeons.common.upgrade.type.Research;
import com.cy4.betterdungeons.common.upgrade.type.ability.DashUpgrade;
import com.cy4.betterdungeons.common.upgrade.type.ability.GroundSlamUpgrade;
import com.cy4.betterdungeons.common.upgrade.type.ability.PlayerAbility;
import com.cy4.betterdungeons.common.upgrade.type.ability.VeinMinerUpgrade;
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
	@Expose
	public UpgradeGroup<EffectUpgrade> JUMP_BOOST;
	@Expose
	public UpgradeGroup<VeinMinerUpgrade> VEIN_MINER;
	@Expose
	public UpgradeGroup<GroundSlamUpgrade> GROUND_SLAM;
	@Expose
	public UpgradeGroup<DashUpgrade> DASH;
	@Expose
	public UpgradeGroup<Research> DARK_UTILITIES;
	@Expose
	public UpgradeGroup<Research> MASTER_BUILDER;
	@Expose
	public UpgradeGroup<Research> ENGINEER;
	@Expose
	public UpgradeGroup<Research> MEKANIC;
	@Expose
	public UpgradeGroup<Research> THERMIC;
	@Expose
	public UpgradeGroup<Research> TORCH_MASTER;
	@Expose
	public UpgradeGroup<Research> BACKPACKS;
	@Expose
	public UpgradeGroup<Research> DANK;
	@Expose
	public UpgradeGroup<Research> STORE_MY_ITEMS;
	@Expose
	public UpgradeGroup<Research> ELEVATED;

	@Override
	public String getName() {
		return "upgrades";
	}

	public List<UpgradeGroup<?>> getAll() {
		return Arrays.asList(HASTE, REGENERATION, RESISTANCE, STRENGTH, JUMP_BOOST, VEIN_MINER, GROUND_SLAM, DASH, DARK_UTILITIES,
				MASTER_BUILDER, ENGINEER, MEKANIC, THERMIC, TORCH_MASTER, BACKPACKS, DANK, STORE_MY_ITEMS, ELEVATED);
	}

	public List<UpgradeGroup<Research>> getAllResearches() {
		return Arrays.asList(DARK_UTILITIES, MASTER_BUILDER, ENGINEER, MEKANIC, THERMIC, TORCH_MASTER, BACKPACKS, DANK, STORE_MY_ITEMS,
				ELEVATED);
	}

	public UpgradeGroup<?> getByName(String name) {
		return this.getAll().stream().filter(group -> group.getParentName().equals(name)).findFirst()
				.orElseThrow(() -> new IllegalStateException("Unknown talent with name " + name));
	}

	public int cooldownOf(UpgradeNode<?> UpgradeNode) {
		UpgradeGroup<?> UpgradeGroup = getByName(UpgradeNode.getGroup().getParentName());
		return ((PlayerAbility) UpgradeGroup.getUpgrade(UpgradeNode.getLevel())).getCooldown();
	}

	@Override
	protected void reset() {
		this.HASTE = UpgradeGroup.ofEffect("Haste", Effects.HASTE, EffectUpgrade.Type.ICON_ONLY, 3, i -> i < 3 ? 2 : 3);
		this.REGENERATION = UpgradeGroup.ofEffect("Regeneration", Effects.REGENERATION, EffectUpgrade.Type.ICON_ONLY, 3,
				i -> i == 0 ? 10 : 5);
		this.RESISTANCE = UpgradeGroup.ofEffect("Resistance", Effects.RESISTANCE, EffectUpgrade.Type.ICON_ONLY, 2,
				i -> i == 2 ? i == 3 ? 10 : 5 : 3);
		this.STRENGTH = UpgradeGroup.ofEffect("Strength", Effects.STRENGTH, EffectUpgrade.Type.ICON_ONLY, 10, i -> i <= 3 ? 2 : 3);
		this.JUMP_BOOST = UpgradeGroup.ofEffect("Jump Boost", Effects.JUMP_BOOST, EffectUpgrade.Type.ICON_ONLY, 2, i -> 2);
		this.VEIN_MINER = new UpgradeGroup<>("Vein Miner", new VeinMinerUpgrade(1, 4), new VeinMinerUpgrade(1, 8),
				new VeinMinerUpgrade(1, 16), new VeinMinerUpgrade(2, 32), new VeinMinerUpgrade(2, 64));
		this.GROUND_SLAM = new UpgradeGroup<>("Ground Slam", new GroundSlamUpgrade(3, 1, 30), new GroundSlamUpgrade(1, 2, 28),
				new GroundSlamUpgrade(1, 3, 26), new GroundSlamUpgrade(1, 4, 24), new GroundSlamUpgrade(1, 5, 22),
				new GroundSlamUpgrade(1, 6, 20), new GroundSlamUpgrade(1, 7, 19), new GroundSlamUpgrade(1, 8, 18),
				new GroundSlamUpgrade(1, 9, 17), new GroundSlamUpgrade(1, 10, 16));
		this.DASH = new UpgradeGroup<>("Dash", new DashUpgrade(2, 1), new DashUpgrade(1, 2), new DashUpgrade(1, 3), new DashUpgrade(1, 4),
				new DashUpgrade(1, 5), new DashUpgrade(1, 6), new DashUpgrade(1, 7), new DashUpgrade(1, 8), new DashUpgrade(1, 9),
				new DashUpgrade(1, 10));
		this.DARK_UTILITIES = new UpgradeGroup<>("Dark Utilities",
				new Research(5, "darkutils").withRestrictions(false, false, false, false, true));
		this.MASTER_BUILDER = new UpgradeGroup<>("Master Builder",
				new Research(2, "buildinggadgets").withRestrictions(false, false, false, true, true));
		this.ENGINEER = new UpgradeGroup<>("Engineer",
				new Research(8, "immersiveengineering").withRestrictions(false, false, false, true, true));
		this.MEKANIC = new UpgradeGroup<>("Mekanic", new Research(10, "mekanism").withRestrictions(false, false, false, true, true));
		this.THERMIC = new UpgradeGroup<>("Thermic", new Research(25, "thermal").withRestrictions(false, false, false, false, true));
		this.TORCH_MASTER = new UpgradeGroup<>("Torch Master",
				new Research(1, "torchmaster").withRestrictions(false, false, false, true, true));
		this.BACKPACKS = new UpgradeGroup<>("Backpacks",
				new Research(3, "simplybackpacks").withRestrictions(false, false, false, false, true));
		this.DANK = new UpgradeGroup<>("Dank", new Research(3, "dankstorage").withRestrictions(false, false, false, false, true));
		this.STORE_MY_ITEMS = new UpgradeGroup<>("Store My Items",
				new Research(3, "appliedenergistics2", "refinedstorage").withRestrictions(false, false, false, true, true));
		this.ELEVATED = new UpgradeGroup<>("Elevated", new Research(1, "elevatorid").withRestrictions(false, false, false, false, true));
	}
}
