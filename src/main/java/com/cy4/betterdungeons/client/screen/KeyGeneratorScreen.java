package com.cy4.betterdungeons.client.screen;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.container.KeyGeneratorContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyGeneratorScreen extends ContainerScreen<KeyGeneratorContainer> {

	private static final ResourceLocation TIME_CAPSULE_GUI = new ResourceLocation(BetterDungeons.MOD_ID,
			"textures/gui/key_generator.png");

	public KeyGeneratorScreen(KeyGeneratorContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		this.guiLeft = 0;
		this.guiTop = 0;
		this.xSize = 175;
		this.ySize = 201;
	}

	@Override
	public void render(MatrixStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
		// TODO Auto-generated method stub
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {

		this.font.func_243248_b(matrixStack, this.playerInventory.getDisplayName(), (float)this.playerInventoryTitleX, (float)this.playerInventoryTitleY, 4210752);
		this.font.drawString(matrixStack, this.container.tileEntity.time, 8.0f, 6.0f, 0xE3FFFF);

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX,
			int mouseY) {
		RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.minecraft.getTextureManager().bindTexture(TIME_CAPSULE_GUI);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.blit(matrixStack, x, y, 0, 0, this.xSize, this.ySize);
	}
}
