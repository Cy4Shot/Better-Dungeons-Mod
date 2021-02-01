package com.cy4.betterdungeons.common.block;

import java.util.Random;

import com.cy4.betterdungeons.common.entity.IBoss;
import com.cy4.betterdungeons.common.item.BossKeyItem;
import com.cy4.betterdungeons.common.world.spawner.EntityScaler;
import com.cy4.betterdungeons.core.config.DungeonsConfig;
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
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BossBlock extends Block {

	public static final IntegerProperty COMPLETION = IntegerProperty.create("completion", 0, 4);

	public BossBlock() {
		super(Properties.create(Material.ROCK).sound(SoundType.METAL).hardnessAndResistance(-1.0F, 3600000.0F).noDrops());
		this.setDefaultState(this.stateContainer.getBaseState().with(COMPLETION, 0));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return Block.makeCuboidShape(4f, 0f, 4f, 12f, 32f, 12f);
//        return super.getShape(state, worldIn, pos, context);
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

		BlockState newState = state.with(COMPLETION, MathHelper.clamp(state.get(COMPLETION) + 1, 0, 4));
		world.setBlockState(pos, newState);

		if (world.isRemote) {
			return ActionResultType.SUCCESS;
		}

		this.spawnParticles(world, pos);

		if (newState.get(COMPLETION) == 4) {
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

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(COMPLETION);
	}

}
