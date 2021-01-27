package com.cy4.betterdungeons.client.overlay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.upgrade.UpgradeNode;
import com.cy4.betterdungeons.common.upgrade.UpgradeStyle;
import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class AbilitiesOverlay {
	
	public static final ResourceLocation HUD_RESOURCE = new ResourceLocation(BetterDungeons.MOD_ID, "textures/gui/ability_hud.png");
    private static final ResourceLocation UPGRADES_RESOURCE = new ResourceLocation(BetterDungeons.MOD_ID, "textures/gui/upgrade_sprites.png");

    public static List<UpgradeNode<?>> learnedAbilities;
    public static Map<Integer, Integer> cooldowns = new HashMap<>();
    public static int focusedIndex;
    public static boolean active;

    @SuppressWarnings("deprecation")
	@SubscribeEvent
    public static void
    onPostRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR)
            return; // Render only on HOTBAR

        if (learnedAbilities == null || learnedAbilities.size() == 0)
            return; // Render only if there are any learned abilities

        int previousIndex = (focusedIndex - 1);
        if (previousIndex < 0)
            previousIndex += learnedAbilities.size();

        int nextIndex = (focusedIndex + 1);
        if (nextIndex >= learnedAbilities.size())
            nextIndex -= learnedAbilities.size();

        MatrixStack matrixStack = event.getMatrixStack();
        Minecraft minecraft = Minecraft.getInstance();
        int bottom = minecraft.getMainWindow().getScaledHeight();
        int barWidth = 62;
        int barHeight = 22;

        minecraft.getProfiler().startSection("abilityBar");
        matrixStack.push();

        RenderSystem.enableBlend();
        matrixStack.translate(10, bottom - barHeight, 0);

        minecraft.getTextureManager().bindTexture(HUD_RESOURCE);
        minecraft.ingameGUI.blit(matrixStack,
                0, 0,
                1, 13, barWidth, barHeight);

        minecraft.getTextureManager().bindTexture(UPGRADES_RESOURCE);
        UpgradeNode<?> focusedUpgrade = learnedAbilities.get(focusedIndex);
        UpgradeStyle focusedStyle = DungeonsConfig.UPGRADES_GUI.getStyles().get(focusedUpgrade.getGroup().getParentName());
        GlStateManager.color4f(1, 1, 1, cooldowns.getOrDefault(focusedIndex, 0) > 0 ? 0.4f : 1);
        minecraft.ingameGUI.blit(matrixStack,
                23, 3,
                focusedStyle.u, focusedStyle.v,
                16, 16);

        if (cooldowns.getOrDefault(focusedIndex, 0) > 0) {
            float cooldownPercent = (float) cooldowns.get(focusedIndex) / DungeonsConfig.UPGRADES.cooldownOf(focusedUpgrade);
            int cooldownHeight = (int) (16 * cooldownPercent);
            AbstractGui.fill(matrixStack,
                    23, 3 + (16 - cooldownHeight),
                    23 + 16, 3 + 16,
                    0x99_FFFFFF);
            RenderSystem.enableBlend();
        }

        GlStateManager.color4f(0.7f, 0.7f, 0.7f, 0.5f);
        UpgradeNode<?> previousUpgrade = learnedAbilities.get(previousIndex);
        if (cooldowns.getOrDefault(previousIndex, 0) > 0) {
            float cooldownPercent = (float) cooldowns.get(previousIndex) / DungeonsConfig.UPGRADES.cooldownOf(previousUpgrade);
            int cooldownHeight = (int) (16 * cooldownPercent);
            AbstractGui.fill(matrixStack,
                    43, 3 + (16 - cooldownHeight),
                    43 + 16, 3 + 16,
                    0x99_FFFFFF);
            RenderSystem.enableBlend();
        }
        UpgradeStyle previousStyle = DungeonsConfig.UPGRADES_GUI.getStyles().get(previousUpgrade.getGroup().getParentName());
        minecraft.ingameGUI.blit(matrixStack,
                43, 3,
                previousStyle.u, previousStyle.v,
                16, 16);

        UpgradeNode<?> nextUpgrade = learnedAbilities.get(nextIndex);
        if (cooldowns.getOrDefault(nextIndex, 0) > 0) {
            float cooldownPercent = (float) cooldowns.get(nextIndex) / DungeonsConfig.UPGRADES.cooldownOf(nextUpgrade);
            int cooldownHeight = (int) (16 * cooldownPercent);
            AbstractGui.fill(matrixStack,
                    3, 3 + (16 - cooldownHeight),
                    3 + 16, 3 + 16,
                    0x99_FFFFFF);
            RenderSystem.enableBlend();
        }
        UpgradeStyle nextStyle = DungeonsConfig.UPGRADES_GUI.getStyles().get(nextUpgrade.getGroup().getParentName());
        minecraft.ingameGUI.blit(matrixStack,
                3, 3,
                nextStyle.u, nextStyle.v,
                16, 16);

        minecraft.getTextureManager().bindTexture(HUD_RESOURCE);
        GlStateManager.color4f(1, 1, 1, 1);
        minecraft.ingameGUI.blit(matrixStack,
                19, -1,
                64 + (cooldowns.getOrDefault(focusedIndex, 0) > 0 ? 50 : active ? 25 : 0),
                13,
                24, 24);

        matrixStack.pop();
        minecraft.getProfiler().endSection();
    }

}
