package com.cy4.betterdungeons.core.network.stats;

import java.util.UUID;
import java.util.function.Consumer;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.block.DungeonPortalBlock;
import com.cy4.betterdungeons.common.world.spawner.DungeonSpawner;
import com.cy4.betterdungeons.common.world.util.PortalPlacer;
import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.cy4.betterdungeons.core.init.BlockInit;
import com.cy4.betterdungeons.core.init.DimensionInit;
import com.cy4.betterdungeons.core.network.DungeonsNetwork;
import com.cy4.betterdungeons.core.network.NetcodeUtils;
import com.cy4.betterdungeons.core.network.message.DungeonRunTickMessage;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.NetworkDirection;

public class DungeonRun implements INBTSerializable<CompoundNBT> {
	public static final PortalPlacer PORTAL_PLACER = new PortalPlacer((pos, facing) -> {
		return BlockInit.DUNGEON_PORTAL.get().getDefaultState().with(DungeonPortalBlock.AXIS, facing.getAxis()).getBlockState();
	}, (pos, facing) -> {
		return BlockInit.DUNGEON_PORTAL_FRAME.get().getDefaultState();
	});

	public static final int REGION_SIZE = 1 << 11;

	public UUID playerId;
	public MutableBoundingBox box;
	public int level;
	public int sTickLeft = DungeonsConfig.CONFIG.getTickCounter();
	public int ticksLeft = this.sTickLeft;

	public BlockPos start;
	public Direction facing;
	public boolean won;

	public DungeonSpawner spawner = new DungeonSpawner(this);
	public boolean finished = false;
	public int timer = 20 * 60;

	protected DungeonRun() {

	}

	public DungeonRun(UUID playerId, MutableBoundingBox box, int level) {
		this.playerId = playerId;
		this.box = box;
		this.level = level;
	}

	public UUID getPlayerId() {
		return this.playerId;
	}

	public boolean isComplete() {
		return this.ticksLeft <= 0 || this.finished;
	}

	public void tick(ServerWorld world) {
		if (this.finished)
			return;

		this.runIfPresent(world.getServer(), player -> {
			this.ticksLeft--;
			this.syncTicksLeft(world.getServer());
		});

		if (this.ticksLeft <= 0) {
			if (this.won) {
				this.runIfPresent(world.getServer(), playerEntity -> {
					this.teleportToStart(world, playerEntity);
				});
			} else {
				this.runIfPresent(world.getServer(), playerEntity -> {
					playerEntity.sendMessage(new StringTextComponent("Time has run out!").mergeStyle(TextFormatting.GREEN), this.playerId);
					playerEntity.inventory.func_234564_a_(stack -> true, -1, playerEntity.container.func_234641_j_());
					playerEntity.openContainer.detectAndSendChanges();
					playerEntity.container.onCraftMatrixChanged(playerEntity.inventory);
					playerEntity.updateHeldItem();

					DamageSource source = new DamageSource("dungeonFailed").setDamageBypassesArmor().setDamageAllowedInCreativeMode();
					playerEntity.attackEntityFrom(source, 100000000.0F);

					this.finish(world, this.playerId);
					this.finished = true;
				});
			}
		} else {
			this.runIfPresent(world.getServer(), player -> {
				if (this.ticksLeft + 20 < this.sTickLeft && player.world.getDimensionKey() != DimensionInit.DUNGEON_WORLD) {
					if (player.world.getDimensionKey() == World.OVERWORLD) {
						this.finished = true;
					} else {
						this.ticksLeft = 1;
					}
				} else {
					this.spawner.tick(player);
				}
			});
		}

		this.timer--;
	}

	public void finish(ServerWorld server, UUID playerId) {
	}

	public static ScoreObjective getOrCreateObjective(Scoreboard scoreboard, String name, ScoreCriteria criteria,
			ScoreCriteria.RenderType renderType) {
		if (!scoreboard.func_197897_d().contains(name)) {
			scoreboard.addObjective(name, criteria, new StringTextComponent(name), renderType);
		}

		return scoreboard.getObjective(name);
	}

	public boolean runIfPresent(MinecraftServer server, Consumer<ServerPlayerEntity> action) {
		if (server == null)
			return false;
		ServerPlayerEntity player = server.getPlayerList().getPlayerByUUID(this.playerId);
		if (player == null)
			return false;
		action.accept(player);
		return true;
	}

