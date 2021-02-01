package com.cy4.betterdungeons.common.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.cy4.betterdungeons.common.block.DungeonCrateBlock;
import com.cy4.betterdungeons.core.init.TileEntityTypesInit;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class DungeonCrateTileEntity extends TileEntity {


    private ItemStackHandler itemHandler = createHandler();
    private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    public DungeonCrateTileEntity() {
        super(TileEntityTypesInit.DUNGEON_CRATE_TILE_ENTITY_TYPE.get());
    }


    public void sendUpdates() {
        this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
        this.world.notifyNeighborsOfStateChange(pos, this.getBlockState().getBlock());
        markDirty();
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("inv", itemHandler.serializeNBT());
        return super.write(compound);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        itemHandler.deserializeNBT(nbt.getCompound("inv"));
        super.read(state, nbt);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(27) {

            @Override
            protected void onContentsChanged(int slot) {
                sendUpdates();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock ||
                        Block.getBlockFromItem(stack.getItem()) instanceof DungeonCrateBlock) {
                    return false;
                }
                return true;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {

                return super.insertItem(slot, stack, simulate);
            }
        };
    }


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    public CompoundNBT saveToNbt() {
        return itemHandler.serializeNBT();
    }

    public void loadFromNBT(CompoundNBT nbt) {
        itemHandler.deserializeNBT(nbt);
    }
}