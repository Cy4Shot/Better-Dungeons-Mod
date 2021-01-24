package com.cy4.betterdungeons.client.tab;

import java.util.LinkedList;
import java.util.List;

import com.cy4.betterdungeons.client.screen.UpgradeTreeScreen;
import com.cy4.betterdungeons.client.screen.util.UpgradeWidget;
import com.cy4.betterdungeons.common.upgrade.UpgradeTree;
import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.StringTextComponent;

public class UpgradeTab extends UpgradeTabBase {

	private List<UpgradeWidget> upgradeWidgets;
	private UpgradeWidget selectedWidget;

	public UpgradeTab(UpgradeTreeScreen parentScreen) {
		super(parentScreen, new StringTextComponent("Upgrades Tab"));
		this.upgradeWidgets = new LinkedList<>();
	}

	public void refresh() {
		this.upgradeWidgets.clear();

		UpgradeTree upgradeTree = parentScreen.getContainer().getUpgradeTree();
		DungeonsConfig.UPGRADES_GUI.getStyles().forEach((abilityName, style) -> {
			this.upgradeWidgets.add(new UpgradeWidget(DungeonsConfig.UPGRADES.getByName(abilityName), upgradeTree, style));
		});
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean mouseClicked = super.mouseClicked(mouseX, mouseY, button);

		Vector2f midpoint = parentScreen.getContainerBounds().midpoint();
		int containerMouseX = (int) ((mouseX - midpoint.x) / viewportScale - viewportTranslation.x);
		int containerMouseY = (int) ((mouseY - midpoint.y) / viewportScale - viewportTranslation.y);
		for (UpgradeWidget abilityWidget : upgradeWidgets) {
			if (abilityWidget.isMouseOver(containerMouseX, containerMouseY)
					&& abilityWidget.mouseClicked(containerMouseX, containerMouseY, button)) {
				if (this.selectedWidget != null)
					this.selectedWidget.deselect();
				this.selectedWidget = abilityWidget;
				this.selectedWidget.select();
				parentScreen.getUpgradeDialog().setUpgradeGroup(this.selectedWidget.getUpgradeGroup());
				break;
			}
		}

		return mouseClicked;
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		RenderSystem.enableBlend();

		Vector2f midpoint = parentScreen.getContainerBounds().midpoint();

		matrixStack.push();
		matrixStack.translate(midpoint.x, midpoint.y, 0);
		matrixStack.scale(viewportScale, viewportScale, 1);
		matrixStack.translate(viewportTranslation.x, viewportTranslation.y, 0);

		int containerMouseX = (int) ((mouseX - midpoint.x) / viewportScale - viewportTranslation.x);
		int containerMouseY = (int) ((mouseY - midpoint.y) / viewportScale - viewportTranslation.y);

		for (UpgradeWidget abilityWidget : upgradeWidgets) {
			abilityWidget.render(matrixStack, containerMouseX, containerMouseY, partialTicks);
		}

		matrixStack.pop();
	}

}