package com.cy4.betterdungeons.core.init;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.item.DungeonKeyItem;
import com.cy4.betterdungeons.common.item.MerchantItem;
import com.cy4.betterdungeons.common.item.PhatDinnerItem;
import com.cy4.betterdungeons.core.itemgroup.BetterDungeonsItemGroup;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BetterDungeons.MOD_ID);

	public static final RegistryObject<Item> DUNGEON_KEY = ITEMS.register("dungeon_key",
			() -> new DungeonKeyItem(new Item.Properties().maxStackSize(1).group(BetterDungeonsItemGroup.BETTER_DUNGEONS)));

	public static final RegistryObject<Item> MERCHANT = ITEMS.register("merchant",
			() -> new MerchantItem());

	public static final RegistryObject<Item> EMPTY_KEY = ITEMS.register("empty_key",
			() -> new Item(new Item.Properties().group(BetterDungeonsItemGroup.BETTER_DUNGEONS)));
	
	public static final RegistryObject<Item> DUNGEON_COIN = ITEMS.register("dungeon_coin",
			() -> new Item(new Item.Properties().group(BetterDungeonsItemGroup.BETTER_DUNGEONS)));

	public static final RegistryObject<Item> PHAT_DINNER = ITEMS.register("phat_dinner", () -> new PhatDinnerItem(
			new Item.Properties().maxStackSize(1).food(PhatDinnerItem.FOOD).group(BetterDungeonsItemGroup.BETTER_DUNGEONS)));

	public static final RegistryObject<Item> NIAZITE_PIECE = ITEMS.register("niazite_piece",
			() -> new Item(new Item.Properties().group(BetterDungeonsItemGroup.BETTER_DUNGEONS)));
	public static final RegistryObject<Item> IDLITE_PIECE = ITEMS.register("idlite_piece",
			() -> new Item(new Item.Properties().group(BetterDungeonsItemGroup.BETTER_DUNGEONS)));
	public static final RegistryObject<Item> THALAMITE_PIECE = ITEMS.register("thalamite_piece",
			() -> new Item(new Item.Properties().group(BetterDungeonsItemGroup.BETTER_DUNGEONS)));
	public static final RegistryObject<Item> DIGINITE_PIECE = ITEMS.register("diginite_piece",
			() -> new Item(new Item.Properties().group(BetterDungeonsItemGroup.BETTER_DUNGEONS)));
	public static final RegistryObject<Item> BLOCITE_PIECE = ITEMS.register("blocite_piece",
			() -> new Item(new Item.Properties().group(BetterDungeonsItemGroup.BETTER_DUNGEONS)));
	public static final RegistryObject<Item> GRINDITE_PIECE = ITEMS.register("grindite_piece",
			() -> new Item(new Item.Properties().group(BetterDungeonsItemGroup.BETTER_DUNGEONS)));
	public static final RegistryObject<Item> TURNITE_PIECE = ITEMS.register("turnite_piece",
			() -> new Item(new Item.Properties().group(BetterDungeonsItemGroup.BETTER_DUNGEONS)));
	public static final RegistryObject<Item> SOULITE_PIECE = ITEMS.register("soulite_piece",
			() -> new Item(new Item.Properties().group(BetterDungeonsItemGroup.BETTER_DUNGEONS)));
}
