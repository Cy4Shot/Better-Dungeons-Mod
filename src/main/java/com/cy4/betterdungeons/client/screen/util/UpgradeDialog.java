package com.cy4.betterdungeons.client.screen.util;

import com.cy4.betterdungeons.client.helper.FontHelper;
import com.cy4.betterdungeons.client.helper.Rectangle;
import com.cy4.betterdungeons.client.helper.ScrollableContainer;
import com.cy4.betterdungeons.client.helper.UIHelper;
import com.cy4.betterdungeons.client.screen.UpgradeTreeScreen;
import com.cy4.betterdungeons.common.upgrade.UpgradeGroup;
import com.cy4.betterdungeons.common.upgrade.UpgradeNode;
import com.cy4.betterdungeons.common.upgrade.UpgradeStyle;
import com.cy4.betterdungeons.common.upgrade.UpgradeTree;
import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.cy4.betterdungeons.core.init.ItemInit;
import com.cy4.betterdungeons.core.network.DungeonsNetwork;
import com.cy4.betterdungeons.core.network.message.PlayerUpgradeMessage;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;

public class UpgradeDialog extends AbstractGui {

	private Rectangle bounds;
	private UpgradeGroup<?> talentGroup;
	private UpgradeTree talentTree;

	private UpgradeTreeScreen screen;
	private UpgradeWidget abilityWidget;
	private ScrollableContainer descriptionComponent;
	private Button abilityUpgradeButton;

	public UpgradeDialog(UpgradeTree talentTree, UpgradeTreeScreen sc) {
		this.talentGroup = null;
		this.talentTree = talentTree;
		this.screen = sc;
		refreshWidgets();
	}

	public void refreshWidgets() {
		if (this.talentGroup != null) {
			UpgradeStyle abilityStyle = DungeonsConfig.UPGRADES_GUI.getStyles().get(talentGroup.getParentName());
			this.abilityWidget = new UpgradeWidget(talentGroup, talentTree, abilityStyle);

			UpgradeNode<?> talentNode = talentTree.getNodeOf(talentGroup);

			String buttonText = !talentNode.isLearned() ? "Learn (" + talentGroup.learningCost() + ")"
					: talentNode.getLevel() >= talentGroup.getMaxLevel() ? "Fully Learned"
							: "Upgrade (" + talentGroup.cost(talentNode.getLevel() + 1) + ")";

			this.abilityUpgradeButton = new Button(10, bounds.getHeight() - 40, bounds.getWidth() - 30, 20,
					new StringTextComponent(buttonText), (button) -> {
						upgradeAbility();
					}, (button, matrixStack, x, y) -> {
					});

			this.descriptionComponent = new ScrollableContainer(this::renderDescriptions);
			int cost = talentNode.getUpgrade() == null ? talentGroup.learningCost() : talentGroup.cost(talentNode.getLevel() + 1);
			this.abilityUpgradeButton.active = talentNode.getLevel() < talentGroup.getMaxLevel() && cost <= numOrbs();
		}
	}

	public int numOrbs() {
		int a = 0;
		for (ItemStack stack : this.screen.getInv().mainInventory) {
			if (stack.getItem() == ItemInit.PHAT_ORB.get()) {
				a += stack.getCount();
			}
		}

		return a;
	}

	public void setUpgradeGroup(UpgradeGroup<?> talentGroup) {
		this.talentGroup = talentGroup;
		refreshWidgets();
	}

	public UpgradeDialog setBounds(Rectangle bounds) {
		this.bounds = bounds;
		return this;
	}

	public Rectangle getHeadingBounds() {
		Rectangle abilityBounds = abilityWidget.getClickableBounds();
		Rectangle headingBounds = new Rectangle();
		headingBounds.x0 = 5;
		headingBounds.y0 = 5;
		headingBounds.x1 = headingBounds.x0 + bounds.getWidth() - 20;
		headingBounds.y1 = headingBounds.y0 + abilityBounds.getHeight() + 5;
		return headingBounds;
	}

	public Rectangle getDescriptionsBounds() {
		Rectangle headingBounds = getHeadingBounds();
		Rectangle descriptionsBounds = new Rectangle();
		descriptionsBounds.x0 = headingBounds.x0;
		descriptionsBounds.y0 = headingBounds.y1 + 10;
		descriptionsBounds.x1 = headingBounds.x1;
		descriptionsBounds.y1 = bounds.getHeight() - 50;
		return descriptionsBounds;
	}

	public void mouseMoved(int screenX, int screenY) {
		if (bounds == null)
			return;
		int containerX = screenX - bounds.x0;
		int containerY = screenY - bounds.y0;
		if (this.abilityUpgradeButton != null) {
			this.abilityUpgradeButton.mouseMoved(containerX, containerY);
		}
	}

	public void mouseClicked(int screenX, int screenY, int button) {
		int containerX = screenX - bounds.x0;
		int containerY = screenY - bounds.y0;
		if (this.abilityUpgradeButton != null) {
			this.abilityUpgradeButton.mouseClicked(containerX, containerY, button);
		}
	}

	public void mouseScrolled(double mouseX, double mouseY, double delta) {
		if (!bounds.contains((int) mouseX, (int) mouseY))
			return;
		descriptionComponent.mouseScrolled(mouseX, mouseY, delta);
	}

