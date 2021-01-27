package com.cy4.betterdungeons.common.event;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.upgrade.Restrictions;
import com.cy4.betterdungeons.common.upgrade.UpgradeTree;
import com.cy4.betterdungeons.core.network.data.PlayerUpgradeData;
import com.cy4.betterdungeons.core.util.SideOnlyFixer;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BetterDungeons.MOD_ID)
public class CraftingEvents {

	public static UpgradeTree RESEARCH_TREE;

	private static UpgradeTree getUpgradeTree(PlayerEntity player) {
		if (player.world.isRemote) {
			return RESEARCH_TREE != null ? RESEARCH_TREE : new UpgradeTree(player.getUniqueID());
		} else {
			return PlayerUpgradeData.get((ServerWorld) player.world).getUpgrades(player);
		}
	}

	@SuppressWarnings("resource")
	private static void warnResearchRequirement(String researchName, String i18nKey) {
		TextComponent name = new StringTextComponent(researchName);
		Style style = Style.EMPTY.setColor(Color.fromInt(0xFF_fce336));
		name.setStyle(style);

		TextComponent text = new TranslationTextComponent("overlay.requires_research." + i18nKey, name);

		Minecraft.getInstance().ingameGUI.setOverlayMessage(text, false);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
		PlayerEntity player = event.getPlayer();
		UpgradeTree upgradeTree = getUpgradeTree(player);

		ItemStack craftedItemStack = event.getCrafting();
		IInventory craftingMatrix = event.getInventory();


		String restrictedBy = upgradeTree.restrictedBy(craftedItemStack.getItem(), Restrictions.Type.CRAFTABILITY);


		if (restrictedBy == null)
			return;

		if (event.getPlayer().world.isRemote) {
			warnResearchRequirement(restrictedBy, "craft");
		}

		for (int i = 0; i < craftingMatrix.getSizeInventory(); i++) {
			ItemStack itemStack = craftingMatrix.getStackInSlot(i);
			if (itemStack != ItemStack.EMPTY) {
				ItemStack itemStackToDrop = itemStack.copy();
				itemStackToDrop.setCount(1);
				player.dropItem(itemStackToDrop, false, false);
			}
		}

		int slot = SideOnlyFixer.getSlotFor(player.inventory, craftedItemStack);

		if (slot != -1) {
			// Most prolly SHIFT-taken, just shrink from the taken stack
			ItemStack stackInSlot = player.inventory.getStackInSlot(slot);
			if (stackInSlot.getCount() < craftedItemStack.getCount()) {
				craftedItemStack.setCount(stackInSlot.getCount());
			}
			stackInSlot.shrink(craftedItemStack.getCount());

		} else {
			craftedItemStack.shrink(craftedItemStack.getCount());
		}
	}

	@SubscribeEvent
	public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
		if (!event.isCancelable())
			return;

		PlayerEntity player = event.getPlayer();
		UpgradeTree upgradeTree = getUpgradeTree(player);

		Item usedItem = event.getItemStack().getItem();

		String restrictedBy = upgradeTree.restrictedBy(usedItem, Restrictions.Type.USABILITY);

		if (restrictedBy == null)
			return; // Doesn't restrict usability of this item, so stop here.

		if (event.getSide() == LogicalSide.CLIENT) {
			warnResearchRequirement(restrictedBy, "usage");
		}

		event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
		if (!event.isCancelable())
			return;

		PlayerEntity player = event.getPlayer();
		UpgradeTree upgradeTree = getUpgradeTree(player);

		Item usedItem = event.getItemStack().getItem();

		String restrictedBy = upgradeTree.restrictedBy(usedItem, Restrictions.Type.USABILITY);

		if (restrictedBy == null)
			return; // Doesn't restrict usability of this item, so stop here.

		if (event.getSide() == LogicalSide.CLIENT) {
			warnResearchRequirement(restrictedBy, "usage");
		}

