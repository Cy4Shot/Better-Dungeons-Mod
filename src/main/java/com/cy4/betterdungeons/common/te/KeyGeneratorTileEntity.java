package com.cy4.betterdungeons.common.te;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

import com.cy4.betterdungeons.core.init.TileEntityTypesInit;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class KeyGeneratorTileEntity extends TileEntity {

	public Date lastRecievedDate = Calendar.getInstance().getTime();

	public KeyGeneratorTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	public KeyGeneratorTileEntity() {
		this(TileEntityTypesInit.KEY_GENERATOR_TILE_ENTITY_TYPE.get());
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		this.lastRecievedDate = new Date(nbt.getLong("lrd"));
	}

	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.putLong("lrd", lastRecievedDate.getTime());
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
}
