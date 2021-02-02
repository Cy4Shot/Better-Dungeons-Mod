package com.cy4.betterdungeons.core.init;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.item.BossKeyItem;
import com.cy4.betterdungeons.common.item.BossTreasureItem;
import com.cy4.betterdungeons.common.item.BossTreasureItem.TreasureRarity;
import com.cy4.betterdungeons.common.item.DungeonFoodItem;
import com.cy4.betterdungeons.common.item.DungeonKeyItem;
import com.cy4.betterdungeons.common.item.MerchantItem;
import com.cy4.betterdungeons.common.item.PhatDinnerItem;
import com.cy4.betterdungeons.core.itemgroup.BetterDungeonsItemGroup;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {

	public static final Item.Properties BASIC_ITEM = new Item.Properties().group(BetterDungeonsItemGroup.BETTER_DUNGEONS);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BetterDungeons.MOD_ID);

	// Dungeon Stuffs
	public static final RegistryObject<Item> DUNGEON_KEY = ITEMS.register("dungeon_key", () -> new DungeonKeyItem());
	public static final RegistryObject<Item> EMPTY_KEY = ITEMS.register("empty_key", () -> new Item(BASIC_ITEM));
	public static final RegistryObject<Item> BOSS_KEY = ITEMS.register("boss_key", () -> new BossKeyItem());
	public static final RegistryObject<Item> MERCHANT = ITEMS.register("merchant", () -> new MerchantItem());
	public static final RegistryObject<Item> DUNGEON_COIN = ITEMS.register("dungeon_coin", () -> new Item(BASIC_ITEM));

	// Dungeon Foods
	public static final RegistryObject<Item> PHAT_DINNER = ITEMS.register("phat_dinner", () -> new PhatDinnerItem());
	public static final RegistryObject<Item> EXAMPLE_FOOD = ITEMS.register("example_food", () -> new DungeonFoodItem(10));

	// Dungeon Treasures
	public static final RegistryObject<Item> COMMON_TREASURE = ITEMS.register("common_treasure",
			() -> new BossTreasureItem(TreasureRarity.COMMON));
	public static final RegistryObject<Item> RARE_TREASURE = ITEMS.register("rare_treasure",
			() -> new BossTreasureItem(TreasureRarity.RARE));
	public static final RegistryObject<Item> EPIC_TREASURE = ITEMS.register("epic_treasure",
			() -> new BossTreasureItem(TreasureRarity.EPIC));
	public static final RegistryObject<Item> LEGENDARY_TREASURE = ITEMS.register("legendary_treasure",
			() -> new BossTreasureItem(TreasureRarity.LEGENDARY));

	// Dungeon Pieces
	public static final RegistryObject<Item> NIAZITE_PIECE = ITEMS.register("niazite_piece", () -> new Item(BASIC_ITEM));
	public static final RegistryObject<Item> IDLITE_PIECE = ITEMS.register("idlite_piece", () -> new Item(BASIC_ITEM));
	public static final RegistryObject<Item> THALAMITE_PIECE = ITEMS.register("thalamite_piece", () -> new Item(BASIC_ITEM));
	public static final RegistryObject<Item> DIGINITE_PIECE = ITEMS.register("diginite_piece", () -> new Item(BASIC_ITEM));
	public static final RegistryObject<Item> BLOCITE_PIECE = ITEMS.register("blocite_piece", () -> new Item(BASIC_ITEM));
	public static final RegistryObject<Item> GRINDITE_PIECE = ITEMS.register("grindite_piece", () -> new Item(BASIC_ITEM));
	public static final RegistryObject<Item> TURNITE_PIECE = ITEMS.register("turnite_piece", () -> new Item(BASIC_ITEM));
	public static final RegistryObject<Item> SOULITE_PIECE = ITEMS.register("soulite_piece", () -> new Item(BASIC_ITEM));
}
