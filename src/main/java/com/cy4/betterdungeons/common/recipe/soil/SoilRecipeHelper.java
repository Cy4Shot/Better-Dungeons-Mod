package com.cy4.betterdungeons.common.recipe.soil;

import com.cy4.betterdungeons.common.recipe.BaseRecipeHelper;
import com.cy4.betterdungeons.core.init.RecipesInit;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SoilRecipeHelper extends BaseRecipeHelper<SoilInfo> {
    public SoilRecipeHelper() {
        super(RecipesInit.soilRecipeType);
    }

    public SoilInfo getSoilForItem(World world, ItemStack stack) {
        return getRecipeStream(world.getRecipeManager()).filter(recipe -> recipe.ingredient.test(stack)).findFirst().orElse(null);
    }
}	