		event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onBlockInteraction(PlayerInteractEvent.RightClickBlock event) {
		if (!event.isCancelable())
			return;

		PlayerEntity player = event.getPlayer();
		UpgradeTree upgradeTree = getUpgradeTree(player);

		String restrictedBy;

		BlockState blockState = player.world.getBlockState(event.getPos());
		restrictedBy = upgradeTree.restrictedBy(blockState.getBlock(), Restrictions.Type.BLOCK_INTERACTABILITY);
		if (restrictedBy != null) {
			if (event.getSide() == LogicalSide.CLIENT) {
				warnResearchRequirement(restrictedBy, "interact_block");
			}
			event.setCanceled(true);
			return;
		}

		ItemStack itemStack = event.getItemStack();
		if (itemStack == ItemStack.EMPTY)
			return;

		Item item = itemStack.getItem();
		restrictedBy = upgradeTree.restrictedBy(item, Restrictions.Type.USABILITY);
		if (restrictedBy != null) {
			if (event.getSide() == LogicalSide.CLIENT) {
				warnResearchRequirement(restrictedBy, "usage");
			}
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onBlockHit(PlayerInteractEvent.LeftClickBlock event) {
		if (!event.isCancelable())
			return;

		PlayerEntity player = event.getPlayer();
		UpgradeTree upgradeTree = getUpgradeTree(player);

		BlockState blockState = player.world.getBlockState(event.getPos());

		String restrictedBy;

		restrictedBy = upgradeTree.restrictedBy(blockState.getBlock(), Restrictions.Type.HITTABILITY);
		if (restrictedBy != null) {
			if (event.getSide() == LogicalSide.CLIENT) {
				warnResearchRequirement(restrictedBy, "hit");
			}
			event.setCanceled(true);
			return;
		}

		ItemStack itemStack = event.getItemStack();
		if (itemStack == ItemStack.EMPTY)
			return;

		Item item = itemStack.getItem();
		restrictedBy = upgradeTree.restrictedBy(item, Restrictions.Type.USABILITY);
		if (restrictedBy != null) {
			if (event.getSide() == LogicalSide.CLIENT) {
				warnResearchRequirement(restrictedBy, "usage");
			}
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onEntityInteraction(PlayerInteractEvent.EntityInteract event) {
		if (!event.isCancelable())
			return;

		PlayerEntity player = event.getPlayer();
		UpgradeTree upgradeTree = getUpgradeTree(player);
		Entity entity = event.getEntity();

		String restrictedBy;

		restrictedBy = upgradeTree.restrictedBy(entity.getType(), Restrictions.Type.ENTITY_INTERACTABILITY);
		if (restrictedBy != null) {
			if (event.getSide() == LogicalSide.CLIENT) {
				warnResearchRequirement(restrictedBy, "interact_entity");
			}
			event.setCanceled(true);
			return;
		}

		ItemStack itemStack = event.getItemStack();
		if (itemStack == ItemStack.EMPTY)
			return;

		Item item = itemStack.getItem();
		restrictedBy = upgradeTree.restrictedBy(item, Restrictions.Type.USABILITY);
		if (restrictedBy != null) {
			if (event.getSide() == LogicalSide.CLIENT) {
				warnResearchRequirement(restrictedBy, "usage");
			}
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onPlayerAttack(AttackEntityEvent event) {
		if (!event.isCancelable())
			return;

		PlayerEntity player = event.getPlayer();
		UpgradeTree upgradeTree = getUpgradeTree(player);
		Entity entity = event.getEntity();

		String restrictedBy;

		restrictedBy = upgradeTree.restrictedBy(entity.getType(), Restrictions.Type.ENTITY_INTERACTABILITY);
		if (restrictedBy != null) {
			if (player.world.isRemote) {
				warnResearchRequirement(restrictedBy, "interact_entity");
			}
			event.setCanceled(true);
			return;
		}

		ItemStack itemStack = player.getHeldItemMainhand();
		if (itemStack == ItemStack.EMPTY)
			return;

		Item item = itemStack.getItem();
		restrictedBy = upgradeTree.restrictedBy(item, Restrictions.Type.USABILITY);
		if (restrictedBy != null) {
			if (player.world.isRemote) {
				warnResearchRequirement(restrictedBy, "usage");
			}
			event.setCanceled(true);
		}
	}

//	@OnlyIn(Dist.CLIENT)
//	@SubscribeEvent(priority = EventPriority.LOWEST)
//	public static void onItemTooltip(ItemTooltipEvent event) {
//		PlayerEntity player = event.getPlayer();
//
//		if (player == null)
//			return;
//
//		UpgradeTree upgradeTree = getUpgradeTree(player);
//		Item item = event.getItemStack().getItem();
//
//		String restrictionCausedBy = Arrays.stream(Restrictions.Type.values()).map(type -> upgradeTree.restrictedBy(item, type))
//				.filter(Objects::nonNull).findFirst().orElseGet(() -> null);
//
//		if (restrictionCausedBy == null)
//			return;
//
//		List<ITextComponent> toolTip = event.getToolTip();
//
//		Style textStyle = Style.EMPTY.setColor(Color.fromInt(0xFF_a8a8a8));
//		Style style = Style.EMPTY.setColor(Color.fromInt(0xFF_fce336));
//		TextComponent text = new TranslationTextComponent("tooltip.requires_research");
//		TextComponent name = new StringTextComponent(" " + restrictionCausedBy);
//		text.setStyle(textStyle);
//		name.setStyle(style);
//		toolTip.add(new StringTextComponent(""));
//		toolTip.add(text);
//		toolTip.add(name);
//	}

}
