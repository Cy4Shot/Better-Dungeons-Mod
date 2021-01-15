package com.cy4.betterdungeons.common.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.cy4.betterdungeons.common.world.DungeonTeleporter;
import com.cy4.betterdungeons.core.init.BlockInit;
import com.cy4.betterdungeons.core.init.DimensionInit;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Cancelable;

public class DungeonPortalBlock extends Block {

	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
	protected static final VoxelShape X_AABB = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
	protected static final VoxelShape Z_AABB = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

	public DungeonPortalBlock() {
		super(Properties.create(Material.PORTAL).hardnessAndResistance(-1F).doesNotBlockMovement()
				.setLightLevel((state) -> 10).noDrops());
		setDefaultState(stateContainer.getBaseState().with(AXIS, Direction.Axis.X));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch (state.get(AXIS)) {
		case Z:
			return Z_AABB;
		case X:
		default:
			return X_AABB;
		}
	}

	public boolean trySpawnPortal(IWorld worldIn, BlockPos pos) {
		DungeonPortalBlock.Size DungeonPortalBlock$size = this.isPortal(worldIn, pos);
		if (DungeonPortalBlock$size != null && !onTrySpawnPortal(worldIn, pos, DungeonPortalBlock$size)) {
			DungeonPortalBlock$size.placePortalBlocks();
			return true;
		} else {
			return false;
		}
	}

	public static boolean onTrySpawnPortal(IWorld world, BlockPos pos, DungeonPortalBlock.Size size) {
		return MinecraftForge.EVENT_BUS.post(new PortalSpawnEvent(world, pos, world.getBlockState(pos), size));
	}

	@Cancelable
	public static class PortalSpawnEvent extends BlockEvent {
		private final DungeonPortalBlock.Size size;

		public PortalSpawnEvent(IWorld world, BlockPos pos, BlockState state, DungeonPortalBlock.Size size) {
			super(world, pos, state);
			this.size = size;
		}

		public DungeonPortalBlock.Size getPortalSize() {
			return size;
		}
	}

