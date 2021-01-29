package com.cy4.betterdungeons.core.init;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.entity.PhatSlimeEntity;

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

	public static final RegistryObject<EntityType<PhatSlimeEntity>> PHAT_SLIME = ENTITY_TYPES.register("phat_slime",
			() -> EntityType.Builder.<PhatSlimeEntity>create(PhatSlimeEntity::new, EntityClassification.MONSTER).size(6.25f, 6.25f)
					.build(new ResourceLocation(BetterDungeons.MOD_ID, "phat_slime").toString()));

	public static void attributes() {
		GlobalEntityTypeAttributes.put(PHAT_SLIME.get(), ZombieEntity.func_234342_eQ_().create()); // Boss Attributes
	}

}
