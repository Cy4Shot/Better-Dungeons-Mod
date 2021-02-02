package com.cy4.betterdungeons.common.item;

import java.util.List;

import javax.annotation.Nullable;

import com.cy4.betterdungeons.core.init.DimensionInit;
import com.cy4.betterdungeons.core.init.ItemInit;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;

public class DungeonFoodItem extends Item {

	public static Food DUNGEON_FOOD = new Food.Builder().saturation(0).hunger(0).fastToEat().setAlwaysEdible().build();

	protected int extraVaultTicks;

	public DungeonFoodItem(int extraSeconds) {
		super(ItemInit.BASIC_ITEM.food(DUNGEON_FOOD));
		this.extraVaultTicks = extraSeconds * 20;
	}

	public int getExtraVaultTicks() {
		return extraVaultTicks;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack itemStack = playerIn.getHeldItem(handIn);
		if (playerIn.world.getDimensionKey() != DimensionInit.DUNGEON_WORLD)
			return ActionResult.resultFail(itemStack);
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		StringTextComponent comp = new StringTextComponent("- Adds" + (int) (this.extraVaultTicks / 20) + " seconds to the Dungeon Timer");
		comp.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_00FF00)));
		tooltip.add(comp);
		StringTextComponent comp1 = new StringTextComponent("[!] Only edible inside a Dungeon");
		comp1.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_FF0000)).setItalic(true));
		tooltip.add(comp1);

		super.addInformation(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		IFormattableTextComponent displayName = (IFormattableTextComponent) super.getDisplayName(stack);
		return displayName.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_fcbd00)));
	}

}
