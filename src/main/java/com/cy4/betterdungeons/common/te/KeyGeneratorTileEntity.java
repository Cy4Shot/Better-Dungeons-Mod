package com.cy4.betterdungeons.common.te;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.annotation.Nullable;

import com.cy4.betterdungeons.common.block.KeyGeneratorBlock;
import com.cy4.betterdungeons.common.container.KeyGeneratorContainer;
import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.cy4.betterdungeons.core.init.ItemInit;
import com.cy4.betterdungeons.core.init.TileEntityTypesInit;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class KeyGeneratorTileEntity extends TileEntity
		implements INamedContainerProvider, IInventory, ITickableTileEntity {

	private static final DateFormat simple = new SimpleDateFormat("HH:mm:ss");

	Date lastRecievedDate = Calendar.getInstance().getTime();
	protected NonNullList<ItemStack> items = NonNullList.withSize(1, new ItemStack(ItemInit.DUNGEON_KEY.get()));

	public String time = "";

	public KeyGeneratorTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		simple.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public KeyGeneratorTileEntity() {
		this(TileEntityTypesInit.KEY_GENERATOR_TILE_ENTITY_TYPE.get());
	}

	@Override
	public Container createMenu(int arg0, PlayerInventory arg1, PlayerEntity arg2) {
		return new KeyGeneratorContainer(arg0, arg1, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.betterdungeons.key_generator");
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		this.items = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(nbt, this.items);
	}

	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		ItemStackHelper.saveAllItems(compound, this.items);
		return compound;
	}

	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT comp = new CompoundNBT();
		write(comp);
		return new SUpdateTileEntityPacket(this.pos, 1, comp);
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		this.read(state, tag);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}

	@Override
	public void clear() {
		this.items.clear();
	}

	@Override
	public ItemStack decrStackSize(int arg0, int arg1) {
		return ItemStackHelper.getAndSplit(this.items, arg0, arg1);
	}

	@Override
	public int getSizeInventory() {
		return this.items.size();
	}

	@Override
	public ItemStack getStackInSlot(int arg0) {
		return this.items.get(arg0);
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.items) {
			if (!itemstack.isEmpty())
				return false;
		}
		return true;
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return this.world.getTileEntity(this.pos) != this ? false
				: player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D,
						(double) this.pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public ItemStack removeStackFromSlot(int arg0) {
		return ItemStackHelper.getAndRemove(this.items, arg0);
	}

	@Override
	public void setInventorySlotContents(int arg0, ItemStack stack) {
		this.items.set(arg0, stack);
		if (stack.getCount() > this.getInventoryStackLimit())
			stack.setCount(this.getInventoryStackLimit());
	}

	@Override
	public void tick() {

		boolean hasKey = this.items.get(0).getCount() > 0;

		((KeyGeneratorBlock) this.world.getBlockState(this.pos).getBlock()).ready(hasKey,
				this.world.getBlockState(this.pos), this.world, this.pos);

		if (hasKey) {
			this.time = "Ready!";
			lastRecievedDate = Calendar.getInstance().getTime();
			return;
		}

		long tTime = Calendar.getInstance().getTime().getTime() - lastRecievedDate.getTime();
		this.time = simple.format(new Date(DungeonsConfig.KEY_GENERATOR.genTimeMillis() - tTime));

		if (tTime > DungeonsConfig.KEY_GENERATOR.genTimeMillis()) {
			items.set(0, new ItemStack(ItemInit.DUNGEON_KEY.get()));
			lastRecievedDate = Calendar.getInstance().getTime();
		}
	}

}
