package com.cy4.betterdungeons.common.recipe.sapling;

import com.cy4.betterdungeons.common.recipe.BaseRecipeHelper;
import com.cy4.betterdungeons.core.init.RecipesInit;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SaplingRecipeHelper extends BaseRecipeHelper<SaplingInfo> {
    public SaplingRecipeHelper() {
        super(RecipesInit.saplingRecipeType);
    }

    public SaplingInfo getSaplingInfoForItem(World world, ItemStack stack) {
        return getRecipeStream(world.getRecipeManager()).filter(recipe -> recipe.ingredient.test(stack)).findFirst().orElse(null);
    }
}