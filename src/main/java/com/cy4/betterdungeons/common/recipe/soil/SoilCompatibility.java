package com.cy4.betterdungeons.common.recipe.soil;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.recipe.sapling.SaplingInfo;
import com.cy4.betterdungeons.core.init.RecipesInit;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

public class SoilCompatibility {

	public static final SoilCompatibility INSTANCE = new SoilCompatibility();

	private Map<SoilInfo, Set<SaplingInfo>> treeCompatibility;
	private Map<SaplingInfo, Set<SoilInfo>> soilCompatibility;

	private void addCompatEntry(SoilInfo soil, SaplingInfo tree) {
		if (!soilCompatibility.containsKey(tree)) {
			soilCompatibility.put(tree, new HashSet<>());
		}

		soilCompatibility.get(tree).add(soil);

		if (!treeCompatibility.containsKey(soil)) {
			treeCompatibility.put(soil, new HashSet<>());
		}

		treeCompatibility.get(soil).add(tree);
	}

	public Set<SoilInfo> getValidSoilsForSapling(SaplingInfo sapling) {
		return soilCompatibility.getOrDefault(sapling, new HashSet<>());
	}

	public boolean canTreeGrowOnSoil(SaplingInfo sapling, SoilInfo soil) {
		if (!soilCompatibility.containsKey(sapling) || soilCompatibility.get(sapling) == null) {
			return false;
		}

		return soilCompatibility.get(sapling).contains(soil);
	}

	public boolean isValidSoil(ItemStack soilStack) {
		for (SoilInfo soil : treeCompatibility.keySet()) {
			if (soil.ingredient.test(soilStack)) {
				return true;
			}
		}

		return false;
	}

	public void update(Collection<IRecipe<?>> recipes) {
		if (recipes == null || recipes.size() <= 0) {
			return;
		}

		List<SaplingInfo> saplings = recipes.stream().filter(r -> r.getType() == RecipesInit.saplingRecipeType).map(r -> (SaplingInfo) r)
				.collect(Collectors.toList());
		List<SoilInfo> soils = recipes.stream().filter(r -> r.getType() == RecipesInit.soilRecipeType).map(r -> (SoilInfo) r)
				.collect(Collectors.toList());

		treeCompatibility = new HashMap<>();
		soilCompatibility = new HashMap<>();

		Map<String, Set<SoilInfo>> reverseSoilTagMap = new HashMap<>();
		for (SoilInfo soil : soils) {
			for (String tag : soil.tags) {
				if (!reverseSoilTagMap.containsKey(tag)) {
					reverseSoilTagMap.put(tag, new HashSet<>());
				}

				reverseSoilTagMap.get(tag).add(soil);
			}
		}

		for (SaplingInfo sapling : saplings) {
			for (String tag : sapling.tags) {
				if (!reverseSoilTagMap.containsKey(tag)) {
					continue;
				}

				for (SoilInfo soil : reverseSoilTagMap.get(tag)) {
					BetterDungeons.LOGGER.debug("Tree '{}' grows on '{}' because of '{}'", sapling.getId(), soil.getId(), tag);
					this.addCompatEntry(soil, sapling);
				}
			}
		}

		BetterDungeons.LOGGER.info("Updated soil compatibility");
	}

}
