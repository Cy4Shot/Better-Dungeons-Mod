package com.cy4.betterdungeons.core.init;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.item.DungeonKeyItem;
import com.cy4.betterdungeons.common.item.PhatDinnerItem;
import com.cy4.betterdungeons.core.itemgroup.BetterDungeonsItemGroup;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
			BetterDungeons.MOD_ID);

	public static final RegistryObject<Item> DUNGEON_KEY = ITEMS.register("dungeon_key", () -> new DungeonKeyItem(
			new Item.Properties().maxStackSize(1).group(BetterDungeonsItemGroup.BETTER_DUNGEONS)));

	public static final RegistryObject<Item> EMPTY_KEY = ITEMS.register("empty_key",
			() -> new Item(new Item.Properties().group(BetterDungeonsItemGroup.BETTER_DUNGEONS)));

	public static final RegistryObject<Item> PHAT_DINNER = ITEMS.register("phat_dinner",
			() -> new PhatDinnerItem(new Item.Properties().maxStackSize(1).food(PhatDinnerItem.FOOD)
					.group(BetterDungeonsItemGroup.BETTER_DUNGEONS)));

}
