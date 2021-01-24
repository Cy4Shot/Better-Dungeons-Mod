package com.cy4.betterdungeons.client.screen.util;

import java.util.Collections;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.client.screen.PlayerRewardScreen;
import com.cy4.betterdungeons.core.network.DungeonsNetwork;
import com.cy4.betterdungeons.core.network.message.RewardMessage;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class UpgradeButton extends Button {

	protected static final ResourceLocation button = new ResourceLocation(BetterDungeons.MOD_ID, "textures/gui/button_sprites.png");
	public static final int BUTTON_SIZE = 32;
	public static final int ICON_SIZE = 16;

	private PlayerRewardScreen screen;
	private Block reward;

	public UpgradeButton(int x, int y, PlayerRewardScreen screen, Block reward) {
		super(x, y, BUTTON_SIZE, BUTTON_SIZE, StringTextComponent.EMPTY, button -> {
			upgrade(screen, reward);
		});
		this.screen = screen;
		this.reward = reward;
	}

	private static void upgrade(PlayerRewardScreen screen2, Block reward) {
		DungeonsNetwork.CHANNEL.sendToServer(new RewardMessage(reward));
		screen2.closeScreen();
	}

	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		Minecraft.getInstance().getTextureManager().bindTexture(button);
		if (isHovered()) {
			blit(matrixStack, x, y, 0, 32, BUTTON_SIZE, BUTTON_SIZE, 64, 64);
			screen.renderToolTip(matrixStack, Collections.singletonList(reward.getTranslatedName().func_241878_f()), mouseX, mouseY,
					screen.getMinecraft().fontRenderer);
		} else {
			blit(matrixStack, x, y, 0, 0, BUTTON_SIZE, BUTTON_SIZE, 64, 64);
		}
	}

}
