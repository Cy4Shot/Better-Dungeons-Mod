package com.cy4.betterdungeons.common.item;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.cy4.betterdungeons.core.init.ItemInit;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class BossTreasureItem extends Item {

	TreasureRarity rarity;

	public BossTreasureItem(TreasureRarity rarity) {
		super(ItemInit.basicItem().maxStackSize(1));
		this.rarity = rarity;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (worldIn.isRemote)
			return super.onItemRightClick(worldIn, playerIn, handIn);
		if (handIn != Hand.MAIN_HAND)
			return super.onItemRightClick(worldIn, playerIn, handIn);
		ItemStack stack = playerIn.getHeldItemMainhand();
		if (stack.getItem() instanceof BossTreasureItem) {
			BossTreasureItem item = (BossTreasureItem) stack.getItem();
			ItemStack toDrop = ItemStack.EMPTY;
			switch (item.getRarity()) {
			case COMMON:
				toDrop = DungeonsConfig.BOSS_TREASURE_COMMON.getRandom();
				break;
			case RARE:
				toDrop = DungeonsConfig.BOSS_TREASURE_RARE.getRandom();
				break;
			case EPIC:
				toDrop = DungeonsConfig.BOSS_TREASURE_EPIC.getRandom();
				break;
			case LEGENDARY:
				toDrop = DungeonsConfig.BOSS_TREASURE_LEGENDARY.getRandom();
				break;
			}
			playerIn.dropItem(toDrop, false);
			stack.shrink(1);
		}

		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if (stack.getItem() instanceof BossTreasureItem) {
			BossTreasureItem item = (BossTreasureItem) stack.getItem();
			tooltip.add(new StringTextComponent(TextFormatting.GOLD + "Right-Click to open..."));
			tooltip.add(new StringTextComponent("Rarity: " + item.getRarity().color + item.getRarity()));
		}
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		if (stack.getItem() instanceof BossTreasureItem) {
			BossTreasureItem item = (BossTreasureItem) stack.getItem();
			return new StringTextComponent(item.getRarity().color + "Treasure");
		}
		return super.getDisplayName(stack);
	}

	public TreasureRarity getRarity() {
		return rarity;
	}

	public enum TreasureRarity {
		COMMON(TextFormatting.AQUA), RARE(TextFormatting.GREEN), EPIC(TextFormatting.LIGHT_PURPLE), LEGENDARY(TextFormatting.YELLOW);

		public final TextFormatting color;

		TreasureRarity(TextFormatting color) {
			this.color = color;
		}

		public static TreasureRarity getWeightedRandom() {
			Random rand = new Random();
			return getWeightedRarityAt(rand.nextInt(getTotalWeight()));
		}

		private static int getTotalWeight() {
			int totalWeight = 0;
			for (TreasureRarity rarity : TreasureRarity.values()) {
				System.out.println("Weight: " + getWeight(rarity));
				totalWeight += getWeight(rarity);
			}
			return totalWeight;
		}

		private static TreasureRarity getWeightedRarityAt(int index) {
			TreasureRarity current = null;

			for (TreasureRarity rarity : TreasureRarity.values()) {
				current = rarity;
				index -= getWeight(rarity);
				System.out.println("Index: " + index);
				if (index < 0)
					break;
			}
			return current;
		}

		private static int getWeight(TreasureRarity rarity) {
			switch (rarity) {
			case COMMON:
				return DungeonsConfig.RARITY.COMMON_WEIGHT;
			case RARE:
				return DungeonsConfig.RARITY.RARE_WEIGHT;
			case EPIC:
				return DungeonsConfig.RARITY.EPIC_WEIGHT;
			case LEGENDARY:
				return DungeonsConfig.RARITY.LEGENDARY_WEIGHT;
			}
			return DungeonsConfig.RARITY.COMMON_WEIGHT;
		}
	}

}
