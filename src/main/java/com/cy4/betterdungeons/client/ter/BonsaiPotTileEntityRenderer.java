package com.cy4.betterdungeons.client.ter;

import com.cy4.betterdungeons.client.ter.model.MultiblockBlockModel;
import com.cy4.betterdungeons.client.ter.model.MultiblockBlockModelRenderer;
import com.cy4.betterdungeons.client.ter.model.TreeModel;
import com.cy4.betterdungeons.common.te.BonsaiPotTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;

public class BonsaiPotTileEntityRenderer extends TileEntityRenderer<BonsaiPotTileEntity> {
	public BonsaiPotTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(BonsaiPotTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
			int combinedLightIn, int combinedOverlayIn) {
		renderSoil(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
		renderShape(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
	}

	private void renderShape(BonsaiPotTileEntity tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer,
			int combinedLightIn, int combinedOverlayIn) {
		if (!tile.hasSapling()) {
			return;
		}

		MultiblockBlockModel model = TreeModel.get(tile.getTreeId());
		if (model == null) {
			return;
		}

		if (model.getBlockCount() == 0) {
			return;
		}

		matrix.push();
		matrix.translate(0.5f, 0.0f, 0.5f);

		// Translate up a bit, so we actually grow out of the bonsai pot, not through it
		matrix.translate(0.0d, 0.10d, 0.0d);

		// Scale the whole tree to a single block width/depth
		float scale = (float) model.getScaleRatio(false);
		matrix.scale(scale, scale, scale);

		// Scale it down even further so we get leave a bit of room on all sides
		float maxSize = 0.9f;
		matrix.scale(maxSize, maxSize, maxSize);

		float progress = (float) tile.getProgress(partialTicks);
		matrix.scale(progress, progress, progress);

		float rotate = tile.modelRotation * 90.0f;
		matrix.rotate(Vector3f.YP.rotationDegrees(rotate));

		float translateOffsetX = (float) (model.width + 1) / 2.0f;
		float translateOffsetY = 0.0f;
		float translateOffsetZ = (float) (model.depth + 1) / 2.0f;
		matrix.translate(-translateOffsetX, -translateOffsetY, -translateOffsetZ);

		MultiblockBlockModelRenderer.renderModel(model, matrix, buffer, combinedLightIn, combinedOverlayIn, tile.getWorld(), tile.getPos());

		matrix.pop();
	}

	@SuppressWarnings("resource")
	private void renderSoil(BonsaiPotTileEntity tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLightIn,
			int combinedOverlayIn) {
		if (tile.getSoilBlockState() == null) {
			return;
		}

		BlockRendererDispatcher brd = Minecraft.getInstance().getBlockRendererDispatcher();

		matrix.push();
		matrix.scale(1 / 16.0f, 1 / 16.0f, 1 / 16.0f);
		matrix.translate(2.0d, 1.1d, 2.0d);
		matrix.scale(12.0f, 1.0f, 12.0f);

		brd.renderModel(tile.getSoilBlockState(), tile.getPos(), tile.getWorld(), matrix, buffer.getBuffer(RenderType.getCutoutMipped()),
				false, tile.getWorld().rand, EmptyModelData.INSTANCE);

		matrix.pop();
	}
}