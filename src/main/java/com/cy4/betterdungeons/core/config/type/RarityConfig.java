package com.cy4.betterdungeons.core.config.type;

import com.cy4.betterdungeons.core.config.Config;
import com.google.gson.annotations.Expose;

public class RarityConfig extends Config {

    @Expose
    public int COMMON_WEIGHT;
    @Expose
    public int RARE_WEIGHT;
    @Expose
    public int EPIC_WEIGHT;
    @Expose
    public int LEGENDARY_WEIGHT;

    @Override
    public String getName() {
        return "rarity";
    }

    @Override
    protected void reset() {
        COMMON_WEIGHT = 20;
        RARE_WEIGHT = 10;
        EPIC_WEIGHT = 5;
        LEGENDARY_WEIGHT = 1;

    }
}