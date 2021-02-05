package com.cy4.betterdungeons.common.item;

import java.util.List;

import javax.annotation.Nonnull;

import com.cy4.betterdungeons.core.init.ItemInit;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.patchouli.api.PatchouliAPI;

public class GuideBookItem extends Item {
	public GuideBookItem() {
		super(ItemInit.basicItem().maxStackSize(1));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(getEdition().deepCopy().mergeStyle(TextFormatting.GRAY));
	}

	@SuppressWarnings("deprecation")
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);

		if (playerIn instanceof ServerPlayerEntity) {
			PatchouliAPI.instance.openBookGUI((ServerPlayerEntity) playerIn, Registry.ITEM.getKey(this));
		}

		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}

	@SuppressWarnings("deprecation")
	public static ITextComponent getEdition() {
		return PatchouliAPI.instance.getSubtitle(Registry.ITEM.getKey(ItemInit.GUIDE_BOOK.get()));
	}
}