	public void syncTicksLeft(MinecraftServer server) {
		NetcodeUtils.runIfPresent(server, this.playerId, player -> {
			DungeonsNetwork.CHANNEL.sendTo(new DungeonRunTickMessage(this.ticksLeft), player.connection.netManager,
					NetworkDirection.PLAY_TO_CLIENT);
		});
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putUniqueId("PlayerId", this.playerId);
		nbt.put("Box", this.box.toNBTTagIntArray());
		nbt.putInt("Level", this.level);
		nbt.putInt("StartTicksLeft", this.sTickLeft);
		nbt.putInt("TicksLeft", this.ticksLeft);
		nbt.putBoolean("Won", this.won);

		if (this.start != null) {
			CompoundNBT startNBT = new CompoundNBT();
			startNBT.putInt("x", this.start.getX());
			startNBT.putInt("y", this.start.getY());
			startNBT.putInt("z", this.start.getZ());
			nbt.put("Start", startNBT);
		}

		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.playerId = nbt.getUniqueId("PlayerId");
		this.box = new MutableBoundingBox(nbt.getIntArray("Box"));
		this.level = nbt.getInt("Level");
		this.sTickLeft = nbt.getInt("StartTicksLeft");
		this.ticksLeft = nbt.getInt("TicksLeft");
		this.won = nbt.getBoolean("Won");

		if (nbt.contains("Start", Constants.NBT.TAG_COMPOUND)) {
			CompoundNBT startNBT = nbt.getCompound("Start");
			this.start = new BlockPos(startNBT.getInt("x"), startNBT.getInt("y"), startNBT.getInt("z"));
		}
	}

	public static DungeonRun fromNBT(CompoundNBT nbt) {
		DungeonRun raid = new DungeonRun();
		raid.deserializeNBT(nbt);
		return raid;
	}

	public void teleportToStart(ServerWorld world, ServerPlayerEntity player) {
		if (this.start == null) {
			BetterDungeons.LOGGER.warn("No dungeon start was found.");
			player.teleport(world, this.box.minX + this.box.getXSize() / 2.0F, 256, this.box.minZ + this.box.getZSize() / 2.0F,
					player.rotationYaw, player.rotationPitch);
			return;
		}

		player.teleport(world, this.start.getX() + 0.5D, this.start.getY() + 0.2D, this.start.getZ() + 0.5D,
				this.facing == null ? world.getRandom().nextFloat() * 360.0F : this.facing.rotateY().getHorizontalAngle(), 0.0F);

		player.setOnGround(true);
	}

	public void start(ServerWorld world, ServerPlayerEntity player, ChunkPos chunkPos) {
		loop: for (int x = -48; x < 48; x++) {
			for (int z = -48; z < 48; z++) {
				for (int y = 0; y < 48; y++) {
					BlockPos pos = chunkPos.asBlockPos().add(x, 128 + y, z);
					if (world.getBlockState(pos).getBlock() != Blocks.CRIMSON_PRESSURE_PLATE)
						continue;
					world.setBlockState(pos, Blocks.AIR.getDefaultState());

					this.start = pos;

					for (Direction direction : Direction.Plane.HORIZONTAL) {
						int count = 1;

						while (world.getBlockState(pos.offset(direction, count)).getBlock() == Blocks.WARPED_PRESSURE_PLATE) {
							world.setBlockState(pos.offset(direction, count), Blocks.AIR.getDefaultState());
							count++;
						}

						if (count != 1) {
							PORTAL_PLACER.place(world, pos, this.facing = direction, count, count + 1);
							System.out.println("Ayoo placing Portal right 'ere " + pos);
							break loop;
						}
					}
				}
			}
		}

		this.teleportToStart(world, player);
		player.func_242279_ag();

		this.runIfPresent(world.getServer(), playerEntity -> {
			long seconds = (this.ticksLeft / 20) % 60;
			long minutes = ((this.ticksLeft / 20) / 60) % 60;
			String duration = String.format("%02d:%02d", minutes, seconds);

			StringTextComponent title = new StringTextComponent("The Dungeon");
			title.setStyle(Style.EMPTY.setColor(Color.fromInt(0xFF_ddd01e)));

			IFormattableTextComponent subtitle = new StringTextComponent("Good luck, ").append(player.getName())
					.append(new StringTextComponent("!"));
			subtitle.setStyle(Style.EMPTY.setColor(Color.fromInt(0xFF_ddd01e)));

			StringTextComponent actionBar = new StringTextComponent("You have " + duration + " minutes to complete the run.");
			actionBar.setStyle(Style.EMPTY.setColor(Color.fromInt(0xFF_ddd01e)));

			STitlePacket titlePacket = new STitlePacket(STitlePacket.Type.TITLE, title);
			STitlePacket subtitlePacket = new STitlePacket(STitlePacket.Type.SUBTITLE, subtitle);

			playerEntity.connection.sendPacket(titlePacket);
			playerEntity.connection.sendPacket(subtitlePacket);
			playerEntity.sendStatusMessage(actionBar, true);
		});
	}
}
