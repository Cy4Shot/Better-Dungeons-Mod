package com.cy4.betterdungeons.client.overlay;

import com.cy4.betterdungeons.client.helper.FontHelper;
import com.cy4.betterdungeons.core.init.DimensionInit;
import com.cy4.betterdungeons.core.init.SoundInit;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class DungeonRunOverlay {

	public static int remainingTicks;

	public static SimpleSound panicSound;
	public static SimpleSound ambientLoop;
	public static SimpleSound ambientSound;
	public static SimpleSound bossLoop;

	public static boolean bossSummoned;
	private static int ticksBeforeAmbientSound;

	public static void startBossLoop() {
		if (bossLoop != null)
			stopBossLoop();
		Minecraft minecraft = Minecraft.getInstance();
		bossLoop = SimpleSound.ambientWithoutAttenuation(SoundInit.BOSS.get(), 0.75f, 1f);
		minecraft.getSoundHandler().play(bossLoop);
	}

	public static void stopBossLoop() {
		if (bossLoop == null)
			return;
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.getSoundHandler().stop(bossLoop);
		bossLoop = null;
	}

	@SubscribeEvent
	public static void onPostRender(RenderGameOverlayEvent.Post event) {
		if (event.getType() != RenderGameOverlayEvent.ElementType.POTION_ICONS)
			return; // Render only on HOTBAR

		Minecraft minecraft = Minecraft.getInstance();

		boolean inDungeon = minecraft.world.getDimensionKey() == DimensionInit.DUNGEON_WORLD;

		if (minecraft.world == null || (!inDungeon)) {
			if (inDungeon)
				stopBossLoop();
			bossSummoned = false;
			return;
		}

		if (remainingTicks == 0)
			return; // Timed out, stop here

		MatrixStack matrixStack = event.getMatrixStack();
		int panicTicks = 30 * 20;

		if (inDungeon) {
			if (!bossSummoned)
				stopBossLoop();
			else if (!minecraft.getSoundHandler().isPlaying(bossLoop))
				startBossLoop();
		}
		matrixStack.push();
		matrixStack.translate(62, minecraft.getMainWindow().getScaledHeight(), 0);
		FontHelper.drawStringWithBorder(matrixStack, formatTimeString(), 18, -12,
				remainingTicks < panicTicks && remainingTicks % 10 < 5 ? 0xFF_FF0000 : 0xFF_FFFFFF, 0xFF_000000);
		matrixStack.pop();

		if (inDungeon) {
			if (bossSummoned && ambientLoop != null && minecraft.getSoundHandler().isPlaying(ambientLoop)) {
				minecraft.getSoundHandler().stop(ambientLoop);
			}

			if (ambientLoop == null || !minecraft.getSoundHandler().isPlaying(ambientLoop)) {
				if (!bossSummoned) {
					ambientLoop = SimpleSound.music(SoundInit.AMBIENT_LOOP.get());
					minecraft.getSoundHandler().play(ambientLoop);
				}
			}

			if (ticksBeforeAmbientSound < 0) {
				if (ambientSound == null || !minecraft.getSoundHandler().isPlaying(ambientSound)) {
					ambientSound = SimpleSound.ambient(SoundInit.AMBIENT.get());	
					minecraft.getSoundHandler().play(ambientSound);
					ticksBeforeAmbientSound = 60 * 60;
				}
			}

			ticksBeforeAmbientSound--;
		}

		if (remainingTicks < panicTicks) {
			if (panicSound == null || !minecraft.getSoundHandler().isPlaying(panicSound)) {
				panicSound = SimpleSound.master(SoundInit.TIMER_PANIC.get(), 2.0f - ((float) remainingTicks / panicTicks));
				minecraft.getSoundHandler().play(panicSound);
			}
		}
	}

	public static String formatTimeString() {
		long seconds = (remainingTicks / 20) % 60;
		long minutes = ((remainingTicks / 20) / 60) % 60;
		return String.format("%02d:%02d", minutes, seconds);
	}

}