package com.cy4.betterdungeons.common.upgrade;

import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

import com.cy4.betterdungeons.common.upgrade.type.DashUpgrade;
import com.cy4.betterdungeons.common.upgrade.type.EffectUpgrade;
import com.cy4.betterdungeons.common.upgrade.type.PlayerUpgrade;
import com.cy4.betterdungeons.common.upgrade.type.VeinMinerUpgrade;
import com.cy4.betterdungeons.core.util.math.RomanNumber;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.annotations.Expose;

import net.minecraft.potion.Effect;

public class UpgradeGroup<T extends PlayerUpgrade> {

	@Expose
	private final String name;
	@Expose
	private final T[] levels;

	private BiMap<String, T> registry;

	@SuppressWarnings("unchecked")
	public UpgradeGroup(String name, T... levels) {
		this.name = name;
		this.levels = levels;
	}

	public int getMaxLevel() {
		return this.levels.length;
	}

	public String getParentName() {
		return this.name;
	}

	public String getName(int level) {
		if (level == 0)
			return name + " " + RomanNumber.toRoman(0);
		return this.getRegistry().inverse().get(this.getUpgrade(level));
	}

	public T getUpgrade(int level) {
		if (level < 0)
			return this.levels[0];
		if (level >= getMaxLevel())
			return this.levels[getMaxLevel() - 1];
		return this.levels[level - 1];
	}

	public BiMap<String, T> getRegistry() {
		if (this.registry == null) {
			this.registry = HashBiMap.create(this.getMaxLevel());

			if (this.getMaxLevel() == 1)
				this.registry.put(this.getParentName(), this.levels[0]);

			else if (this.getMaxLevel() > 1) {
				for (int i = 0; i < this.getMaxLevel(); i++) {
					this.registry.put(this.getParentName() + " " + RomanNumber.toRoman(i + 1), this.getUpgrade(i + 1));
				}
			}
		}
		return this.registry;
	}

	public static UpgradeGroup<EffectUpgrade> ofEffect(String name, Effect effect, EffectUpgrade.Type type, int maxLevel,
			IntUnaryOperator cost) {
		EffectUpgrade[] talents = IntStream.range(0, maxLevel).mapToObj(i -> new EffectUpgrade(cost.applyAsInt(i + 1), effect, i, type))
				.toArray(EffectUpgrade[]::new);
		return new UpgradeGroup<>(name, talents);
	}

	public static UpgradeGroup<VeinMinerUpgrade> ofVeinMiner(String name, int maxLevel, IntUnaryOperator cost) {
		VeinMinerUpgrade[] talents = IntStream.range(0, maxLevel).mapToObj(i -> new VeinMinerUpgrade(cost.applyAsInt(i + 1), i * 3 + 9))
				.toArray(VeinMinerUpgrade[]::new);
		return new UpgradeGroup<>(name, talents);
	}

	public static UpgradeGroup<DashUpgrade> ofDash(String name, int maxLevel, IntUnaryOperator cost) {
		DashUpgrade[] talents = IntStream.range(0, maxLevel).mapToObj(i -> new DashUpgrade(cost.applyAsInt(i + 1), i * 3))
				.toArray(DashUpgrade[]::new);
		return new UpgradeGroup<>(name, talents);
	}
}