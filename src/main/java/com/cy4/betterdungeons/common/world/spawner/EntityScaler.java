package com.cy4.betterdungeons.common.world.spawner;

import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.cy4.betterdungeons.core.config.type.DungeonMobsConfig;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EntityScaler {

	public static void scaleVault(LivingEntity entity, int level, Random random, Type type) {
		DungeonMobsConfig.Level overrides = DungeonsConfig.DUNGEON_MOBS.getForLevel(level);

		for(EquipmentSlotType slot: EquipmentSlotType.values()) {
			if(slot.getSlotType() == EquipmentSlotType.Group.HAND) {
				if(!entity.getItemStackFromSlot(slot).isEmpty())continue;
			}

			ItemStack loot = new ItemStack(type.loot.apply(overrides, slot));

			for(int i = 0; i < type.trials.apply(overrides); i++) {
				EnchantmentHelper.addRandomEnchantment(random, loot,
						EnchantmentHelper.calcItemStackEnchantability(random, type.level.apply(overrides), 15, loot), true);
			}

			entity.setItemStackToSlot(slot, loot);
		}
	}

	public enum Type {
		MOB(DungeonMobsConfig.Level::getForMob, level -> level.MOB_MISC.ENCH_TRIALS, level -> level.MOB_MISC.ENCH_LEVEL),
		BOSS(DungeonMobsConfig.Level::getForBoss, level -> level.BOSS_MISC.ENCH_TRIALS, level -> level.BOSS_MISC.ENCH_LEVEL);

		private final BiFunction<DungeonMobsConfig.Level, EquipmentSlotType, Item> loot;
		private final Function<DungeonMobsConfig.Level, Integer> trials;
		private final Function<DungeonMobsConfig.Level, Integer> level;

		Type(BiFunction<DungeonMobsConfig.Level, EquipmentSlotType, Item> loot,
		     Function<DungeonMobsConfig.Level, Integer> trials,
		     Function<DungeonMobsConfig.Level, Integer> level) {
			this.loot = loot;
			this.trials = trials;
			this.level = level;
		}
	}

}
