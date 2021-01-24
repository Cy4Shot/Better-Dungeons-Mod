package com.cy4.betterdungeons.client.screen;

import java.util.LinkedList;
import java.util.List;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.client.helper.Rectangle;
import com.cy4.betterdungeons.client.helper.ScrollableContainer;
import com.cy4.betterdungeons.client.screen.util.MerchantWidget;
import com.cy4.betterdungeons.common.container.DungeonMerchantContainer;
import com.cy4.betterdungeons.common.merchant.Merchant;
import com.cy4.betterdungeons.common.merchant.Trade;
import com.cy4.betterdungeons.core.network.DungeonsNetwork;
import com.cy4.betterdungeons.core.network.message.MerchantUIMessage;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;

public class DungeonMerchantScreen extends ContainerScreen<DungeonMerchantContainer> {

	public static final ResourceLocation HUD_RESOURCE = new ResourceLocation(BetterDungeons.MOD_ID, "textures/gui/dungeon_merchant.png");

	public ScrollableContainer tradesContainer;
	public List<MerchantWidget> tradeWidgets;

	public DungeonMerchantScreen(DungeonMerchantContainer screenContainer, PlayerInventory inv, ITextComponent title) {
		super(screenContainer, inv, new StringTextComponent("Vending Machine"));

		tradesContainer = new ScrollableContainer(this::renderTrades);
		tradeWidgets = new LinkedList<>();

		List<Merchant> cores = screenContainer.getTileEntity().getCores();

		for (int i = 0; i < cores.size(); i++) {
			Merchant traderCore = cores.get(i);
			int x = 0;
			int y = i * MerchantWidget.BUTTON_HEIGHT;
			tradeWidgets.add(new MerchantWidget(x, y, traderCore, this));
		}

		xSize = 394;
		ySize = 170;
	}

	public Rectangle getTradeBoundaries() {
		float midX = width / 2f;
		float midY = height / 2f;

		Rectangle boundaries = new Rectangle();
		boundaries.x0 = (int) (midX - 134);
		boundaries.y0 = (int) (midY - 66);
		boundaries.setWidth(100);
		boundaries.setHeight(142);

		return boundaries;
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		Rectangle tradeBoundaries = getTradeBoundaries();

		double tradeContainerX = mouseX - tradeBoundaries.x0;
		double tradeContainerY = mouseY - tradeBoundaries.y0;

		for (MerchantWidget tradeWidget : tradeWidgets) {
			tradeWidget.mouseMoved(tradeContainerX, tradeContainerY);
		}

		tradesContainer.mouseMoved(mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		Rectangle tradeBoundaries = getTradeBoundaries();

		double tradeContainerX = mouseX - tradeBoundaries.x0;
		double tradeContainerY = mouseY - tradeBoundaries.y0 + tradesContainer.getyOffset();

		for (int i = 0; i < tradeWidgets.size(); i++) {
			MerchantWidget tradeWidget = tradeWidgets.get(i);
			boolean isHovered = tradeWidget.x <= tradeContainerX && tradeContainerX <= tradeWidget.x + MerchantWidget.BUTTON_WIDTH
					&& tradeWidget.y <= tradeContainerY && tradeContainerY <= tradeWidget.y + MerchantWidget.BUTTON_HEIGHT;

			if (isHovered) {
				getContainer().selectTrade(i);
				DungeonsNetwork.CHANNEL.sendToServer(MerchantUIMessage.selectTrade(i));
				Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1f));
			}
		}

		tradesContainer.mouseClicked(mouseX, mouseY, button);

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		tradesContainer.mouseReleased(mouseX, mouseY, button);
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		tradesContainer.mouseScrolled(mouseX, mouseY, delta);
		return true;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
		// For some reason, without this it won't render :V
		this.font.func_243248_b(matrixStack, new StringTextComponent(""), (float) this.titleX, (float) this.titleY, 4210752);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);

		float midX = width / 2f;
		float midY = height / 2f;

		Minecraft minecraft = getMinecraft();

		int containerWidth = 276;
		int containerHeight = 166;

		minecraft.getTextureManager().bindTexture(HUD_RESOURCE);
		blit(matrixStack, (int) (midX - containerWidth / 2), (int) (midY - containerHeight / 2), 0, 0, containerWidth, containerHeight, 512,
				256);

		tradesContainer.setBounds(getTradeBoundaries());
		tradesContainer.setInnerHeight(MerchantWidget.BUTTON_HEIGHT * tradeWidgets.size());

		tradesContainer.render(matrixStack, mouseX, mouseY, partialTicks);
		super.render(matrixStack, mouseX, mouseY, partialTicks);

		minecraft.fontRenderer.drawString(matrixStack, "Merchants", midX - 108, midY - 77, 0xFF_3f3f3f);

		if (getContainer().getSelectedTrade() != null) {
			String name = "Vendor - " + getContainer().getSelectedTrade().getName();
			minecraft.fontRenderer.drawString(matrixStack, name, midX + 50 - minecraft.fontRenderer.getStringWidth(name) / 2f, midY - 70,
					0xFF_3f3f3f);
		}

		this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}

	public void renderTrades(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		Rectangle tradeBoundaries = getTradeBoundaries();

		int tradeContainerX = mouseX - tradeBoundaries.x0;
		int tradeContainerY = mouseY - tradeBoundaries.y0 + tradesContainer.getyOffset();

		for (MerchantWidget tradeWidget : tradeWidgets) {
			tradeWidget.render(matrixStack, tradeContainerX, tradeContainerY, partialTicks);
		}
	}

	@Override
	protected void renderHoveredTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
		Rectangle tradeBoundaries = getTradeBoundaries();

		int tradeContainerX = mouseX - tradeBoundaries.x0;
		int tradeContainerY = mouseY - tradeBoundaries.y0 + tradesContainer.getyOffset();

		for (MerchantWidget tradeWidget : tradeWidgets) {
			if (tradeWidget.isHovered(tradeContainerX, tradeContainerY)) {
				Trade trade = tradeWidget.getTraderCode().getTrade();
				if (trade.getTradesLeft() != 0) {
					ItemStack sellStack = trade.getSell().toStack();
					renderTooltip(matrixStack, sellStack, mouseX, mouseY);
				} else {
					StringTextComponent text = new StringTextComponent("Sold out, sorry!");
					text.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_FF0000)));
					renderTooltip(matrixStack, text, mouseX, mouseY);
				}
			}
		}

		super.renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}
}
