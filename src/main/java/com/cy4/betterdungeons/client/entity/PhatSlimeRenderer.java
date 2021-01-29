package com.cy4.betterdungeons.client.entity;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.client.entity.layer.PhatSlimeGelLayer;
import com.cy4.betterdungeons.common.entity.PhatSlimeEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PhatSlimeRenderer extends MobRenderer<PhatSlimeEntity, PhatSlimeModel<PhatSlimeEntity>> {
	private static final ResourceLocation SLIME_TEXTURES = new ResourceLocation(BetterDungeons.MOD_ID, "textures/entity/phat_slime.png");

	public PhatSlimeRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new PhatSlimeModel<>(100), 0.25F);
		this.addLayer(new PhatSlimeGelLayer<>(this));
	}

	public void render(PhatSlimeEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
			int packedLightIn) {
		this.shadowSize = 0.25F * (float) entityIn.getSlimeSize();
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	protected void preRenderCallback(PhatSlimeEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
		matrixStackIn.scale(0.999F, 0.999F, 0.999F);
		matrixStackIn.translate(0.0D, (double) 0.001F, 0.0D);
		float f1 = (float) entitylivingbaseIn.getSlimeSize();
		float f2 = MathHelper.lerp(partialTickTime, entitylivingbaseIn.prevSquishFactor, entitylivingbaseIn.squishFactor)
				/ (f1 * 0.5F + 1.0F);
		float f3 = 1.0F / (f2 + 1.0F);
		matrixStackIn.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
	}

	public ResourceLocation getEntityTexture(PhatSlimeEntity entity) {
		return SLIME_TEXTURES;
	}
}