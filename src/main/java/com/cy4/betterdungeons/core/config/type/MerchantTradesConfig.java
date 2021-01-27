package com.cy4.betterdungeons.core.config.type;

import java.util.ArrayList;
import java.util.List;

import com.cy4.betterdungeons.common.merchant.Product;
import com.cy4.betterdungeons.common.merchant.Trade;
import com.cy4.betterdungeons.core.config.Config;
import com.google.gson.annotations.Expose;

import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class MerchantTradesConfig extends Config {

	@Expose
	public List<Trade> TRADES = new ArrayList<>();

	@Override
	public String getName() {
		return "merchant";
	}

	@Override
	protected void reset() {

		this.TRADES.add(new Trade(new Product(Items.APPLE, 8, null), null, new Product(Items.GOLDEN_APPLE, 1, null)));
		this.TRADES.add(new Trade(new Product(Items.GOLDEN_APPLE, 8, null), null, new Product(Items.ENCHANTED_GOLDEN_APPLE, 1, null)));
		
		CompoundNBT nbt = new CompoundNBT();
		ListNBT enchantments = new ListNBT();
		CompoundNBT knockback = new CompoundNBT();
		knockback.putString("id", "minecraft:knockback");
		knockback.putInt("lvl", 10);
		enchantments.add(knockback);
		nbt.put("Enchantments", enchantments);
		nbt.put("ench", enchantments);
		this.TRADES.add(new Trade(new Product(Items.ENCHANTED_GOLDEN_APPLE, 8, null), null, new Product(Items.STICK, 1, nbt)));
	}

}
