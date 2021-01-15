package com.cy4.betterdungeons.core.init;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.container.KeyGeneratorContainer;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainerTypesInit {
	
	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(
			ForgeRegistries.CONTAINERS, BetterDungeons.MOD_ID);

	public static final RegistryObject<ContainerType<KeyGeneratorContainer>> KEY_GENERATOR_CONTAINER = CONTAINER_TYPES
			.register("key_generator", () -> IForgeContainerType.create(KeyGeneratorContainer::new));

}
