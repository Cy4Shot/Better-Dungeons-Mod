package com.cy4.betterdungeons.common.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

public class KeyCreationTableRecipe {
	private final UUID player;
    private List<RequiredItem> requiredItems = new ArrayList<>();


    public KeyCreationTableRecipe(UUID uuid) {
        this.player = uuid;
    }

    public KeyCreationTableRecipe(UUID uuid, List<RequiredItem> items) {
        this.player = uuid;
        this.requiredItems = items;
    }


    public static KeyCreationTableRecipe deserialize(CompoundNBT nbt) {
        UUID player = nbt.getUniqueId("player");
        ListNBT list = nbt.getList("requiredItems", Constants.NBT.TAG_COMPOUND);
        List<RequiredItem> requiredItems = new ArrayList<>();
        for (INBT tag : list) {
            CompoundNBT compound = (CompoundNBT) tag;
            requiredItems.add(RequiredItem.deserializeNBT(compound));
        }
        return new KeyCreationTableRecipe(player, requiredItems);
    }

    public static CompoundNBT serialize(KeyCreationTableRecipe recipe) {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT list = new ListNBT();
        for (RequiredItem item : recipe.getRequiredItems()) {
            list.add(RequiredItem.serializeNBT(item));
        }
        nbt.putUniqueId("player", recipe.getPlayer());
        nbt.put("requiredItems", list);
        return nbt;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public List<RequiredItem> getRequiredItems() {
        return requiredItems;
    }

    public boolean isComplete() {
        for (RequiredItem item : requiredItems) {
            if (!item.reachedAmountRequired()) {
                return false;
            }
        }
        return true;
    }
}
