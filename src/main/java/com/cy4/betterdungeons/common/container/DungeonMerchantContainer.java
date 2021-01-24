package com.cy4.betterdungeons.common.container;

import java.util.List;

import com.cy4.betterdungeons.common.block.DungeonMerchantBlock;
import com.cy4.betterdungeons.common.container.inv.MerchantInventory;
import com.cy4.betterdungeons.common.container.slot.NonValidSlot;
import com.cy4.betterdungeons.common.merchant.Merchant;
import com.cy4.betterdungeons.common.te.DungeonMerchantTileEntity;
import com.cy4.betterdungeons.core.init.ContainerTypesInit;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DungeonMerchantContainer extends Container {

	protected DungeonMerchantTileEntity tileEntity;
	protected MerchantInventory vendingInventory;
	protected PlayerInventory playerInventory;

	public DungeonMerchantContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
		super(ContainerTypesInit.DUNGEON_MERCHANT_CONTAINER.get(), windowId);

		BlockState blockState = world.getBlockState(pos);
		this.tileEntity = DungeonMerchantBlock.getMerchantTile(world, pos, blockState);
		this.playerInventory = playerInventory;

		this.vendingInventory = new MerchantInventory();
		this.addSlot(new Slot(vendingInventory, MerchantInventory.BUY_SLOT, 210, 43) {
			@Override
			public void onSlotChanged() {
				super.onSlotChanged();
				vendingInventory.updateRecipe();
			}

			@Override
			public void onSlotChange(ItemStack oldStackIn, ItemStack newStackIn) {
				super.onSlotChange(oldStackIn, newStackIn);
				vendingInventory.updateRecipe();
			}
		});
		this.addSlot(new NonValidSlot(vendingInventory, MerchantInventory.SELL_SLOT, 268, 43));

		// Player Inventory
		for (int i1 = 0; i1 < 3; ++i1) {
			for (int k1 = 0; k1 < 9; ++k1) {
				this.addSlot(new Slot(playerInventory, k1 + i1 * 9 + 9, 167 + k1 * 18, 86 + i1 * 18));
			}
		}

		// Player Hotbar
		for (int j1 = 0; j1 < 9; ++j1) {
			this.addSlot(new Slot(playerInventory, j1, 167 + j1 * 18, 144));
		}
	}

	public DungeonMerchantContainer(int windowId, PlayerInventory inv, PacketBuffer buffer) {
		this(windowId, inv.player.world, buffer.readBlockPos(), inv, inv.player);
	}

	public DungeonMerchantTileEntity getTileEntity() {
		return tileEntity;
	}

	public Merchant getSelectedTrade() {
		return vendingInventory.getSelectedCore();
	}

	public void selectTrade(int index) {
		List<Merchant> cores = tileEntity.getCores();
		if (index < 0 || index >= cores.size())
			return;

		Merchant traderCore = cores.get(index);

		vendingInventory.updateSelectedCore(tileEntity, traderCore);
		vendingInventory.updateRecipe();

		if (vendingInventory.getStackInSlot(MerchantInventory.BUY_SLOT) != ItemStack.EMPTY) {
			ItemStack buyStack = vendingInventory.removeStackFromSlot(MerchantInventory.BUY_SLOT);
			playerInventory.addItemStackToInventory(buyStack);
		}

		if (traderCore.getTrade().getTradesLeft() <= 0)
			return;

		int slot = slotForItem(traderCore.getTrade().getBuy().getItem());
		if (slot != -1) {
			ItemStack buyStack = playerInventory.removeStackFromSlot(slot);
			vendingInventory.setInventorySlotContents(MerchantInventory.BUY_SLOT, buyStack);
		}
	}

	private int slotForItem(Item item) {
		for (int i = 0; i < playerInventory.getSizeInventory(); i++) {
			if (playerInventory.getStackInSlot(i).getItem() == item) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public void onContainerClosed(PlayerEntity player) {
		super.onContainerClosed(player);

		ItemStack buy = vendingInventory.getStackInSlot(0);

		if (!buy.isEmpty()) {
			boolean added = player.inventory.addItemStackToInventory(buy);
			if (!added)
				player.dropItem(buy, false, false);
		}
	}
}
