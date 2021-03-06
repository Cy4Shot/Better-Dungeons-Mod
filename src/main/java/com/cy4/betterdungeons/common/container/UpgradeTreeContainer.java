package com.cy4.betterdungeons.common.container;

import com.cy4.betterdungeons.common.upgrade.UpgradeTree;
import com.cy4.betterdungeons.core.init.ContainerTypesInit;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

public class UpgradeTreeContainer extends Container {

	private UpgradeTree upgradeTree;

	public UpgradeTreeContainer(int windowId, UpgradeTree upgradeTree) {
		super(ContainerTypesInit.UPGRADE_TREE_CONTAINER.get(), windowId);
		this.upgradeTree = upgradeTree;
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return true;
	}

	public UpgradeTree getUpgradeTree() {
		return upgradeTree;
	}
}
