package com.cy4.betterdungeons.common.merchant;

import com.cy4.betterdungeons.core.util.nbt.INBTSerializable;
import com.cy4.betterdungeons.core.util.nbt.NBTSerialize;
import com.google.gson.annotations.Expose;

public class Merchant implements INBTSerializable {

	@Expose
	@NBTSerialize
	private String NAME;
	@Expose
	@NBTSerialize
	private Trade TRADE;

	public Merchant(String name, Trade trade) {
		this.NAME = name;
		this.TRADE = trade;
	}

	public Merchant() {
	}

	public String getName() {
		return this.NAME;
	}

	public void setName(String name) {
		this.NAME = name;
	}

	public Trade getTrade() {
		return this.TRADE;
	}

	public void setTrade(Trade trade) {
		this.TRADE = trade;
	}

}
