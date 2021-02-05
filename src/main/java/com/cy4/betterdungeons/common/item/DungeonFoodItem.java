package com.cy4.betterdungeons.common.item;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.cy4.betterdungeons.core.init.DimensionInit;
import com.cy4.betterdungeons.core.init.ItemInit;
import com.cy4.betterdungeons.core.network.data.DungeonRunData;
import com.cy4.betterdungeons.core.network.stats.DungeonRun;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DungeonFoodItem extends Item {

	public static Debuff REDUCE_HEARTS = new Debuff(0.1f, "10% Chance to reduce 1 - 4 Hearts", new Effect() {
		public void execute(PlayerEntity player) {
			player.heal(-(new Random().nextInt(4) + 1));
		};
	});
	public static Debuff MINING_FATIGUE = new Debuff(0.1f, "10% Chance of Mining Fatigue for 30 Seconds",
			Effect.ofEffect(Effects.MINING_FATIGUE, 255, 30 * 20));
	public static Debuff WEAKNESS = new Debuff(0.2f, "20% Chance of Weakness for 1 Minute",
			Effect.ofEffect(Effects.WEAKNESS, 255, 60 * 20));
	public static Debuff NONE = new Debuff(1f, "No Negative Effect", Effect.none());

	public static Food DUNGEON_FOOD = new Food.Builder().saturation(0).hunger(0).setAlwaysEdible().build();
	protected int extraDungeonTicks;
	protected Debuff debuff;

	public DungeonFoodItem(int extraSeconds, Debuff debuff) {
		super(ItemInit.basicItem().food(DUNGEON_FOOD));
		this.extraDungeonTicks = extraSeconds * 20;
		this.debuff = debuff;
	}

	public int getExtraDungeonTicks() {
		return extraDungeonTicks;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack itemStack = playerIn.getHeldItem(handIn);
		if (playerIn.world.getDimensionKey() != DimensionInit.DUNGEON_WORLD)
			return ActionResult.resultFail(itemStack);
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		StringTextComponent comp;

		comp = new StringTextComponent("- Adds " + (int) (this.extraDungeonTicks / 20) + " seconds to the Dungeon Timer");
		comp.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_00FF00)));
		tooltip.add(comp);

		comp = new StringTextComponent("- " + this.debuff.name);
		comp.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_FF0000)).setItalic(true));
		tooltip.add(comp);

		tooltip.add(new StringTextComponent(""));
		comp = new StringTextComponent("[!] Only edible inside a Dungeon");
		comp.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_FF0000)).setItalic(true));
		tooltip.add(comp);

		super.addInformation(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		IFormattableTextComponent displayName = (IFormattableTextComponent) super.getDisplayName(stack);
		return displayName.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_fcbd00)));
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		if (!worldIn.isRemote && entityLiving instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) entityLiving;
			DungeonRun raid = DungeonRunData.get((ServerWorld) worldIn).getActiveFor(player);
			raid.ticksLeft += getExtraDungeonTicks();

			if (new Random().nextFloat() < this.debuff.chance)
				this.debuff.effect.execute(player);

			worldIn.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.BLOCK_CONDUIT_ACTIVATE,
					SoundCategory.MASTER, 1.0F, 1.0F);
		}

		return super.onItemUseFinish(stack, worldIn, entityLiving);
	}

	public static class Debuff {
		float chance;
		String name;
		Effect effect;

		public Debuff(float chance, String name, Effect effect) {
			this.chance = chance;
			this.name = name;
			this.effect = effect;
		}
	}

	public static class Effect {
		public void execute(PlayerEntity player) {

		};

		public static Effect ofEffect(net.minecraft.potion.Effect effect, int amplifier, int duration) {
			return new Effect() {
				public void execute(PlayerEntity player) {
					player.addPotionEffect(new EffectInstance(effect, duration, amplifier, false, false));
				};
			};
		}

		public static Effect none() {
			return new Effect() {

			};
		}
	}

}
