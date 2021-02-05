package com.cy4.betterdungeons.common.item;

import com.cy4.betterdungeons.common.block.DungeonPortalBlock;
import com.cy4.betterdungeons.core.init.BlockInit;
import com.cy4.betterdungeons.core.init.ItemInit;

import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DungeonKeyItem extends Item {

	public DungeonKeyItem() {
		super(ItemInit.basicItem().maxStackSize(1));
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		if (context.getPlayer() != null) {
			if (context.getPlayer().world.getDimensionKey() == World.OVERWORLD) {
				for (Direction direction : Direction.Plane.VERTICAL) {
					BlockPos framePos = context.getPos().offset(direction);
					if (((DungeonPortalBlock) BlockInit.DUNGEON_PORTAL.get()).trySpawnPortal(context.getWorld(), framePos)) {
						context.getItem().split(1);
						return ActionResultType.CONSUME;
					} else
						return ActionResultType.FAIL;
				}
			}
		}
		return ActionResultType.FAIL;
	}

}
