package com.cy4.betterdungeons.client.ter;

import com.cy4.betterdungeons.common.te.BossBlockTileEntity;
import com.cy4.betterdungeons.core.init.ItemInit;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class BossBlockTileEntityRenderer extends TileEntityRenderer<BossBlockTileEntity> {

	private Minecraft mc = Minecraft.getInstance();

	public BossBlockTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(BossBlockTileEntity table, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight,
			int combinedOverlay) {
		if (table.getKeys() == 0)
			return;

		ClientPlayerEntity player = mc.player;
		int lightLevel = getLightAtPos(table.getWorld(), table.getPos().up());

		renderItem(new ItemStack(ItemInit.BOSS_KEY.get()), new double[] { .5d, 1d, .5d },
				Vector3f.YP.rotationDegrees(180.0F - player.rotationYaw), matrixStack, buffer, partialTicks, combinedOverlay, lightLevel,
				0.8f);

		renderLabel(new ItemStack(ItemInit.BOSS_KEY.get()), matrixStack, buffer, lightLevel, new double[] { .5d, 1.3d, .5d },
				new StringTextComponent(String.valueOf(table.getKeys())), 0xffffff);
	}

	private void renderItem(ItemStack stack, double[] translation, Quaternion rotation, MatrixStack matrixStack, IRenderTypeBuffer buffer,
			float partialTicks, int combinedOverlay, int lightLevel, float scale) {
		matrixStack.push();
		matrixStack.translate(translation[0], translation[1], translation[2]);
		matrixStack.rotate(rotation);
		matrixStack.scale(scale, scale, scale);
		IBakedModel ibakedmodel = mc.getItemRenderer().getItemModelWithOverrides(stack, null, null);
		mc.getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, true, matrixStack, buffer, lightLevel,
				combinedOverlay, ibakedmodel);
		matrixStack.pop();
	}

	private void renderLabel(ItemStack item, MatrixStack matrixStack, IRenderTypeBuffer buffer, int lightLevel, double[] corner,
			StringTextComponent text, int color) {
		FontRenderer fontRenderer = mc.fontRenderer;

		// render amount required for the item
		matrixStack.push();
		float scale = 0.01f;
		int opacity = (int) (.4f * 255.0F) << 24;
		float offset = (float) (-fontRenderer.getStringPropertyWidth(text) / 2);
		Matrix4f matrix4f = matrixStack.getLast().getMatrix();

		matrixStack.translate(corner[0], corner[1] + .4f, corner[2]);
		matrixStack.scale(scale, scale, scale);
		matrixStack.rotate(mc.getRenderManager().getCameraOrientation()); // face the camera
		matrixStack.rotate(Vector3f.ZP.rotationDegrees(180.0F)); // flip vertical
		fontRenderer.func_243247_a(text, offset, 0, color, false, matrix4f, buffer, false, opacity, lightLevel);
		matrixStack.pop();
	}

	private int getLightAtPos(World world, BlockPos pos) {
		int blockLight = world.getLightFor(LightType.BLOCK, pos);
		int skyLight = world.getLightFor(LightType.SKY, pos);
		return LightTexture.packLight(blockLight, skyLight);
	}

}
