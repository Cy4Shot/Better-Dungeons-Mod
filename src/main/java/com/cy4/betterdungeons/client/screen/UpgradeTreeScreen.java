package com.cy4.betterdungeons.client.screen;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.client.helper.Rectangle;
import com.cy4.betterdungeons.client.helper.UIHelper;
import com.cy4.betterdungeons.client.screen.util.UpgradeDialog;
import com.cy4.betterdungeons.client.tab.UpgradeTab;
import com.cy4.betterdungeons.client.tab.UpgradeTabBase;
import com.cy4.betterdungeons.common.container.UpgradeTreeContainer;
import com.cy4.betterdungeons.common.upgrade.UpgradeTree;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class UpgradeTreeScreen extends ContainerScreen<UpgradeTreeContainer> {

	public static final ResourceLocation UI_RESOURCE = new ResourceLocation(BetterDungeons.MOD_ID, "textures/gui/scrollable.png");
	public static final ResourceLocation BACKGROUNDS_RESOURCE = new ResourceLocation(BetterDungeons.MOD_ID,
			"textures/gui/upgrade_screen.png");

	public static final int TAB_WIDTH = 28;
	public static final int GAP = 3;

	protected UpgradeTabBase activeTab;
	protected UpgradeDialog upgradeDialog;
	private PlayerInventory inv;

	public UpgradeTreeScreen(UpgradeTreeContainer container, PlayerInventory inventory, ITextComponent title) {
		super(container, inventory, new StringTextComponent("Upgrade Tree Screen!"));

		this.activeTab = new UpgradeTab(this);
		UpgradeTree upgradeTree = getContainer().getUpgradeTree();
		this.upgradeDialog = new UpgradeDialog(upgradeTree, this);
		this.inv = inventory;

		refreshWidgets();

		xSize = 270;
		ySize = 200;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	protected void init() {
		xSize = width; // f in the chat for jei. you will be missed.
		super.init();
	}

	public void refreshWidgets() {
		this.activeTab.refresh();
		if (this.upgradeDialog != null) {
			this.upgradeDialog.refreshWidgets();
		}
	}

	public Rectangle getContainerBounds() {
		Rectangle bounds = new Rectangle();
		bounds.x0 = 30; // px
		bounds.y0 = 60; // px
		bounds.x1 = (int) (width * 0.55);
		bounds.y1 = height - 30;
		return bounds;
	}

	public Rectangle getTabBounds(int index, boolean active) {
		Rectangle containerBounds = getContainerBounds();
		Rectangle bounds = new Rectangle();
		bounds.x0 = containerBounds.x0 + 5 + index * (TAB_WIDTH + GAP);
		bounds.y0 = containerBounds.y0 - 25 - (active ? 21 : 17);
		bounds.setWidth(TAB_WIDTH);
		bounds.setHeight(active ? 32 : 25);
		return bounds;
	}

	public UpgradeDialog getUpgradeDialog() {
		return upgradeDialog;
	}

	/* --------------------------------------------------- */

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		Rectangle containerBounds = getContainerBounds();

		if (containerBounds.contains((int) mouseX, (int) mouseY)) {
			this.activeTab.mouseClicked(mouseX, mouseY, button);

		} else {
			Rectangle upgradesTabBounds = getTabBounds(1, activeTab instanceof UpgradeTab);

			if (upgradesTabBounds.contains(((int) mouseX), ((int) mouseY))) {
				this.activeTab.onClose();
				this.activeTab = new UpgradeTab(this);
				this.refreshWidgets();
			} else if (activeTab instanceof UpgradeTab) {
				this.upgradeDialog.mouseClicked((int) mouseX, (int) mouseY, button);
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		this.activeTab.mouseReleased(mouseX, mouseY, button);
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		this.activeTab.mouseMoved(mouseX, mouseY);
		if (activeTab instanceof UpgradeTab) {
			this.upgradeDialog.mouseMoved((int) mouseX, (int) mouseY);
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		if (getContainerBounds().contains((int) mouseX, (int) mouseY)) {
			this.activeTab.mouseScrolled(mouseX, mouseY, delta);
		} else if (activeTab instanceof UpgradeTab) {
			this.upgradeDialog.mouseScrolled(mouseX, mouseY, delta);
		}

		return super.mouseScrolled(mouseX, mouseY, delta);
	}

	@Override
	public void onClose() {
		this.activeTab.onClose();
	}

	/* --------------------------------------------------- */

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
		renderBackground(matrixStack);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
		// For some reason, without this it won't render :V
		this.font.func_243248_b(matrixStack, new StringTextComponent(""), (float) this.titleX, (float) this.titleY, 4210752);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		super.render(matrixStack, mouseX, mouseY, partialTicks);

		UIHelper.renderOverflowHidden(matrixStack, this::renderContainerBackground,
				ms -> activeTab.render(ms, mouseX, mouseY, partialTicks));

		Rectangle containerBounds = getContainerBounds();
		renderContainerBorders(matrixStack);
		renderContainerTabs(matrixStack);
		Rectangle dialogBounds = new Rectangle();
		dialogBounds.x0 = containerBounds.x1 + 15;
		dialogBounds.y0 = containerBounds.y0 - 18;
		dialogBounds.x1 = width - 21;
		dialogBounds.y1 = height - 21;

		upgradeDialog.setBounds(dialogBounds);

		if (activeTab instanceof UpgradeTab) {
			upgradeDialog.render(matrixStack, mouseX, mouseY, partialTicks);
		}
	}

	private void renderContainerTabs(MatrixStack matrixStack) {
		Rectangle containerBounds = getContainerBounds();

		if (activeTab instanceof UpgradeTab)
			minecraft.fontRenderer.drawString(matrixStack, "Upgrades", containerBounds.x0, containerBounds.y0 - 12, 0xFF_3f3f3f);

		minecraft.getProfiler().endSection();

	}

	private void renderContainerBorders(MatrixStack matrixStack) {
		assert this.minecraft != null;
		this.minecraft.getTextureManager().bindTexture(UI_RESOURCE);

		Rectangle containerBounds = getContainerBounds();

		RenderSystem.enableBlend();

		blit(matrixStack, containerBounds.x0 - 9, containerBounds.y0 - 18, 0, 0, 15, 24);
		blit(matrixStack, containerBounds.x1 - 7, containerBounds.y0 - 18, 18, 0, 15, 24);
		blit(matrixStack, containerBounds.x0 - 9, containerBounds.y1 - 7, 0, 27, 15, 16);
		blit(matrixStack, containerBounds.x1 - 7, containerBounds.y1 - 7, 18, 27, 15, 16);

		matrixStack.push();
		matrixStack.translate(containerBounds.x0 + 6, containerBounds.y0 - 18, 0);
		matrixStack.scale(containerBounds.x1 - containerBounds.x0 - 13, 1, 1);
		blit(matrixStack, 0, 0, 16, 0, 1, 24);
		matrixStack.translate(0, containerBounds.y1 - containerBounds.y0 + 11, 0);
		blit(matrixStack, 0, 0, 16, 27, 1, 16);
		matrixStack.pop();

		matrixStack.push();
		matrixStack.translate(containerBounds.x0 - 9, containerBounds.y0 + 6, 0);
		matrixStack.scale(1, containerBounds.y1 - containerBounds.y0 - 13, 1);
		blit(matrixStack, 0, 0, 0, 25, 15, 1);
		matrixStack.translate(containerBounds.x1 - containerBounds.x0 + 2, 0, 0);
		blit(matrixStack, 0, 0, 18, 25, 15, 1);
		matrixStack.pop();
	}

	private void renderContainerBackground(MatrixStack matrixStack) {
		assert this.minecraft != null;

		this.minecraft.getTextureManager().bindTexture(BACKGROUNDS_RESOURCE);

		Rectangle containerBounds = getContainerBounds();

		// TODO: Include scale param
		int textureSize = 16;
		int currentX = containerBounds.x0;
		int currentY = containerBounds.y0;
		int uncoveredWidth = containerBounds.getWidth();
		int uncoveredHeight = containerBounds.getHeight();
		while (uncoveredWidth > 0) {
			while (uncoveredHeight > 0) {
				blit(matrixStack, currentX, currentY, 16 * 5, 0, // TODO: <-- depends on tab
						Math.min(textureSize, uncoveredWidth), Math.min(textureSize, uncoveredHeight));
				uncoveredHeight -= textureSize;
				currentY += textureSize;
			}

			// Decrement
			uncoveredWidth -= textureSize;
			currentX += textureSize;

			// Reset
			uncoveredHeight = containerBounds.getHeight();
			currentY = containerBounds.y0;
		}
	}

	public PlayerInventory getInv() {
		return inv;
	}

}