package com.cy4.betterdungeons.common.container.inv;

import com.cy4.betterdungeons.common.merchant.Merchant;
import com.cy4.betterdungeons.common.merchant.Product;
import com.cy4.betterdungeons.common.merchant.Trade;
import com.cy4.betterdungeons.common.te.DungeonMerchantTileEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class MerchantInventory implements IInventory {

	public static final int BUY_SLOT = 0;
	public static final int EXTRA_SLOT = 1;
	public static final int SELL_SLOT = 2;

	private final NonNullList<ItemStack> slots = NonNullList.withSize(3, ItemStack.EMPTY);
	private DungeonMerchantTileEntity tileEntity;
	private Merchant selectedCore;

	public void updateSelectedCore(DungeonMerchantTileEntity tileEntity, Merchant core) {
		this.tileEntity = tileEntity;
		this.selectedCore = core;
	}

	public Merchant getSelectedCore() {
		return selectedCore;
	}

	@Override
	public int getSizeInventory() {
		return this.slots.size();
	}

	@Override
	public boolean isEmpty() {
		return this.slots.isEmpty();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return this.slots.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack itemStack = slots.get(index);

		if (index == SELL_SLOT && !itemStack.isEmpty()) {
			ItemStack andSplit = ItemStackHelper.getAndSplit(slots, index, itemStack.getCount());
			decrStackSize(BUY_SLOT, selectedCore.getTrade().getBuy().getAmount());
			selectedCore.getTrade().onTraded();
			tileEntity.sendUpdates();
			updateRecipe();
			return andSplit;
		}

		ItemStack splitStack = ItemStackHelper.getAndSplit(slots, index, count);
		updateRecipe();
		return splitStack;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack andRemove = ItemStackHelper.getAndRemove(this.slots, index);
		updateRecipe();
		return andRemove;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		slots.set(index, stack);
		updateRecipe();
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return true;
	}

	public void updateRecipe() {
		if (selectedCore == null)
			return;

		Trade trade = selectedCore.getTrade();
		Product buy = trade.getBuy();
		Product sell = trade.getSell();

		if (slots.get(BUY_SLOT).getItem() != buy.getItem()) {
			slots.set(SELL_SLOT, ItemStack.EMPTY);
		} else if (slots.get(BUY_SLOT).getCount() < buy.getAmount()) {
			slots.set(SELL_SLOT, ItemStack.EMPTY);
		} else {
			slots.set(SELL_SLOT, sell.toStack());
		}

		if (trade.getTradesLeft() == 0) {
			slots.set(SELL_SLOT, ItemStack.EMPTY);
		}
	}

	@Override
	public void clear() {
		this.slots.clear();
	}

}