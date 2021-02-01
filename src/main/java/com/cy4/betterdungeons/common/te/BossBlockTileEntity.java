package com.cy4.betterdungeons.common.te;

import javax.annotation.Nullable;

import com.cy4.betterdungeons.core.init.TileEntityTypesInit;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class BossBlockTileEntity extends TileEntity {

	private int keys = 0;

	public BossBlockTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	public BossBlockTileEntity() {
		this(TileEntityTypesInit.BOSS_BLOCK_TILE_ENTITY_TYPE.get());
	}

	public int getKeys() {
		return keys;
	}

	public void setKeys(int keys) {
		this.keys = keys;
	}
	
	public void addKey() {
		this.keys++;
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("keys", keys);
		return super.write(compound);
	}

	@Override
	public void read(BlockState state, CompoundNBT compound) {
		keys = compound.getInt("keys");
		super.read(state, compound);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		tag.putInt("keys", keys);
		return tag;
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
		CompoundNBT tag = pkt.getNbtCompound();
		handleUpdateTag(getBlockState(), tag);
	}

}
