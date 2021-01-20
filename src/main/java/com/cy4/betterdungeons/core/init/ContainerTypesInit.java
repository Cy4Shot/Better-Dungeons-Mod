package com.cy4.betterdungeons.core.init;

import java.util.Optional;
import java.util.UUID;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.container.KeyGeneratorContainer;
import com.cy4.betterdungeons.common.container.UpgradeContainer;
import com.cy4.betterdungeons.common.upgrade.UpgradeTree;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainerTypesInit {

	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS,
			BetterDungeons.MOD_ID);

	public static final RegistryObject<ContainerType<KeyGeneratorContainer>> KEY_GENERATOR_CONTAINER = CONTAINER_TYPES
			.register("key_generator", () -> IForgeContainerType.create(KeyGeneratorContainer::new));

	public static final RegistryObject<ContainerType<UpgradeContainer>> UPGRADE_CONTAINER = CONTAINER_TYPES.register("upgrade_container",
			() -> createContainerType((windowId, inventory, buffer) -> {
				UUID uniqueID = inventory.player.getUniqueID();
				UpgradeTree upgradeTree = new UpgradeTree(uniqueID);
				upgradeTree.deserializeNBT(Optional.ofNullable(buffer.readCompoundTag()).orElse(new CompoundNBT()));
				return new UpgradeContainer(windowId, upgradeTree);
			}));

	private static <T extends Container> ContainerType<T> createContainerType(IContainerFactory<T> factory) {
		return new ContainerType<T>(factory);
	}

}
