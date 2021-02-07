package com.cy4.betterdungeons.common.block;

import com.cy4.betterdungeons.common.container.KeyGeneratorContainer;
import com.cy4.betterdungeons.common.te.KeyGeneratorTileEntity;
import com.cy4.betterdungeons.core.init.TileEntityTypesInit;
import com.cy4.betterdungeons.core.network.data.PlayerKeyGeneratorPlacingData;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class KeyGeneratorBlock extends Block {

	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public static BooleanProperty READY = BooleanProperty.create("ready");

	public KeyGeneratorBlock() {
		super(Properties.create(Material.ROCK, MaterialColor.DIAMOND).setRequiresTool().hardnessAndResistance(3f, 3600000.0F));
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(READY, Boolean.valueOf(true)));
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

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	public static KeyGeneratorTileEntity getTile(World world, BlockPos pos, BlockState state) {
		TileEntity tileEntity = world.getTileEntity(pos);

		if ((!(tileEntity instanceof KeyGeneratorTileEntity)))
			return null;

		return (KeyGeneratorTileEntity) tileEntity;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return TileEntityTypesInit.KEY_GENERATOR_TILE_ENTITY_TYPE.get().create();
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
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn,
			BlockRayTraceResult hit) {
		if (!world.isRemote) {
			TileEntity tileEntity = world.getTileEntity(pos);
			if (tileEntity instanceof KeyGeneratorTileEntity) {
				INamedContainerProvider containerProvider = new INamedContainerProvider() {
					@Override
					public ITextComponent getDisplayName() {
						return new TranslationTextComponent("container.betterdungeons.key_generator");
					}

					@Override
					public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
						return new KeyGeneratorContainer(i, world, pos, playerInventory, playerEntity);
					}
				};
				NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getPos());
			} else {
				throw new IllegalStateException("Our named container provider is missing!");
			}
		}
		return ActionResultType.SUCCESS;
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
