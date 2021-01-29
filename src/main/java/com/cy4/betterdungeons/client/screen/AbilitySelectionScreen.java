package com.cy4.betterdungeons.client.screen;

import java.util.LinkedList;
import java.util.List;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.client.overlay.AbilitiesOverlay;
import com.cy4.betterdungeons.client.screen.util.AbilitySelectionWidget;
import com.cy4.betterdungeons.common.upgrade.UpgradeNode;
import com.cy4.betterdungeons.core.network.DungeonsNetwork;
import com.cy4.betterdungeons.core.network.message.UpgradeKeyMessage;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class AbilitySelectionScreen extends Screen {

	public static final ResourceLocation HUD_RESOURCE = new ResourceLocation(BetterDungeons.MOD_ID, "textures/gui/ability_hud.png");

	public AbilitySelectionScreen() {
		super(new StringTextComponent(""));
	}

	public List<AbilitySelectionWidget> getAbilitiesAsWidgets() {
		List<AbilitySelectionWidget> abilityWidgets = new LinkedList<>();

		Minecraft minecraft = Minecraft.getInstance();

		float midX = minecraft.getMainWindow().getScaledWidth() / 2f;
		float midY = minecraft.getMainWindow().getScaledHeight() / 2f;
		float radius = 50;

		List<UpgradeNode<?>> learnedAbilities = AbilitiesOverlay.learnedAbilities;
		for (int i = 0; i < learnedAbilities.size(); i++) {
			UpgradeNode<?> ability = learnedAbilities.get(i);
			double angle = i * (2 * Math.PI / learnedAbilities.size()) - Math.PI / 2;
			double x = radius * Math.cos(angle) + midX;
			double y = radius * Math.sin(angle) + midY;

			AbilitySelectionWidget widget = new AbilitySelectionWidget((int) x, (int) y, ability);
			abilityWidgets.add(widget);
		}

		return abilityWidgets;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		for (AbilitySelectionWidget widget : getAbilitiesAsWidgets()) {
			if (widget.isMouseOver(mouseX, mouseY)) {
				requestSwap(widget.getUpgradeNode());
				closeScreen();
				return true;
			}
		}

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {

		return super.keyReleased(keyCode, scanCode, modifiers);
	}

	public void requestSwap(UpgradeNode<?> abilityNode) {
		int abilityIndex = AbilitiesOverlay.learnedAbilities.indexOf(abilityNode);
		if (abilityIndex != AbilitiesOverlay.focusedIndex) {
			DungeonsNetwork.CHANNEL.sendToServer(new UpgradeKeyMessage(abilityIndex));
		}
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);

		Minecraft minecraft = Minecraft.getInstance();

		float midX = minecraft.getMainWindow().getScaledWidth() / 2f;
		float midY = minecraft.getMainWindow().getScaledHeight() / 2f;
		float radius = 50;

		List<AbilitySelectionWidget> abilitiesAsWidgets = getAbilitiesAsWidgets();
		for (int i = 0; i < abilitiesAsWidgets.size(); i++) {
			AbilitySelectionWidget widget = abilitiesAsWidgets.get(i);
			widget.render(matrixStack, mouseX, mouseY, partialTicks);

			if (widget.isMouseOver(mouseX, mouseY)) {
				String abilityName = widget.getUpgradeNode().getName();
				int abilityNameWidth = minecraft.fontRenderer.getStringWidth(abilityName);
				minecraft.fontRenderer.drawStringWithShadow(matrixStack, abilityName, midX - abilityNameWidth / 2f, midY - (radius + 35),
						0x00_FFFFFF);

				if (i == AbilitiesOverlay.focusedIndex) {
					String text = "Currently Focused Ability";
					int textWidth = minecraft.fontRenderer.getStringWidth(text);
					minecraft.fontRenderer.drawStringWithShadow(matrixStack, text, midX - textWidth / 2f, midY + (radius + 15),
							0x00_ABEABE);
				}
			}
		}

		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

}