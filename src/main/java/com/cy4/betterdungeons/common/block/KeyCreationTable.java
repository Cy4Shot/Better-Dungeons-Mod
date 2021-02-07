package com.cy4.betterdungeons.common.block;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.cy4.betterdungeons.common.recipe.KeyCreationTableRecipe;
import com.cy4.betterdungeons.common.recipe.RequiredItem;
import com.cy4.betterdungeons.common.te.KeyCreationTableTileEntity;
import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.cy4.betterdungeons.core.init.BlockInit;
import com.cy4.betterdungeons.core.init.ItemInit;
import com.cy4.betterdungeons.core.init.TileEntityTypesInit;
import com.cy4.betterdungeons.core.network.data.PlayerKeyCreationTableData;
import com.cy4.betterdungeons.core.network.data.PlayerKeyCreationTablePlacingData;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class KeyCreationTable extends Block {

	public static final VoxelShape shape = Stream
			.of(Block.makeCuboidShape(1, 10, 1, 15, 12, 15), Block.makeCuboidShape(4, 8, 4, 12, 10, 12),
					Block.makeCuboidShape(6, 2, 6, 10, 8, 10), Block.makeCuboidShape(4, 0, 4, 12, 2, 12))
			.reduce((v1, v2) -> {
				return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);
			}).get();

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public KeyCreationTable() {
		super(Properties.create(Material.ROCK, MaterialColor.DIAMOND).setRequiresTool().hardnessAndResistance(3f, 3600000.0F).notSolid()
				.setAllowsSpawn(BlockInit::neverAllowSpawn));
		this.setDefaultState(this.stateContainer.getBaseState().with(POWERED, Boolean.FALSE));

	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(POWERED, Boolean.FALSE);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return shape;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(POWERED);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return TileEntityTypesInit.KEY_CREATION_TABLE_TILE_ENTITY_TYPE.get().create();
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn,
			BlockRayTraceResult hit) {
		if (worldIn.isRemote)
			return ActionResultType.SUCCESS;
		if (handIn != Hand.MAIN_HAND)
			return ActionResultType.SUCCESS;

		KeyCreationTableTileEntity table = getAltarTileEntity(worldIn, pos);
		if (table == null)
			return ActionResultType.SUCCESS;

		// infusion in process.. do nothing
		if (table.getInfusionTimer() != -1)
			return ActionResultType.SUCCESS;

		if (player.isSneaking() && table.containsKey() && player.getHeldItemMainhand().getItem() == Items.AIR) {

			player.setHeldItem(Hand.MAIN_HAND, new ItemStack(ItemInit.EMPTY_KEY.get()));
			table.setContainsKey(false);

			table.sendUpdates();
			return ActionResultType.SUCCESS;
		}

		ItemStack heldItem = player.getHeldItemMainhand();
		if (heldItem.getItem() != ItemInit.EMPTY_KEY.get())
			return ActionResultType.SUCCESS;

		PlayerKeyCreationTableData data = PlayerKeyCreationTableData.get((ServerWorld) worldIn);

		// player has no recipe, give them one.
		if (!data.getRecipes().containsKey(player.getUniqueID())) {
			List<RequiredItem> items = DungeonsConfig.KEY_CREATION_TABLE.getRequiredItemsFromConfig((ServerWorld) worldIn, player);
			data.add(player.getUniqueID(), new KeyCreationTableRecipe(player.getUniqueID(), items));
		}

		table.setContainsKey(true);

		heldItem.setCount(heldItem.getCount() - 1);
		table.sendUpdates();
		return ActionResultType.SUCCESS;
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (worldIn.isRemote)
			return;
		boolean powered = worldIn.isBlockPowered(pos);
		if (powered != state.get(POWERED)) {
			if (powered) {
				KeyCreationTableTileEntity table = getAltarTileEntity(worldIn, pos);
				if (table != null && table.containsKey()) {
					if (table.getInfusionTimer() != -1)
						return;
					PlayerEntity player = worldIn.getClosestPlayer(table.getPos().getX(), table.getPos().getY(), table.getPos().getZ(),
							DungeonsConfig.KEY_CREATION_TABLE.PLAYER_RANGE_CHECK, null);
					if (player != null) {
						PlayerKeyCreationTableData data = PlayerKeyCreationTableData.get((ServerWorld) worldIn);
						KeyCreationTableRecipe recipe = data.getRecipe(player);
						if (recipe != null && recipe.isComplete()) {
							data.remove(player.getUniqueID());
							table.startInfusionTimer(DungeonsConfig.KEY_CREATION_TABLE.INFUSION_TIME);
						}
					}
				}
			}
		}
		worldIn.setBlockState(pos, state.with(POWERED, powered), 3);
	}

	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
		return true;
	}

	private KeyCreationTableTileEntity getAltarTileEntity(World worldIn, BlockPos pos) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te == null || !(te instanceof KeyCreationTableTileEntity))
			return null;
		KeyCreationTableTileEntity table = (KeyCreationTableTileEntity) worldIn.getTileEntity(pos);
		return table;
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!worldIn.isRemote()) {
			PlayerKeyCreationTablePlacingData data = PlayerKeyCreationTablePlacingData.get((ServerWorld) worldIn);
			data.setCanPlace(player, true);
		}

		KeyCreationTableTileEntity table = getAltarTileEntity(worldIn, pos);
		if (table == null || player.isCreative()) {
			super.onBlockHarvested(worldIn, pos, state, player);
			return;
		}

		if (table.containsKey()) {
			ItemEntity entity = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemInit.EMPTY_KEY.get()));
			worldIn.addEntity(entity);
		}
		ItemEntity entity = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(BlockInit.KEY_CREATION_TABLE.get()));
		worldIn.addEntity(entity);

		super.onBlockHarvested(worldIn, pos, state, player);
	}
}
