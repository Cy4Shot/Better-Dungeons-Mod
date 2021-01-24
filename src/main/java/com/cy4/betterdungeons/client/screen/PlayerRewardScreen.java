package com.cy4.betterdungeons.client.screen;

import java.util.Random;

import com.cy4.betterdungeons.client.screen.util.UpgradeButton;
import com.cy4.betterdungeons.common.container.RewardContainer;
import com.cy4.betterdungeons.core.init.BlockInit;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerRewardScreen extends ContainerScreen<RewardContainer> {

	public static final Block[] REWARDS = new Block[] { BlockInit.NIAZITE_SHARD.get(), BlockInit.IDLITE_SHARD.get(),
			BlockInit.THALAMITE_SHARD.get(), BlockInit.BLOCITE_SHARD.get(), BlockInit.GRINDITE_SHARD.get(), BlockInit.DIGINITE_SHARD.get(),
			BlockInit.TURNITE_SHARD.get(), BlockInit.SOULITE_SHARD.get() };

	public Block[] curr = new Block[3];

	public PlayerRewardScreen(RewardContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		this.guiLeft = 0;
		this.guiTop = 0;
		this.xSize = 175;
		this.ySize = 201;
	}

	@Override
	protected void init() {
		xSize = width; // RIP JEI :(
		super.init();

		for (int i = 0; i < 3; i++)
			curr[i] = REWARDS[new Random().nextInt(REWARDS.length)];

		for (int i = 0; i < 3; i++)
			addButton(new UpgradeButton((i + 1) * (this.xSize / 4) - 16, this.ySize / 3, this, curr[i])); // Button A
	}

	@Override
	public void render(MatrixStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		for (int i = 0; i < 3; i++)
			drawItemStack(new ItemStack(curr[i]), (i + 1) * (this.xSize / 4) - 16, this.ySize / 3, "");
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
	}

	@SuppressWarnings("deprecation")
	private void drawItemStack(ItemStack stack, int x, int y, String altText) {
		RenderHelper.enableStandardItemLighting();
		RenderSystem.pushMatrix();
		RenderSystem.translatef(-8, -8, 0);
		this.itemRenderer.renderItemAndEffectIntoGUI(stack, (int) x, (int) y);
		this.itemRenderer.renderItemOverlayIntoGUI(this.font, stack, (int) x, (int) y, "");
		RenderSystem.popMatrix();
		RenderHelper.disableStandardItemLighting();
	}

}
