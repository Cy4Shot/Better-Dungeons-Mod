package com.cy4.betterdungeons.client.screen;

import org.lwjgl.opengl.GL11;

import com.cy4.betterdungeons.common.container.DungeonCrateContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class DungeonCrateScreen extends ContainerScreen<DungeonCrateContainer> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/shulker_box.png");

    public DungeonCrateScreen(DungeonCrateContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        ++this.ySize;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @SuppressWarnings("deprecation")
	@Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;	
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
    }
}