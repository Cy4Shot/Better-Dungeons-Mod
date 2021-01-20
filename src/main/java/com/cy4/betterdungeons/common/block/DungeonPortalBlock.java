package com.cy4.betterdungeons.common.block;

import java.util.Optional;
import java.util.Random;

import javax.annotation.Nullable;

import com.cy4.betterdungeons.core.init.BlockInit;
import com.cy4.betterdungeons.core.init.DimensionInit;
import com.cy4.betterdungeons.core.network.data.DungeonRunData;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
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
		super(Properties.create(Material.PORTAL).hardnessAndResistance(-1F).doesNotBlockMovement().setLightLevel((state) -> 10).noDrops()
				.setAllowsSpawn(BlockInit::neverAllowSpawn));
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
			return DungeonPortalBlock$size1.isValid() && DungeonPortalBlock$size1.portalBlockCount == 0 ? DungeonPortalBlock$size1 : null;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos,
			BlockPos facingPos) {
		Direction.Axis direction$axis = facing.getAxis();
		Direction.Axis direction$axis1 = stateIn.get(AXIS);
		boolean flag = direction$axis1 != direction$axis && direction$axis.isHorizontal();
		return !flag && facingState.getBlock() != this && !(new Size(worldIn, currentPos, direction$axis1)).validatePortal()
				? Blocks.AIR.getDefaultState()
				: super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (world.isRemote || !(entity instanceof PlayerEntity))
			return;
		if (entity.isPassenger() || entity.isBeingRidden() || !entity.isNonBoss())
			return;

		ServerPlayerEntity player = (ServerPlayerEntity) entity;
		VoxelShape playerVoxel = VoxelShapes.create(player.getBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ()));

		if (VoxelShapes.compare(playerVoxel, state.getShape(world, pos), IBooleanFunction.AND)) {
			RegistryKey<World> worldKey = world.getDimensionKey() == DimensionInit.DUNGEON_WORLD ? World.OVERWORLD
					: DimensionInit.DUNGEON_WORLD;
			ServerWorld destination = ((ServerWorld) world).getServer().getWorld(worldKey);

			if (destination == null)
				return;

			// Reset cooldown.
			if (player.func_242280_ah()) {
				player.func_242279_ag();
				return;
			}

			world.getServer().runAsync(() -> {
				if (worldKey == World.OVERWORLD) {
					ServerPlayerEntity playerEntity = (ServerPlayerEntity) entity;
					BlockPos blockPos = playerEntity.func_241140_K_();
					Optional<Vector3d> spawn = blockPos == null ? Optional.empty()
							: PlayerEntity.func_242374_a(destination, blockPos, playerEntity.func_242109_L(), playerEntity.func_241142_M_(),
									true);

					if (spawn.isPresent()) {
						BlockState blockstate = destination.getBlockState(blockPos);
						Vector3d vector3d = spawn.get();

						if (!blockstate.isIn(BlockTags.BEDS) && !blockstate.isIn(Blocks.RESPAWN_ANCHOR)) {
							playerEntity.teleport(destination, vector3d.x, vector3d.y, vector3d.z, playerEntity.func_242109_L(), 0.0F);
						} else {
							Vector3d vector3d1 = Vector3d.copyCenteredHorizontally(blockPos).subtract(vector3d).normalize();
							playerEntity
									.teleport(destination, vector3d.x, vector3d.y, vector3d.z,
											(float) MathHelper.wrapDegrees(
													MathHelper.atan2(vector3d1.z, vector3d1.x) * (double) (180F / (float) Math.PI) - 90.0D),
											0.0F);
						}
					} else {
						this.moveToSpawn(destination, player);
					}
				} else if (worldKey == DimensionInit.DUNGEON_WORLD) {
					DungeonRunData.get(destination).startNew(player);
				}
			});

			if (worldKey == DimensionInit.DUNGEON_WORLD) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
			}

			player.func_242279_ag();
		}
	}

	private void moveToSpawn(ServerWorld world, ServerPlayerEntity player) {
		BlockPos blockpos = world.getSpawnPoint();

		if (world.getDimensionType().hasSkyLight() && world.getServer().getGameType() != GameType.ADVENTURE) {
			int i = Math.max(0, world.getServer().getSpawnRadius(world));
			int j = MathHelper.floor(world.getWorldBorder().getClosestDistance(blockpos.getX(), blockpos.getZ()));
			if (j < i) {
				i = j;
			}

			if (j <= 1) {
				i = 1;
			}

			long k = i * 2 + 1;
			long l = k * k;
			int i1 = l > 2147483647L ? Integer.MAX_VALUE : (int) l;
			int j1 = i1 <= 16 ? i1 - 1 : 17;
			int k1 = (new Random()).nextInt(i1);

			for (int l1 = 0; l1 < i1; ++l1) {
				int i2 = (k1 + j1 * l1) % i1;
				int j2 = i2 % (i * 2 + 1);
				int k2 = i2 / (i * 2 + 1);
				BlockPos blockpos1 = getSpawnPoint(world, blockpos.getX() + j2 - i, blockpos.getZ() + k2 - i, false);
				if (blockpos1 != null) {
					player.teleport(world, blockpos1.getX(), blockpos1.getY(), blockpos1.getZ(), 0.0F, 0.0F);

					if (world.hasNoCollisions(player)) {
						break;
					}
				}
			}
		} else {
			player.teleport(world, blockpos.getX(), blockpos.getY(), blockpos.getZ(), 0.0F, 0.0F);

			while (!world.hasNoCollisions(player) && player.getPosY() < 255.0D) {
				player.teleport(world, player.getPosX(), player.getPosY() + 1.0D, player.getPosZ(), 0.0F, 0.0F);
			}
		}
	}

	protected static BlockPos getSpawnPoint(ServerWorld p_241092_0_, int p_241092_1_, int p_241092_2_, boolean p_241092_3_) {
		BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_241092_1_, 0, p_241092_2_);
		Biome biome = p_241092_0_.getBiome(blockpos$mutable);
		boolean flag = p_241092_0_.getDimensionType().getHasCeiling();
		BlockState blockstate = biome.getGenerationSettings().getSurfaceBuilderConfig().getTop();
		if (p_241092_3_ && !blockstate.getBlock().isIn(BlockTags.VALID_SPAWN)) {
			return null;
		} else {
			Chunk chunk = p_241092_0_.getChunk(p_241092_1_ >> 4, p_241092_2_ >> 4);
			int i = flag ? p_241092_0_.getChunkProvider().getChunkGenerator().getGroundHeight()
					: chunk.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, p_241092_1_ & 15, p_241092_2_ & 15);
			if (i < 0) {
				return null;
			} else {
				int j = chunk.getTopBlockY(Heightmap.Type.WORLD_SURFACE, p_241092_1_ & 15, p_241092_2_ & 15);
				if (j <= i && j > chunk.getTopBlockY(Heightmap.Type.OCEAN_FLOOR, p_241092_1_ & 15, p_241092_2_ & 15)) {
					return null;
				} else {
					for (int k = i + 1; k >= 0; --k) {
						blockpos$mutable.setPos(p_241092_1_, k, p_241092_2_);
						BlockState blockstate1 = p_241092_0_.getBlockState(blockpos$mutable);
						if (!blockstate1.getFluidState().isEmpty()) {
							break;
						}

						if (blockstate1.equals(blockstate)) {
							return blockpos$mutable.up().toImmutable();
						}
					}

					return null;
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
				if (!this.canConnect(this.world.getBlockState(blockpos))
						|| !(this.world.getBlockState(blockpos.down()).getBlock().equals(BlockInit.DUNGEON_PORTAL_FRAME.get()))) {
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
			return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
		}

		public void placePortalBlocks() {
			for (int i = 0; i < this.width; ++i) {
				BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i);

				for (int j = 0; j < this.height; ++j) {
					this.world.setBlockState(blockpos.up(j),
							BlockInit.DUNGEON_PORTAL.get().getDefaultState().with(DungeonPortalBlock.AXIS, this.axis), 18);
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
