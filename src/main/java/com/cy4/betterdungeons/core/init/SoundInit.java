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
	public static final RegistryObject<SoundEvent> TIMER_PANIC = registerSound("timer_panic");
	public static final RegistryObject<SoundEvent> BOSS = registerSound("boss");
	public static final RegistryObject<SoundEvent> AMBIENT = registerSound("ambient");
	public static final RegistryObject<SoundEvent> AMBIENT_LOOP = registerSound("ambient_loop");	

	public static RegistryObject<SoundEvent> registerSound(String id) {
		return SOUNDS.register(id, () -> new SoundEvent(new ResourceLocation(BetterDungeons.MOD_ID, id)));
	}

}
