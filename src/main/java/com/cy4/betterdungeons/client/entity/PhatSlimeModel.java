package com.cy4.betterdungeons.client.entity;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PhatSlimeModel<T extends Entity> extends SegmentedModel<T> {
	private final ModelRenderer bb_main;

	public PhatSlimeModel(int hello) {
		textureWidth = 512;
		textureHeight = 512;

		bb_main = new ModelRenderer(this, 0, hello);
		bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
		bb_main.setTextureOffset(0, 0).addBox(-25.0F, -50.0F, -25.0F, 50.0F, 50.0F, 50.0F, 0.0F, false);
		bb_main.setTextureOffset(0, 200).addBox(-18.5F, -37.0F, -18.5F, 37.0F, 37.0F, 37.0F, 0.0F, false);
	}

	@Override
	public Iterable<ModelRenderer> getParts() {
		return ImmutableList.of(this.bb_main);
	}

	@Override
	public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
}