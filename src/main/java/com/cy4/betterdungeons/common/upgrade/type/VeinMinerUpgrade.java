package com.cy4.betterdungeons.common.upgrade.type;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.cy4.betterdungeons.common.upgrade.UpgradeNode;
import com.cy4.betterdungeons.common.upgrade.UpgradeTree;
import com.cy4.betterdungeons.core.network.data.PlayerUpgradeData;
import com.google.gson.annotations.Expose;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VeinMinerUpgrade extends PlayerAbility {

	@Expose
	private final int blockLimit;

	public VeinMinerUpgrade(int cost, int blockLimit) {
		super(cost, Behavior.HOLD_TO_ACTIVATE, 0);
		this.blockLimit = blockLimit;
	}

	public int getBlockLimit() {
		return blockLimit;
	}

	@SubscribeEvent
	public static void onBlockMined(BlockEvent.BreakEvent event) {
		if (!event.getWorld().isRemote()) {
			ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
			UpgradeTree abilityTree = PlayerUpgradeData.get((ServerWorld) event.getWorld()).getUpgrades(player);

			if (!abilityTree.isActive())
				return;

			UpgradeNode<?> focusedAbilityNode = abilityTree.getFocusedUpgrade();

			if (focusedAbilityNode != null) {
				PlayerAbility focusedAbility = (PlayerAbility) focusedAbilityNode.getUpgrade();

				if (focusedAbility instanceof VeinMinerUpgrade) {
					VeinMinerUpgrade veinMinerAbility = (VeinMinerUpgrade) focusedAbility;

					ServerWorld world = (ServerWorld) event.getWorld();
					BlockPos pos = event.getPos();

					BlockState blockState = world.getBlockState(pos);
					if (floodMine(player, world, blockState.getBlock(), pos, veinMinerAbility.getBlockLimit())) {
						event.setCanceled(true);
					}

					abilityTree.setSwappingLocked(true);
				}
			}
		}
	}

	// "Forest Fire Algorithm" from https://en.wikipedia.org/wiki/Flood_fill
	public static boolean floodMine(ServerPlayerEntity player, ServerWorld world, Block targetBlock, BlockPos pos, int limit) {
		if (world.getBlockState(pos).getBlock() != targetBlock)
			return false;

		ItemStack heldItem = player.getHeldItem(player.getActiveHand());

		if (heldItem.isDamageable()) {
			int usesLeft = heldItem.getMaxDamage() - heldItem.getDamage();
			if (usesLeft <= 1)
				return false; // Tool will break anyways, let the event handle that
		}

		int traversedBlocks = 0;
		List<ItemStack> itemDrops = new LinkedList<>();
		Queue<BlockPos> positionQueue = new LinkedList<>();

		itemDrops.addAll(destroyBlockAs(world, pos, player));
		positionQueue.add(pos);
		traversedBlocks++;

		floodLoop: while (!positionQueue.isEmpty()) {
			BlockPos headPos = positionQueue.poll();

			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					for (int z = -1; z <= 1; z++) {
						if (x == 0 && y == 0 && z == 0)
							continue;
						if (traversedBlocks >= limit)
							break floodLoop;
						BlockPos curPos = headPos.add(x, y, z);
						if (world.getBlockState(curPos).getBlock() == targetBlock) {
							itemDrops.addAll(destroyBlockAs(world, curPos, player));
							positionQueue.add(curPos);
							traversedBlocks++;
						}
					}
				}
			}
		}

		itemDrops.forEach(stack -> Block.spawnAsEntity(world, pos, stack));
		return true;
	}

	public static List<ItemStack> destroyBlockAs(ServerWorld world, BlockPos pos, PlayerEntity player) {
		ItemStack heldItem = player.getHeldItem(player.getActiveHand());

		if (heldItem.isDamageable()) {
			int usesLeft = heldItem.getMaxDamage() - heldItem.getDamage();
			if (usesLeft <= 1) {
				return Collections.emptyList();
			}
			heldItem.damageItem(1, player, playerEntity -> {
			});
		}

		List<ItemStack> drops = Block.getDrops(world.getBlockState(pos), world, pos, world.getTileEntity(pos), player, heldItem);

		world.destroyBlock(pos, false, player);

		return drops;
	}

}