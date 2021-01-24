package com.cy4.betterdungeons.common.te;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import com.cy4.betterdungeons.common.item.MerchantItem;
import com.cy4.betterdungeons.common.merchant.Merchant;
import com.cy4.betterdungeons.core.init.TileEntityTypesInit;
import com.cy4.betterdungeons.core.util.nbt.NBTSerializer;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.Constants;

public class DungeonMerchantTileEntity extends TileEntity {

	private List<Merchant> cores = new ArrayList<>();

	public DungeonMerchantTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	public DungeonMerchantTileEntity() {
		this(TileEntityTypesInit.DUNGEON_MERCHANT_TILE_ENTITY_TYPE.get());
	}

	public List<Merchant> getCores() {
		return cores;
	}

	public void addCore(Merchant core) {
		this.cores.add(core);
		sendUpdates();
	}

	public Merchant getLastCore() {
		if (cores == null || cores.size() == 0)
			return null;
		return cores.get(cores.size() - 1);
	}

	public ItemStack getMerchantStack() {
		Merchant lastCore = this.getLastCore();
		if (lastCore == null)
			return ItemStack.EMPTY;
		ItemStack stack = MerchantItem.getStackFromCore(lastCore);
		cores.remove(lastCore);
		return stack;
	}

	public void sendUpdates() {
		this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 0b11);
		this.world.notifyNeighborsOfStateChange(pos, this.getBlockState().getBlock());
		markDirty();
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		ListNBT list = new ListNBT();
		for (Merchant core : cores) {
			try {
				list.add(NBTSerializer.serialize(core));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		compound.put("coresList", list);
		return super.write(compound);
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		ListNBT list = nbt.getList("coresList", Constants.NBT.TAG_COMPOUND);
		this.cores = new LinkedList<>();
		for (INBT tag : list) {
			Merchant core = null;
			try {
				core = NBTSerializer.deserialize(Merchant.class, (CompoundNBT) tag);
			} catch (Exception e) {
				e.printStackTrace();
			}
			cores.add(core);
		}
		super.read(state, nbt);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();

		ListNBT list = new ListNBT();
		for (Merchant core : cores) {
			try {
				list.add(NBTSerializer.serialize(core));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		nbt.put("coresList", list);

		return nbt;
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		read(state, tag);
	}

	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT nbt = pkt.getNbtCompound();
		handleUpdateTag(getBlockState(), nbt);
	}

}
