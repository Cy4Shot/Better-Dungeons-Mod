package com.cy4.betterdungeons.common.item;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.merchant.Merchant;
import com.cy4.betterdungeons.common.merchant.MerchantNameGenerator;
import com.cy4.betterdungeons.common.merchant.Product;
import com.cy4.betterdungeons.common.merchant.Trade;
import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.cy4.betterdungeons.core.init.ItemInit;
import com.cy4.betterdungeons.core.itemgroup.BetterDungeonsItemGroup;
import com.cy4.betterdungeons.core.util.nbt.NBTSerializer;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class MerchantItem extends Item {

	public MerchantItem() {
		super(new Properties().group(BetterDungeonsItemGroup.BETTER_DUNGEONS).maxStackSize(1));
	}

	public static ItemStack generate(String nickname) {
		List<Trade> trades;
		trades = DungeonsConfig.MERCHANT_TRADES.TRADES.stream().filter(Trade::isValid).collect(Collectors.toList());
		Collections.shuffle(trades);

		Optional<Trade> trade = trades.stream().findFirst();
		if (trade.isPresent())
			return getStackFromCore(new Merchant(nickname, trade.get()));

		BetterDungeons.LOGGER.error("Attempted to generate a Merchant.. No Trades in config.");
		return ItemStack.EMPTY;
	}

	public static ItemStack getStackFromCore(Merchant merchant) {
		ItemStack stack = new ItemStack(ItemInit.MERCHANT.get(), 1);
		CompoundNBT nbt = new CompoundNBT();
		try {
			nbt.put("merchant", NBTSerializer.serialize(merchant));
			stack.setTag(nbt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stack;
	}

	public static Merchant getCoreFromStack(ItemStack itemStack) {
		CompoundNBT nbt = itemStack.getTag();
		if (nbt == null)
			return null;
		try {
			return NBTSerializer.deserialize(Merchant.class, nbt.getCompound("merchant"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		CompoundNBT nbt = stack.getOrCreateTag();
		if (nbt.contains("merchant")) {
			Merchant merchant = null;
			try {
				merchant = NBTSerializer.deserialize(Merchant.class, (CompoundNBT) nbt.get("merchant"));
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

			Trade trade = merchant.getTrade();
			if (!trade.isValid())
				return;

			Product buy = trade.getBuy();
			Product extra = trade.getExtra();
			Product sell = trade.getSell();
			tooltip.add(new StringTextComponent(""));
			tooltip.add(new StringTextComponent("Merchant: "));
			StringTextComponent traderName = new StringTextComponent(" " + merchant.getName());
			traderName.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_FFAA00)));
			tooltip.add(traderName);
			tooltip.add(new StringTextComponent(""));
			tooltip.add(new StringTextComponent("Trades: "));
			if (buy != null && buy.isValid()) {
				StringTextComponent comp = new StringTextComponent(" - Buy: ");
				TranslationTextComponent name = new TranslationTextComponent(buy.getItem().getTranslationKey());
				name.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_FFAA00)));
				comp.append(name).append(new StringTextComponent(" x" + buy.getAmount()));
				tooltip.add(comp);
			}
			if (extra != null && extra.isValid()) {
				StringTextComponent comp = new StringTextComponent(" - Extra: ");
				TranslationTextComponent name = new TranslationTextComponent(extra.getItem().getTranslationKey());
				name.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_FFAA00)));
				comp.append(name).append(new StringTextComponent(" x" + extra.getAmount()));
				tooltip.add(comp);
			}
			if (sell != null && sell.isValid()) {
				StringTextComponent comp = new StringTextComponent(" - Sell: ");
				TranslationTextComponent name = new TranslationTextComponent(sell.getItem().getTranslationKey());
				name.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_FFAA00)));
				comp.append(name).append(new StringTextComponent(" x" + sell.getAmount()));
				tooltip.add(comp);
			}

			tooltip.add(new StringTextComponent(""));
			if (trade.getTradesLeft() == 0) {
				StringTextComponent comp = new StringTextComponent("[0] Sold out, sorry!");
				comp.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_FF0000)));
				tooltip.add(comp);
			} else if (trade.getTradesLeft() == -1) {
				StringTextComponent comp = new StringTextComponent("[\u221e] Has unlimited trades.");
				comp.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_00AAFF)));
				tooltip.add(comp);
			} else {
				StringTextComponent comp = new StringTextComponent("[" + trade.getTradesLeft() + "] Has a limited stock.");
				comp.setStyle(Style.EMPTY.setColor(Color.fromInt(0x00_FFAA00)));
				tooltip.add(comp);
			}
		}
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		ITextComponent text = super.getDisplayName(stack);
		CompoundNBT nbt = stack.getOrCreateTag();

		if (nbt.contains("merchant", Constants.NBT.TAG_COMPOUND)) {
			try {
				Merchant merchant = NBTSerializer.deserialize(Merchant.class, nbt.getCompound("merchant"));
				text = new StringTextComponent(merchant.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return text;
	}

	public static String getTraderName(ItemStack stack) {
		CompoundNBT nbt = stack.getOrCreateTag();
		Merchant merchant = null;
		try {
			merchant = NBTSerializer.deserialize(Merchant.class, (CompoundNBT) nbt.get("merchant"));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return merchant.getName();
	}

	public static void updateTraderName(ItemStack stack, String newName) {
		CompoundNBT nbt = stack.getOrCreateTag();
		Merchant merchant = null;
		try {
			merchant = NBTSerializer.deserialize(Merchant.class, (CompoundNBT) nbt.get("merchant"));
			merchant.setName(newName);
			CompoundNBT merchantNBT = new CompoundNBT();
			nbt.put("merchant", NBTSerializer.serialize(merchant));
			stack.setTag(merchantNBT);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("static-access")
	@Override
	public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
		stack = this.generate(MerchantNameGenerator.getName());
		BetterDungeons.LOGGER.info("stack created");
	}

}
