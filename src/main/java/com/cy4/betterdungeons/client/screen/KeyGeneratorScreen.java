package com.cy4.betterdungeons.client.screen;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.cy4.betterdungeons.common.container.KeyGeneratorContainer;
import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.cy4.betterdungeons.core.init.ItemInit;
import com.cy4.betterdungeons.core.network.DungeonsNetwork;
import com.cy4.betterdungeons.core.network.message.KeyGeneratorRewardMessage;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyGeneratorScreen extends ContainerScreen<KeyGeneratorContainer> {

	private static final DateFormat simple = new SimpleDateFormat("HH:mm:ss");
	public Long time = 0L;
	private Button button;

	public KeyGeneratorScreen(KeyGeneratorContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		simple.setTimeZone(TimeZone.getTimeZone("UTC"));
		this.guiLeft = 0;
		this.guiTop = 0;
		this.xSize = 175;
		this.ySize = 201;
		refresh();

		this.addButton(this.button);
		refresh();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.button != null) {
			this.button.mouseClicked(mouseX, mouseY, button);
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	public void refresh() {
		this.button = new Button(this.width / 2 - 50, this.height / 2 - 10, 100, 20,
				new StringTextComponent(time != null && time > DungeonsConfig.KEY_GENERATOR.genTimeMillis() ? "Generate Key"
						: simple.format(new Date(DungeonsConfig.KEY_GENERATOR.genTimeMillis() - time))),
				(button) -> {
					System.out.println("HELLO");
					this.container.getTileEntity().lastRecievedDate = Calendar.getInstance().getTime();
					this.container.player.addItemStackToInventory(new ItemStack(ItemInit.DUNGEON_KEY.get()));
					DungeonsNetwork.CHANNEL
							.sendToServer(new KeyGeneratorRewardMessage(this.container.getTileEntity().lastRecievedDate.getTime()));
				});

		this.button.active = time != null && time > DungeonsConfig.KEY_GENERATOR.genTimeMillis();

	}

	@Override
	public void render(MatrixStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
		this.button.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public void tick() {
		this.time = Calendar.getInstance().getTime().getTime() - this.container.getTileEntity().lastRecievedDate.getTime();
		refresh();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		this.font.func_243248_b(matrixStack, new StringTextComponent(""), (float) this.titleX, (float) this.titleY, 4210752);
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
	}
}
