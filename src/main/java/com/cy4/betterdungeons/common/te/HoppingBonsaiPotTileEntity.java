package com.cy4.betterdungeons.common.te;

import java.util.List;

import com.cy4.betterdungeons.core.init.TileEntityTypesInit;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class HoppingBonsaiPotTileEntity extends BonsaiPotTileEntity {

	public HoppingBonsaiPotTileEntity() {
		super(TileEntityTypesInit.HOPPING_BONSAI_POT_TILE_ENTITY_TYPE.get());
	}

	@Override
	public void tick() {
		super.tick();

		if (this.saplingInfo == null || this.soilInfo == null) {
			this.updateInfoObjects();
		}

		if (this.world.isRemote || this.saplingInfo == null) {
			return;
		}
		
		if (getProgress() >= 1.0f) {
			TileEntity below = getWorld().getTileEntity(getPos().down());
			if (below != null) {
				LazyOptional<IItemHandler> cap = below.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);

				if (cap.isPresent()) {
					List<ItemStack> drops = this.saplingInfo.getRandomizedDrops(this.world.rand);

					IItemHandler targetHandler = cap.orElse(null);
					for (ItemStack drop : drops) {
						ItemHandlerHelper.insertItemStacked(targetHandler, drop, false);
					}

					this.setSapling(this.saplingStack);
				}
			}
		}
	}
}