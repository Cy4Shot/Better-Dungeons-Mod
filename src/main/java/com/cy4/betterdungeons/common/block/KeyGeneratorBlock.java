package com.cy4.betterdungeons.common.block;

import com.cy4.betterdungeons.common.te.KeyGeneratorTileEntity;
import com.cy4.betterdungeons.core.init.TileEntityTypesInit;
import com.cy4.betterdungeons.core.network.data.PlayerKeyGeneratorPlacingData;
import com.cy4.betterdungeons.core.network.stats.PlayerPlacingStats;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class KeyGeneratorBlock extends Block {

	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public static BooleanProperty READY = BooleanProperty.create("ready");

	public KeyGeneratorBlock() {
		super(Properties.create(Material.ROCK, MaterialColor.DIAMOND).setRequiresTool().hardnessAndResistance(3f,
				3600000.0F));
		this.setDefaultState(
				this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(READY, Boolean.valueOf(true)));
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.with(FACING, mirrorIn.mirror(state.get(FACING)));
	}

	public void ready(boolean e, BlockState state, World worldIn, BlockPos pos) {
		if (!worldIn.isRemote) {
			BlockState blockstateCycled = state.with(READY, Boolean.valueOf(e));
			worldIn.setBlockState(pos, blockstateCycled, 3);
		}
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(FACING, READY);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return TileEntityTypesInit.KEY_GENERATOR_TILE_ENTITY_TYPE.get().create();
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if (!worldIn.isRemote) {
			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile instanceof KeyGeneratorTileEntity) {
				NetworkHooks.openGui((ServerPlayerEntity) player, (KeyGeneratorTileEntity) tile, pos);
				return ActionResultType.SUCCESS;
			}

		}
		return ActionResultType.FAIL;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (!worldIn.isRemote()) {
			PlayerKeyGeneratorPlacingData data = PlayerKeyGeneratorPlacingData.get((ServerWorld) worldIn);
			PlayerPlacingStats stats = data.getPlaceStats((PlayerEntity) placer);
			if (stats.canPlace()) {
				data.setCanPlace((PlayerEntity) placer, false);
			} else {
				worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
				((PlayerEntity) placer).addItemStackToInventory(stack);
			}
		}
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!worldIn.isRemote()) {
			PlayerKeyGeneratorPlacingData data = PlayerKeyGeneratorPlacingData.get((ServerWorld) worldIn);
			data.setCanPlace(player, true);
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}
}
