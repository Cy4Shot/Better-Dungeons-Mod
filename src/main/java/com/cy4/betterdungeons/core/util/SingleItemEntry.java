package com.cy4.betterdungeons.core.util;

import com.google.gson.annotations.Expose;

public class SingleItemEntry {
	
	@Expose public String ITEM;
    @Expose public String NBT;

    public SingleItemEntry(String item, String nbt) {
        this.ITEM = item;
        this.NBT = nbt;
    }

}
