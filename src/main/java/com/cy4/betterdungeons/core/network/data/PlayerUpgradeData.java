package com.cy4.betterdungeons.core.network.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.upgrade.UpgradeNode;
import com.cy4.betterdungeons.common.upgrade.UpgradeTree;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerUpgradeData extends WorldSavedData {

	protected static final String DATA_NAME = BetterDungeons.MOD_ID + "_PlayerUpgrades";

	private Map<UUID, UpgradeTree> playerMap = new HashMap<>();

	public PlayerUpgradeData() {
		this(DATA_NAME);
	}

	public PlayerUpgradeData(String name) {
		super(name);
	}

	public UpgradeTree getUpgrades(PlayerEntity player) {
		return this.getUpgrades(player.getUniqueID());
	}

	public UpgradeTree getUpgrades(UUID uuid) {
		return this.playerMap.computeIfAbsent(uuid, UpgradeTree::new);
	}

	/* ------------------------------- */

	public PlayerUpgradeData add(ServerPlayerEntity player, UpgradeNode<?>... nodes) {
		this.getUpgrades(player).add(player.getServer(), nodes);

		markDirty();
		return this;
	}

	public PlayerUpgradeData remove(ServerPlayerEntity player, UpgradeNode<?>... nodes) {
		this.getUpgrades(player).remove(player.getServer(), nodes);

		markDirty();
		return this;
	}

	public PlayerUpgradeData upgradeUpgrade(ServerPlayerEntity player, UpgradeNode<?> upgradeNode) {
		this.getUpgrades(player).upgradeUpgrade(player.getServer(), upgradeNode);

		this.getUpgrades(player).sync(player.server);

		markDirty();
		return this;
	}

	public PlayerUpgradeData resetUpgradeTree(ServerPlayerEntity player) {
		UUID uniqueID = player.getUniqueID();

		UpgradeTree oldUpgradeTree = playerMap.get(uniqueID);
		if (oldUpgradeTree != null) {
			for (UpgradeNode<?> node : oldUpgradeTree.getNodes()) {
				if (node.isLearned())
					node.getUpgrade().onRemoved(player);
			}
		}

		UpgradeTree upgradeTree = new UpgradeTree(uniqueID);
		this.playerMap.put(uniqueID, upgradeTree);

		this.getUpgrades(player).sync(player.server);

		markDirty();
		return this;
	}

	/* ------------------------------- */

	public PlayerUpgradeData tick(MinecraftServer server) {
		this.playerMap.values().forEach(abilityTree -> abilityTree.tick(server));
		return this;
	}

	@SubscribeEvent
	public static void onWorldTick(TickEvent.WorldTickEvent event) {
		if (event.side == LogicalSide.SERVER) {
			get((ServerWorld) event.world).tick(((ServerWorld) event.world).getServer());
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.side == LogicalSide.SERVER) {
			get((ServerWorld) event.player.world).getUpgrades(event.player).tick(event);
		}
	}

	/* ------------------------------- */

	@Override
	public void read(CompoundNBT nbt) {
		ListNBT playerList = nbt.getList("PlayerEntries", Constants.NBT.TAG_STRING);
		ListNBT upgradeList = nbt.getList("UpgradeEntries", Constants.NBT.TAG_COMPOUND);

		if (playerList.size() != upgradeList.size()) {
			throw new IllegalStateException("Map doesn't have the same amount of keys as values");
		}

		for (int i = 0; i < playerList.size(); i++) {
			UUID playerUUID = UUID.fromString(playerList.getString(i));
			this.getUpgrades(playerUUID).deserializeNBT(upgradeList.getCompound(i));
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		ListNBT playerList = new ListNBT();
		ListNBT upgradeList = new ListNBT();

		this.playerMap.forEach((uuid, abilityTree) -> {
			playerList.add(StringNBT.valueOf(uuid.toString()));
			upgradeList.add(abilityTree.serializeNBT());
		});

		nbt.put("PlayerEntries", playerList);
		nbt.put("UpgradeEntries", upgradeList);

		return nbt;
	}

	public static PlayerUpgradeData get(ServerWorld world) {
		return world.getServer().func_241755_D_().getSavedData().getOrCreate(PlayerUpgradeData::new, DATA_NAME);
	}
}
