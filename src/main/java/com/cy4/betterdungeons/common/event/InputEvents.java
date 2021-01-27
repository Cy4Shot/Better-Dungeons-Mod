package com.cy4.betterdungeons.common.event;

import org.lwjgl.glfw.GLFW;

import com.cy4.betterdungeons.client.overlay.AbilitiesOverlay;
import com.cy4.betterdungeons.core.init.KeybindInit;
import com.cy4.betterdungeons.core.network.DungeonsNetwork;
import com.cy4.betterdungeons.core.network.message.OpenUpgradeTreeMessage;
import com.cy4.betterdungeons.core.network.message.UpgradeKeyMessage;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class InputEvents {

	@SubscribeEvent
	public static void onKey(InputEvent.KeyInputEvent event) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.world == null)
			return;
		onInput(minecraft, event.getKey(), event.getAction());
	}

	@SubscribeEvent
	public static void onMouse(InputEvent.MouseInputEvent event) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.world == null)
			return;
		onInput(minecraft, event.getButton(), event.getAction());
	}

	private static void onInput(Minecraft minecraft, int key, int action) {
		if (minecraft.currentScreen == null && KeybindInit.abilityWheelKey.isKeyDown()) {
			if (AbilitiesOverlay.learnedAbilities == null || AbilitiesOverlay.learnedAbilities.size() <= 2)
				return;
//			minecraft.displayGuiScreen(new AbilitySelectionScreen());
			DungeonsNetwork.CHANNEL.sendToServer(new UpgradeKeyMessage(true));

		} else if (minecraft.currentScreen == null && KeybindInit.openAbilityTree.isPressed()) {
			DungeonsNetwork.CHANNEL.sendToServer(new OpenUpgradeTreeMessage());

		} else if (minecraft.currentScreen == null && KeybindInit.abilityKey.getKey().getKeyCode() == key) {
			if (action == GLFW.GLFW_RELEASE) {
				DungeonsNetwork.CHANNEL.sendToServer(new UpgradeKeyMessage(true, false, false, false));
			} else if (action == GLFW.GLFW_PRESS) {
				DungeonsNetwork.CHANNEL.sendToServer(new UpgradeKeyMessage(false, true, false, false));
			}
		}
	}

	@SubscribeEvent
	public static void onMouseScroll(InputEvent.MouseScrollEvent event) {
		Minecraft minecraft = Minecraft.getInstance();

		if (minecraft.world == null)
			return;

		double scrollDelta = event.getScrollDelta();

		if (KeybindInit.abilityKey.isKeyDown()) {
			if (minecraft.currentScreen == null) {
				if (scrollDelta < 0) {
					DungeonsNetwork.CHANNEL.sendToServer(new UpgradeKeyMessage(false, false, false, true));

				} else {
					DungeonsNetwork.CHANNEL.sendToServer(new UpgradeKeyMessage(false, false, true, false));
				}
			}
			event.setCanceled(true);
		}
	}
}