package com.cy4.betterdungeons.common.dungeon.generation;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.cy4.betterdungeons.common.dungeon.generation.DungeonGenerator.DungeonTileType;
import com.cy4.betterdungeons.common.world.DungeonTeleporter;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.server.ServerWorld;

public class DungeonStructureSpawner {

	public static final ResourceLocation ROOM = new ResourceLocation("betterdungeons:room");
	public static final ResourceLocation TUNNEL = new ResourceLocation("betterdungeons:corridor");
	public static final ResourceLocation BOSS = new ResourceLocation("betterdungeons:boss");
	public static final ResourceLocation ENTRY = new ResourceLocation("betterdungeons:room");

	public static float percentage;

	public static boolean canGenerateNewDungeon = true;

	public static Map<DungeonTileType, Template> templates;

	private static void genEntries(ServerWorld worldIn) {
		templates = new HashMap<DungeonTileType, Template>();
		for (DungeonTileType d : DungeonTileType.values()) {
			templates.put(d, worldIn.getStructureTemplateManager().getTemplateDefaulted(getTile(d).getKey()));
		}
	}

	public static void startPlace(ServerWorld worldIn, Random rand, Entity entity) {
		genEntries(worldIn);
		DungeonTileType[][] d = new DungeonGenerator(6).buildDungeon();
		Thread t1 = new Thread() {
			public void run() {
				canGenerateNewDungeon = false;
				place(d, worldIn, rand, entity.getPosition(), (PlayerEntity) entity);
				DungeonTeleporter.teleportToDimension((PlayerEntity) entity, worldIn, entity.getPosition());
				canGenerateNewDungeon = true;
			}
		};
		if (canGenerateNewDungeon) {
			t1.start();
		}
	}

	public static boolean place(DungeonTileType[][] dungeon, ServerWorld worldIn, Random rand, BlockPos pos,
			PlayerEntity entity) {
		for (int x = 0; x < dungeon.length; x++) {
			for (int z = 0; z < dungeon.length; z++) {
				Entry<ResourceLocation, Rotation> tile = getTile(dungeon[x][z]);
				if (dungeon[x][z] != DungeonTileType.NONE) {
					Template template = templates.get(dungeon[x][z]);
					PlacementSettings placementsettings = (new PlacementSettings()).setRotation(tile.getValue())
							.setRandom(worldIn.getRandom())
							.addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
					percentage = (float) (x * dungeon.length + z) / (float) (dungeon.length * dungeon.length);
					System.out.println(percentage);
					entity.sendStatusMessage(new StringTextComponent("Loading Dungeon: Spawning ["
							+ (x * dungeon.length + z) + "/" + (dungeon.length * dungeon.length) + "]"), true);
					BlockPos pos1 = template.getZeroPositionWithTransform(pos, Mirror.NONE, tile.getValue())
							.add(1000 + 41 * x, 0, 0 + 41 * z).toMutable();
					template.func_237146_a_(worldIn, pos1, pos1, placementsettings, worldIn.getRandom(), 2);
				}
			}
		}
		return true;
	}

	public static Entry<ResourceLocation, Rotation> getTile(DungeonTileType type) {
		switch (type) {
		case BOSS:
			return new AbstractMap.SimpleEntry<ResourceLocation, Rotation>(BOSS, Rotation.NONE);
		case ENTRY:
			return new AbstractMap.SimpleEntry<ResourceLocation, Rotation>(ENTRY, Rotation.NONE);
		case ROOM:
			return new AbstractMap.SimpleEntry<ResourceLocation, Rotation>(ROOM, Rotation.NONE);
		case TUNNEL_EW:
			return new AbstractMap.SimpleEntry<ResourceLocation, Rotation>(TUNNEL, Rotation.CLOCKWISE_90);
		case TUNNEL_NS:
			return new AbstractMap.SimpleEntry<ResourceLocation, Rotation>(TUNNEL, Rotation.NONE);
		default:
			return new AbstractMap.SimpleEntry<ResourceLocation, Rotation>(new ResourceLocation(""), Rotation.NONE);
		}
	}

}
