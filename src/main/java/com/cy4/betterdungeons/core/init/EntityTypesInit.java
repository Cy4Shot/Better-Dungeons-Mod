package com.cy4.betterdungeons.core.init;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.entity.EnderSlimeEntity;
import com.cy4.betterdungeons.common.entity.SlimeSpikesEntity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityTypesInit {

	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES,
			BetterDungeons.MOD_ID);

	public static final RegistryObject<EntityType<EnderSlimeEntity>> ENDER_SLIME = ENTITY_TYPES.register("ender_slime",
			() -> EntityType.Builder.<EnderSlimeEntity>create(EnderSlimeEntity::new, EntityClassification.MONSTER).size(6.25f, 6.25f)
					.build(new ResourceLocation(BetterDungeons.MOD_ID, "ender_slime").toString()));
	public static final RegistryObject<EntityType<SlimeSpikesEntity>> SLIME_SPIKES = ENTITY_TYPES.register("slime_spikes",
			() -> EntityType.Builder.<SlimeSpikesEntity>create(SlimeSpikesEntity::new, EntityClassification.MISC).size(1f, 1f)
					.build(new ResourceLocation(BetterDungeons.MOD_ID, "slime_spikes").toString()));

	public static void attributes() {
		GlobalEntityTypeAttributes.put(ENDER_SLIME.get(), ZombieEntity.func_234342_eQ_().create()); // Boss Attributes
	}

}
