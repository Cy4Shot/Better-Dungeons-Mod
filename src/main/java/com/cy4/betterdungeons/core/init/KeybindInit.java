package com.cy4.betterdungeons.core.init;

import java.awt.event.KeyEvent;

import com.cy4.betterdungeons.BetterDungeons;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
public class KeybindInit {
	public static KeyBinding openAbilityTree;

	public static void register(final FMLClientSetupEvent event) {
		openAbilityTree = createKeyBinding("open_ability_tree", KeyEvent.VK_H);
		ClientRegistry.registerKeyBinding(openAbilityTree);
	}

	private static KeyBinding createKeyBinding(String name, int key) {
		return new KeyBinding("key." + BetterDungeons.MOD_ID + "." + name, key, "key.category." + BetterDungeons.MOD_ID);
	}
}