	public void upgradeAbility() {
		UpgradeNode<?> talentNode = this.talentTree.getNodeOf(talentGroup);
		if (talentNode.getLevel() >= talentGroup.getMaxLevel())
			return;

		int cost = talentNode.getUpgrade() == null ? talentGroup.learningCost() : talentGroup.cost(talentNode.getLevel() + 1);
		talentTree.upgradeUpgrade(null, talentNode);

		DungeonsNetwork.CHANNEL.sendToServer(new PlayerUpgradeMessage(this.talentGroup.getParentName(), cost));
		refreshWidgets();
	}

	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		matrixStack.push();
		renderBackground(matrixStack, mouseX, mouseY, partialTicks);
		if (talentGroup == null)
			return;

		matrixStack.translate(bounds.x0 + 5, bounds.y0 + 5, 0);
		renderHeading(matrixStack, mouseX, mouseY, partialTicks);
		descriptionComponent.setBounds(getDescriptionsBounds());
		descriptionComponent.render(matrixStack, mouseX, mouseY, partialTicks);
		renderFooter(matrixStack, mouseX, mouseY, partialTicks);
		matrixStack.push();
	}

	private void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		Minecraft.getInstance().getTextureManager().bindTexture(UpgradeTreeScreen.UI_RESOURCE);
		fill(matrixStack, bounds.x0 + 5, bounds.y0 + 5, bounds.x1 - 5, bounds.y1 - 5, 0xFF_C6C6C6);
		blit(matrixStack, bounds.x0, bounds.y0, 0, 44, 5, 5);
		blit(matrixStack, bounds.x1 - 5, bounds.y0, 8, 44, 5, 5);
		blit(matrixStack, bounds.x0, bounds.y1 - 5, 0, 52, 5, 5);
		blit(matrixStack, bounds.x1 - 5, bounds.y1 - 5, 8, 52, 5, 5);
		matrixStack.push();
		matrixStack.translate(bounds.x0 + 5, bounds.y0, 0);
		matrixStack.scale(bounds.getWidth() - 10, 1, 1);
		blit(matrixStack, 0, 0, 6, 44, 1, 5);
		matrixStack.translate(0, bounds.getHeight() - 5, 0);
		blit(matrixStack, 0, 0, 6, 52, 1, 5);
		matrixStack.pop();
		matrixStack.push();
		matrixStack.translate(bounds.x0, bounds.y0 + 5, 0);
		matrixStack.scale(1, bounds.getHeight() - 10, 1);
		blit(matrixStack, 0, 0, 0, 50, 5, 1);
		matrixStack.translate(bounds.getWidth() - 5, 0, 0);
		blit(matrixStack, 0, 0, 8, 50, 5, 1);
		matrixStack.pop();
	}

	private void renderHeading(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		Minecraft.getInstance().getTextureManager().bindTexture(UpgradeTreeScreen.UI_RESOURCE);
		UpgradeStyle abilityStyle = DungeonsConfig.UPGRADES_GUI.getStyles().get(talentGroup.getParentName());
		UpgradeNode<?> talentNode = talentTree.getNodeByName(talentGroup.getParentName());
		Rectangle abilityBounds = abilityWidget.getClickableBounds();
		UIHelper.renderContainerBorder(this, matrixStack, getHeadingBounds(), 14, 44, 2, 2, 2, 2, 0xFF_8B8B8B);
		String abilityName = talentNode.getLevel() == 0 ? talentNode.getGroup().getName(1) : talentNode.getName();
		String subText = talentNode.getLevel() == 0 ? "Not Learned Yet" : "Learned";
		matrixStack.push();
		matrixStack.translate(10, 0, 0);
		FontHelper.drawStringWithBorder(matrixStack, abilityName, abilityBounds.getWidth() + 5, 13,
				talentNode.getLevel() == 0 ? 0xFF_FFFFFF : 0xFF_fff8c7, talentNode.getLevel() == 0 ? 0xFF_000000 : 0xFF_3b3300);

		FontHelper.drawStringWithBorder(matrixStack, subText, abilityBounds.getWidth() + 5, 23,
				talentNode.getLevel() == 0 ? 0xFF_FFFFFF : 0xFF_fff8c7, talentNode.getLevel() == 0 ? 0xFF_000000 : 0xFF_3b3300);

		matrixStack.translate(-abilityStyle.x, -abilityStyle.y, 0);
		matrixStack.translate(abilityBounds.getWidth() / 2f, 0, 0);
		matrixStack.translate(0, 23, 0);
		abilityWidget.render(matrixStack, mouseX, mouseY, partialTicks);
		matrixStack.pop();
	}

	private void renderDescriptions(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		descriptionComponent.setInnerHeight(
				UIHelper.renderWrappedText(matrixStack, DungeonsConfig.UPGRADE_DESCRIPTIONS.getDescriptionFor(talentGroup.getParentName()),
						descriptionComponent.getRenderableBounds().getWidth(), 10) * 10 + 20);
		RenderSystem.enableDepthTest();
	}

	private void renderFooter(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		int containerX = mouseX - bounds.x0;
		int containerY = mouseY - bounds.y0;
		this.abilityUpgradeButton.render(matrixStack, containerX, containerY, partialTicks);
		Minecraft.getInstance().getTextureManager().bindTexture(UpgradeTreeScreen.UI_RESOURCE);
		UpgradeNode<?> talentNode = talentTree.getNodeOf(talentGroup);
		if (!talentNode.isLearned()) {
			blit(matrixStack, 13, bounds.getHeight() - 40 - 2, 121 + 30, 0, 15, 23);
		}
	}

}
