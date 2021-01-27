package com.cy4.betterdungeons.client.screen.util;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.client.helper.Rectangle;
import com.cy4.betterdungeons.client.overlay.AbilitiesOverlay;
import com.cy4.betterdungeons.common.upgrade.UpgradeNode;
import com.cy4.betterdungeons.common.upgrade.UpgradeStyle;
import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class AbilitySelectionWidget extends Widget {

	public static final ResourceLocation HUD_RESOURCE = new ResourceLocation(BetterDungeons.MOD_ID, "textures/gui/ability_hud.png");
	private static final ResourceLocation ABILITIES_RESOURCE = new ResourceLocation(BetterDungeons.MOD_ID,
			"textures/gui/upgrade_sprites.png");

	protected UpgradeNode<?> abilityNode;

	public AbilitySelectionWidget(int x, int y, UpgradeNode<?> abilityNode) {
		super(x, y, 24, 24, new StringTextComponent(abilityNode.getName()));
		this.abilityNode = abilityNode;
	}

	public UpgradeNode<?> getUpgradeNode() {
		return abilityNode;
	}

	public Rectangle getBounds() {
		Rectangle bounds = new Rectangle();
		bounds.x0 = x - 12;
		bounds.y0 = y - 12;
		bounds.setWidth(width);
		bounds.setHeight(height);
		return bounds;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return getBounds().contains((int) mouseX, (int) mouseY);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		Rectangle bounds = getBounds();

		Minecraft minecraft = Minecraft.getInstance();

		UpgradeStyle abilityStyle = DungeonsConfig.UPGRADES_GUI.getStyles().get(abilityNode.getGroup().getParentName());

		int abilityIndex = AbilitiesOverlay.learnedAbilities.indexOf(abilityNode);
		int cooldown = AbilitiesOverlay.cooldowns.getOrDefault(abilityIndex, 0);

		if (AbilitiesOverlay.focusedIndex == abilityIndex) {
			GlStateManager.color4f(0.7f, 0.7f, 0.7f, 0.3f);

		} else {
			GlStateManager.color4f(1f, 1f, 1f, 1f);
		}

		RenderSystem.enableBlend();

		minecraft.getTextureManager().bindTexture(HUD_RESOURCE);
		blit(matrixStack, bounds.x0 + 1, bounds.y0 + 1, 28, 36, 22, 22);

		minecraft.getTextureManager().bindTexture(ABILITIES_RESOURCE);
		blit(matrixStack, bounds.x0 + 4, bounds.y0 + 4, abilityStyle.u, abilityStyle.v, 16, 16);

		if (cooldown > 0) {
			GlStateManager.color4f(0.7f, 0.7f, 0.7f, 0.5f);
			float cooldownPercent = (float) cooldown / DungeonsConfig.UPGRADES.cooldownOf(abilityNode);
			int cooldownHeight = (int) (16 * cooldownPercent);
			AbstractGui.fill(matrixStack, bounds.x0 + 4, bounds.y0 + 4 + (16 - cooldownHeight), bounds.x0 + 4 + 16, bounds.y0 + 4 + 16,
					0x99_FFFFFF);
			RenderSystem.enableBlend();
		}

		if (AbilitiesOverlay.focusedIndex == abilityIndex) {
			minecraft.getTextureManager().bindTexture(HUD_RESOURCE);
			blit(matrixStack, bounds.x0, bounds.y0, 64 + 25, 13, 24, 24);

		} else if (bounds.contains(mouseX, mouseY)) {
			GlStateManager.color4f(1f, 1f, 1f, 1f);
			minecraft.getTextureManager().bindTexture(HUD_RESOURCE);
			blit(matrixStack, bounds.x0, bounds.y0, 64 + (cooldown > 0 ? 50 : 0), 13, 24, 24);
		}
	}

}