package com.cy4.betterdungeons.client.overlay;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.core.util.AnimationTwoPhased;
import com.cy4.betterdungeons.core.util.FontHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class DungeonLevelOverlay {

	public static final ResourceLocation RESOURCE = new ResourceLocation(BetterDungeons.MOD_ID,
			"textures/gui/dungeons_bar.png");

	public static int dungeonLevel;
	public static int dungeonExp, tnl;

	public static AnimationTwoPhased expGainedAnimation = new AnimationTwoPhased(0f, 1f, 0f, 500);
	public static long previousTick = System.currentTimeMillis();

	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public static void onPostRender(RenderGameOverlayEvent.Post event) {
		if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR)
			return; // Render only on POTION_ICONS

		long now = System.currentTimeMillis();

		MatrixStack matrixStack = event.getMatrixStack();
		Minecraft minecraft = Minecraft.getInstance();
		int midX = minecraft.getMainWindow().getScaledWidth() / 2;
		int bottom = minecraft.getMainWindow().getScaledHeight();

		String text = String.valueOf(dungeonLevel);
		int textX = midX + 50 - (minecraft.fontRenderer.getStringWidth(text) / 2);
		int textY = bottom - 54;
		int barWidth = 85;
		float expPercentage = (float) dungeonExp / tnl;

		expGainedAnimation.tick((int) (now - previousTick));
		previousTick = now;

		minecraft.getProfiler().startSection("dungeonBar");
		minecraft.getTextureManager().bindTexture(RESOURCE);
		RenderSystem.enableBlend();
		minecraft.ingameGUI.blit(matrixStack, midX + 9, bottom - 48, 1, 1, barWidth, 5);
		if (expGainedAnimation.getValue() != 0) {
			GlStateManager.color4f(1, 1, 1, expGainedAnimation.getValue());
			minecraft.ingameGUI.blit(matrixStack, midX + 8, bottom - 49, 62, 41, 84, 7);
			GlStateManager.color4f(1, 1, 1, 1);
		}

		minecraft.ingameGUI.blit(matrixStack, midX + 9, bottom - 48, 1, 7, (int) (barWidth * expPercentage), 5);
		if (expGainedAnimation.getValue() != 0) {
			GlStateManager.color4f(1, 1, 1, expGainedAnimation.getValue());
			minecraft.ingameGUI.blit(matrixStack, midX + 8, bottom - 49, 62, 49, (int) (barWidth * expPercentage), 7);
			GlStateManager.color4f(1, 1, 1, 1);
		}

		FontHelper.drawStringWithBorder(matrixStack, text, textX, textY, 0xFF_ffe637, 0x3c3400);
		minecraft.getProfiler().endSection();
	}

}
