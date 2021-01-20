package com.cy4.betterdungeons.core.network.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.core.init.ConfiguredStructuresInit;
import com.cy4.betterdungeons.core.init.DimensionInit;
import com.cy4.betterdungeons.core.init.StructureInit;
import com.cy4.betterdungeons.core.network.stats.DungeonRun;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.BiomeRegistry;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class DungeonRunData extends WorldSavedData {

	protected static final String DATA_NAME = BetterDungeons.MOD_ID + "_DungeonRun";

	private Map<UUID, DungeonRun> activeRuns = new HashMap<>();
	private int xOffset = 0;

	public DungeonRunData() {
		this(DATA_NAME);
	}

	public DungeonRunData(String name) {
		super(name);
	}

	public DungeonRun getAt(BlockPos pos) {
		return this.activeRuns.values().stream().filter(run -> run.box.isVecInside(pos)).findFirst().orElse(null);
	}

	public void remove(ServerWorld server, UUID playerId) {
		DungeonRun v = this.activeRuns.remove(playerId);

		if (v != null) {
			v.ticksLeft = 0;
			v.finish(server, playerId);
		}
	}

	public DungeonRun getActiveFor(ServerPlayerEntity player) {
		return this.activeRuns.get(player.getUniqueID());
	}

	@SuppressWarnings("resource")
	public DungeonRun startNew(ServerPlayerEntity player) {
		player.sendStatusMessage(new StringTextComponent("Generating dungeon, please wait...").mergeStyle(TextFormatting.GREEN), true);

		DungeonRun run = new DungeonRun(player.getUniqueID(),
				new MutableBoundingBox(this.xOffset, 0, 0, this.xOffset += DungeonRun.REGION_SIZE, 256, DungeonRun.REGION_SIZE),
				PlayerDungeonData.get(player.getServerWorld()).getDungeonStats(player).getDungeonLevel());

		if (this.activeRuns.containsKey(player.getUniqueID())) {
			this.activeRuns.get(player.getUniqueID()).ticksLeft = 0;
		}

		this.activeRuns.put(run.getPlayerId(), run);
		this.markDirty();

		ServerWorld world = player.getServer().getWorld(DimensionInit.DUNGEON_WORLD);

		player.getServer().runAsync(() -> {
			try {
				ChunkPos chunkPos = new ChunkPos((run.box.minX + run.box.getXSize() / 2) >> 4,
						(run.box.minZ + run.box.getZSize() / 2) >> 4);

				StructureSeparationSettings settings = new StructureSeparationSettings(1, 0, -1);

				StructureStart<?> start = ConfiguredStructuresInit.CONFIGURED_DUNGEON.func_242771_a(world.func_241828_r(),
						world.getChunkProvider().generator, world.getChunkProvider().generator.getBiomeProvider(),
						world.getStructureTemplateManager(), world.getSeed(), chunkPos, BiomeRegistry.PLAINS, 0, settings);

				// This is some cursed calculations, don't ask me how it works.
				int chunkRadius = DungeonRun.REGION_SIZE >> 5;

				for (int x = -chunkRadius; x <= chunkRadius; x += 17) {
					for (int z = -chunkRadius; z <= chunkRadius; z += 17) {
						world.getChunk(chunkPos.x + x, chunkPos.z + z, ChunkStatus.EMPTY, true).func_230344_a_(StructureInit.DUNGEON.get(),
								start);
					}
				}

				run.start(world, player, chunkPos);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		return run;
	}

	public void tick(ServerWorld world) {
		this.activeRuns.values().forEach(dungeonRun -> dungeonRun.tick(world));

		boolean removed = false;

		List<Runnable> tasks = new ArrayList<>();

		for (DungeonRun run : this.activeRuns.values()) {
			if (run.isComplete()) {
				run.syncTicksLeft(world.getServer());
				tasks.add(() -> this.remove(world, run.playerId));
				removed = true;
			}
		}

		tasks.forEach(Runnable::run);

		if (removed || this.activeRuns.size() > 0) {
			this.markDirty();
		}
	}

	@Override
	public void read(CompoundNBT nbt) {
		this.activeRuns.clear();

		nbt.getList("ActiveRuns", Constants.NBT.TAG_COMPOUND).forEach(runNBT -> {
			DungeonRun run = DungeonRun.fromNBT((CompoundNBT) runNBT);
			this.activeRuns.put(run.getPlayerId(), run);
		});

		this.xOffset = nbt.getInt("XOffset");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		ListNBT runsList = new ListNBT();
		this.activeRuns.values().forEach(run -> runsList.add(run.serializeNBT()));
		nbt.put("ActiveRuns", runsList);

		nbt.putInt("XOffset", this.xOffset);
		return nbt;
	}

	public static DungeonRunData get(ServerWorld world) {
		return world.getServer().func_241755_D_().getSavedData().getOrCreate(DungeonRunData::new, DATA_NAME);
	}

}