	@Nullable
	public DungeonPortalBlock.Size isPortal(IWorld worldIn, BlockPos pos) {
		DungeonPortalBlock.Size DungeonPortalBlock$size = new Size(worldIn, pos, Direction.Axis.X);
		if (DungeonPortalBlock$size.isValid() && DungeonPortalBlock$size.portalBlockCount == 0) {
			return DungeonPortalBlock$size;
		} else {
			DungeonPortalBlock.Size DungeonPortalBlock$size1 = new Size(worldIn, pos, Direction.Axis.Z);
			return DungeonPortalBlock$size1.isValid() && DungeonPortalBlock$size1.portalBlockCount == 0
					? DungeonPortalBlock$size1
					: null;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
			BlockPos currentPos, BlockPos facingPos) {
		Direction.Axis direction$axis = facing.getAxis();
		Direction.Axis direction$axis1 = stateIn.get(AXIS);
		boolean flag = direction$axis1 != direction$axis && direction$axis.isHorizontal();
		return !flag && facingState.getBlock() != this
				&& !(new Size(worldIn, currentPos, direction$axis1)).validatePortal() ? Blocks.AIR.getDefaultState()
						: super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entity) {
		if (!entity.isPassenger() && !entity.isBeingRidden() && entity.isNonBoss()) {
			if (entity.func_242280_ah()) {
				entity.func_242279_ag();
			} else {
				if (!entity.world.isRemote && !pos.equals(entity.field_242271_ac)) {
					entity.field_242271_ac = pos.toImmutable();
				}
				World entityWorld = entity.world;
				if (entityWorld != null) {
					MinecraftServer minecraftserver = entityWorld.getServer();
					RegistryKey<World> destination = entity.world.getDimensionKey() == DimensionInit.DUNGEON_WORLD
							? World.OVERWORLD
							: DimensionInit.DUNGEON_WORLD;
					if (minecraftserver != null) {
						ServerWorld destinationWorld = minecraftserver.getWorld(destination);
						if (destinationWorld != null && minecraftserver.getAllowNether() && !entity.isPassenger()) {
							entity.world.getProfiler().startSection("undergarden_portal");
							entity.func_242279_ag();
							DungeonTeleporter.teleportToDimension((PlayerEntity) entity, destinationWorld, pos);
							entity.world.getProfiler().endSection();
						}
					}
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
	}

	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		switch (rot) {
		case COUNTERCLOCKWISE_90:
		case CLOCKWISE_90:
			switch (state.get(AXIS)) {
			case Z:
				return state.with(AXIS, Direction.Axis.X);
			case X:
				return state.with(AXIS, Direction.Axis.Z);
			default:
				return state;
			}
		default:
			return state;
		}
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(AXIS);
	}

	public static class Size {
		private final IWorld world;
		private final Direction.Axis axis;
		private final Direction rightDir;
		private final Direction leftDir;
		private int portalBlockCount;
		@Nullable
		private BlockPos bottomLeft;
		private int height;
		private int width;

		public Size(IWorld worldIn, BlockPos pos, Direction.Axis axisIn) {
			this.world = worldIn;
			this.axis = axisIn;
			if (axisIn == Direction.Axis.X) {
				this.leftDir = Direction.EAST;
				this.rightDir = Direction.WEST;
			} else {
				this.leftDir = Direction.NORTH;
				this.rightDir = Direction.SOUTH;
			}

			for (BlockPos blockpos = pos; pos.getY() > blockpos.getY() - 21 && pos.getY() > 0
					&& this.canConnect(worldIn.getBlockState(pos.down())); pos = pos.down()) {
				;
			}

			int i = this.getDistanceUntilEdge(pos, this.leftDir) - 1;
			if (i >= 0) {
				this.bottomLeft = pos.offset(this.leftDir, i);
				this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);
				if (this.width < 2 || this.width > 21) {
					this.bottomLeft = null;
					this.width = 0;
				}
			}

			if (this.bottomLeft != null) {
				this.height = this.calculatePortalHeight();
			}

		}

		protected int getDistanceUntilEdge(BlockPos pos, Direction directionIn) {
			int i;
			for (i = 0; i < 22; ++i) {
				BlockPos blockpos = pos.offset(directionIn, i);
				if (!this.canConnect(this.world.getBlockState(blockpos)) || !(this.world.getBlockState(blockpos.down())
						.getBlock().equals(BlockInit.DUNGEON_PORTAL_FRAME.get()))) {
					break;
				}
			}

			BlockPos framePos = pos.offset(directionIn, i);
			return this.world.getBlockState(framePos).getBlock().equals(BlockInit.DUNGEON_PORTAL_FRAME.get()) ? i : 0;
		}

		public int getHeight() {
			return this.height;
		}

		public int getWidth() {
			return this.width;
		}

		protected int calculatePortalHeight() {
			label56: for (this.height = 0; this.height < 21; ++this.height) {
				for (int i = 0; i < this.width; ++i) {
					BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i).up(this.height);
					BlockState blockstate = this.world.getBlockState(blockpos);
					if (!this.canConnect(blockstate)) {
						break label56;
					}

					Block block = blockstate.getBlock();
					if (block == BlockInit.DUNGEON_PORTAL.get()) {
						++this.portalBlockCount;
					}

					if (i == 0) {
						BlockPos framePos = blockpos.offset(this.leftDir);
						if (!(this.world.getBlockState(framePos).getBlock().equals(BlockInit.DUNGEON_PORTAL_FRAME.get()))) {
							break label56;
						}
					} else if (i == this.width - 1) {
						BlockPos framePos = blockpos.offset(this.rightDir);
						if (!(this.world.getBlockState(framePos).getBlock().equals(BlockInit.DUNGEON_PORTAL_FRAME.get()))) {
							break label56;
						}
					}
				}
			}

			for (int j = 0; j < this.width; ++j) {
				BlockPos framePos = this.bottomLeft.offset(this.rightDir, j).up(this.height);
				if (!(this.world.getBlockState(framePos).getBlock().equals(BlockInit.DUNGEON_PORTAL_FRAME.get()))) {
					this.height = 0;
					break;
				}
			}

			if (this.height <= 21 && this.height >= 3) {
				return this.height;
			} else {
				this.bottomLeft = null;
				this.width = 0;
				this.height = 0;
				return 0;
			}
		}

		@SuppressWarnings("deprecation")
		protected boolean canConnect(BlockState pos) {
			Block block = pos.getBlock();
			return pos.isAir() || block == BlockInit.DUNGEON_PORTAL.get();
		}

		public boolean isValid() {
			return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3
					&& this.height <= 21;
		}

		public void placePortalBlocks() {
			for (int i = 0; i < this.width; ++i) {
				BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i);

				for (int j = 0; j < this.height; ++j) {
					this.world.setBlockState(blockpos.up(j), BlockInit.DUNGEON_PORTAL.get().getDefaultState()
							.with(DungeonPortalBlock.AXIS, this.axis), 18);
				}
			}

		}

		private boolean isPortalCountValidForSize() {
			return this.portalBlockCount >= this.width * this.height;
		}

		public boolean validatePortal() {
			return this.isValid() && this.isPortalCountValidForSize();
		}
	}

}
