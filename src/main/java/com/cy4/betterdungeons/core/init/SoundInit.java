package com.cy4.betterdungeons.core.init;

import com.cy4.betterdungeons.BetterDungeons;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundInit {

	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BetterDungeons.MOD_ID);

	public static final RegistryObject<SoundEvent> LEVEL_UP = registerSound("level_up");

	public static RegistryObject<SoundEvent> registerSound(String id) {
		return SOUNDS.register(id, () -> new SoundEvent(new ResourceLocation(BetterDungeons.MOD_ID, id)));
	}

}
