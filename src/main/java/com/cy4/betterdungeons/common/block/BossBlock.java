package com.cy4.betterdungeons.common.block;

import java.util.Random;

import com.cy4.betterdungeons.common.entity.IBoss;
import com.cy4.betterdungeons.common.item.BossKeyItem;
import com.cy4.betterdungeons.common.te.BossBlockTileEntity;
import com.cy4.betterdungeons.common.world.spawner.EntityScaler;
import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.cy4.betterdungeons.core.init.TileEntityTypesInit;
import com.cy4.betterdungeons.core.network.data.DungeonRunData;
import com.cy4.betterdungeons.core.network.stats.DungeonRun;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BossBlock extends Block {

	public BossBlock() {
		super(Properties.create(Material.ROCK).sound(SoundType.METAL).hardnessAndResistance(-1.0F, 3600000.0F).noDrops());
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockRayTraceResult hit) {
		ItemStack heldStack = player.getHeldItem(hand);

		if (heldStack.getItem() instanceof BossKeyItem) {
			if (!player.isCreative()) {
				heldStack.shrink(1);
			}
		} else {
			return ActionResultType.PASS;
		}
		BossBlockTileEntity te = ((BossBlockTileEntity) world.getTileEntity(pos));
		te.addKey();

		if (world.isRemote) {
			return ActionResultType.SUCCESS;
		}

		this.spawnParticles(world, pos);

		if (te.getKeys() == 4) {
			DungeonRun raid = DungeonRunData.get((ServerWorld) world).getAt(pos);

			if (raid != null) {
				spawnBoss(raid, (ServerWorld) world, pos, EntityScaler.Type.BOSS);
			}

			world.setBlockState(pos, Blocks.AIR.getDefaultState());
		}

		return ActionResultType.SUCCESS;
	}

	public void spawnBoss(DungeonRun raid, ServerWorld world, BlockPos pos, EntityScaler.Type type) {
		LivingEntity boss;

		if (type == EntityScaler.Type.BOSS) {
			boss = DungeonsConfig.DUNGEON_MOBS.getForLevel(raid.level).BOSS_POOL.getRandom(world.getRandom()).create(world);
		} else {
			return;
		}
		boss.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 0.2D, pos.getZ() + 0.5D, 0.0F, 0.0F);
		world.summonEntity(boss);

		boss.getTags().add("IBoss");
		if (boss instanceof IBoss) {
			((IBoss) boss).getServerBossInfo().setVisible(true);
		}

		EntityScaler.scaleVault(boss, raid.level, new Random(), EntityScaler.Type.BOSS);
		boss.setCustomName(new StringTextComponent("Boss"));
	}

	private void spawnParticles(World world, BlockPos pos) {
		for (int i = 0; i < 20; ++i) {
			double d0 = world.rand.nextGaussian() * 0.02D;
			double d1 = world.rand.nextGaussian() * 0.02D;
			double d2 = world.rand.nextGaussian() * 0.02D;

			((ServerWorld) world).spawnParticle(ParticleTypes.POOF, pos.getX() + world.rand.nextDouble() - d0,
					pos.getY() + world.rand.nextDouble() - d1, pos.getZ() + world.rand.nextDouble() - d2, 10, d0, d1, d2, 1.0D);
		}

		world.playSound(null, pos, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return TileEntityTypesInit.BOSS_BLOCK_TILE_ENTITY_TYPE.get().create();
	}

}
