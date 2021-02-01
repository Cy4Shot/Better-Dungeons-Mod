package com.cy4.betterdungeons.client.entity;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.entity.SlimeSpikesEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.EvokerFangsModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class SlimeSpikesRenderer extends EntityRenderer<SlimeSpikesEntity> {
	private static final ResourceLocation SLIME_SPIKES = new ResourceLocation(BetterDungeons.MOD_ID, "textures/entity/slime_spikes.png");
	private final EvokerFangsModel<SlimeSpikesEntity> model = new EvokerFangsModel<>();

	public SlimeSpikesRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
	}

	public void render(SlimeSpikesEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn,
			IRenderTypeBuffer bufferIn, int packedLightIn) {
		float f = entityIn.getAnimationProgress(partialTicks);
		if (f != 0.0F) {
			float f1 = 2.0F;
			if (f > 0.9F) {
				f1 = (float) ((double) f1 * ((1.0D - (double) f) / (double) 0.1F));
			}

			matrixStackIn.push();
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90.0F - entityIn.rotationYaw));
			matrixStackIn.scale(-f1, -f1, f1);
			matrixStackIn.translate(0.0D, (double) -0.626F, 0.0D);
			matrixStackIn.scale(0.5F, 0.5F, 0.5F);
			this.model.setRotationAngles(entityIn, f, 0.0F, 0.0F, entityIn.rotationYaw, entityIn.rotationPitch);
			IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.model.getRenderType(SLIME_SPIKES));
			this.model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
			matrixStackIn.pop();
			super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
		}
	}

	public ResourceLocation getEntityTexture(SlimeSpikesEntity entity) {
		return SLIME_SPIKES;
	}
}
