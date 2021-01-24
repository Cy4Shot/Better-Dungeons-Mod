package com.cy4.betterdungeons.common.merchant;

import com.cy4.betterdungeons.core.util.nbt.INBTSerializable;
import com.cy4.betterdungeons.core.util.nbt.NBTSerialize;
import com.google.gson.annotations.Expose;

public class Trade implements INBTSerializable {

	@Expose
	@NBTSerialize
	protected Product buy;
	@Expose
	@NBTSerialize
	protected Product extra;
	@Expose
	@NBTSerialize
	protected Product sell;
	@Expose
	@NBTSerialize
	protected int max_trades;
	@Expose
	@NBTSerialize
	protected int times_traded;

	public Trade() {
		// Serialization.
		this.max_trades = -1;
	}

	public Trade(Product buy, Product extra, Product sell) {
		this.buy = buy;
		this.extra = extra;
		this.sell = sell;
	}

	public Product getBuy() {
		return this.buy;
	}

	public Product getExtra() {
		return this.extra;
	}

	public Product getSell() {
		return this.sell;
	}

	public int getMaxTrades() {
		return max_trades;
	}

	public int getTimesTraded() {
		return times_traded;
	}

	public int getTradesLeft() {
		if (max_trades == -1)
			return -1;
		return Math.max(0, max_trades - times_traded);
	}

	public void onTraded() {
		this.times_traded++;
	}

	public boolean isValid() {
		if (this.buy == null || !this.buy.isValid())
			return false;
		if (this.sell == null || !this.sell.isValid())
			return false;
		if (this.extra != null && !this.extra.isValid())
			return false;
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		else if (obj == this)
			return true;
		else if (this.getClass() != obj.getClass())
			return false;

		Trade trade = (Trade) obj;
		return trade.sell.equals(this.sell) && trade.buy.equals(this.buy);
	}

}