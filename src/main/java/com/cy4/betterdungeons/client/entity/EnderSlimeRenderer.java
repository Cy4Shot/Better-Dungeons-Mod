package com.cy4.betterdungeons.client.entity;

import com.cy4.betterdungeons.BetterDungeons;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnderSlimeRenderer extends SlimeRenderer {

    public static final ResourceLocation TEXTURE = new ResourceLocation(BetterDungeons.MOD_ID, "textures/entity/ender_slime.png");

    public EnderSlimeRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    protected void preRenderCallback(SlimeEntity entitylivingbase, MatrixStack matrixStack, float partialTickTime) {
        super.preRenderCallback(entitylivingbase, matrixStack, partialTickTime);
        matrixStack.scale(2, 2, 2);
    }

    @Override
    public ResourceLocation getEntityTexture(SlimeEntity entity) {
        return TEXTURE;
    }

}