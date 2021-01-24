package com.cy4.betterdungeons.common.upgrade;

import com.cy4.betterdungeons.client.helper.UpgradeFrame;
import com.google.gson.annotations.Expose;

public class UpgradeStyle {
	
	@Expose public int x, y;
    @Expose public UpgradeFrame frameType;
    @Expose public int u, v;

    public UpgradeStyle() {}

    public UpgradeStyle(int x, int y, int u, int v) {
        this.x = x;
        this.y = y;
        this.u = u;
        this.v = v;
    }

}
