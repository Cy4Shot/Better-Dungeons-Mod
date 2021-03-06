package com.cy4.betterdungeons.common.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.recipe.sapling.SaplingInfo;
import com.cy4.betterdungeons.common.recipe.soil.SoilCompatibility;
import com.cy4.betterdungeons.common.recipe.soil.SoilInfo;
import com.cy4.betterdungeons.common.te.BonsaiPotTileEntity;
import com.cy4.betterdungeons.core.init.RecipesInit;
import com.cy4.betterdungeons.core.init.TileEntityTypesInit;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

// Yoinked From Bonsai Pots (MINE NOW !!)
public class BonsaiPotBlock extends DropSelfBlock implements IGrowable, IWaterLoggable {
	private final Random rand = new Random();
	private final VoxelShape shape = VoxelShapes.create(0.065f, 0.005f, 0.065f, 0.935f, 0.185f, 0.935f);
	boolean hopping;

	public BonsaiPotBlock(boolean hopping) {
		super(Properties.create(Material.CLAY, MaterialColor.CLAY).hardnessAndResistance(2.0F).sound(SoundType.WOOD)
				.harvestTool(ToolType.AXE).harvestLevel(0).notSolid());

		this.setDefaultState(this.stateContainer.getBaseState().with(BlockStateProperties.WATERLOGGED, Boolean.FALSE));
		this.hopping = hopping;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		if (this.hopping) {
			return TileEntityTypesInit.HOPPING_BONSAI_POT_TILE_ENTITY_TYPE.get().create();
		} else {
			return TileEntityTypesInit.BONSAI_POT_TILE_ENTITY_TYPE.get().create();
		}
	}

	public static BonsaiPotTileEntity getOwnTile(IBlockReader world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof BonsaiPotTileEntity)) {
			return null;
		}

		return (BonsaiPotTileEntity) te;
	}

	@SuppressWarnings({ "deprecation" })
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		BonsaiPotTileEntity tile = getOwnTile(worldIn, pos);
		if (tile == null) {
			super.onReplaced(state, worldIn, pos, newState, isMoving);
			return;
		}

		if (tile.hasSapling()) {
			InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.getSaplingStack());
		}

		if (tile.hasSoil()) {
			InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.getSoilStack());
		}

		if (tile.hasSoil() && tile.hasSapling() && tile.getProgress() >= 1.0f) {
			tile.dropLoot();
		}

		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}

	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn,
			BlockRayTraceResult hit) {
		if (player.isCrouching()) {
			return ActionResultType.FAIL;
		}

		if (!(world.getTileEntity(pos) instanceof BonsaiPotTileEntity)) {
			return ActionResultType.FAIL;
		}

		ItemStack playerStack = player.getHeldItem(Hand.MAIN_HAND);
		if (playerStack.isEmpty()) {
			playerStack = player.getHeldItem(Hand.OFF_HAND);
		}

		// No items in either of the hands -> no action here
		if (playerStack.isEmpty()) {
			return ActionResultType.FAIL;
		}

		if (world.isRemote) {
			return ActionResultType.SUCCESS;
		}

		BonsaiPotTileEntity pot = (BonsaiPotTileEntity) world.getTileEntity(pos);

		// Soil?
		SoilInfo soil = RecipesInit.soilRecipeHelper.getSoilForItem(world, playerStack);
		if (soil != null && !pot.hasSoil()) {
			if (player.isCreative()) {
				ItemStack soilStack = playerStack.copy();
				soilStack.setCount(1);
				pot.setSoil(soilStack);
			} else {
				pot.setSoil(playerStack.split(1));
			}
			return ActionResultType.SUCCESS;
		}

		// Sapling?
		SaplingInfo sapling = RecipesInit.saplingRecipeHelper.getSaplingInfoForItem(world, playerStack);
		if (sapling != null && !pot.hasSapling()) {
			if (!pot.hasSoil()) {
				SoilInfo randomSoil = RecipesInit.soilRecipeHelper.getRandomRecipe(world.getRecipeManager(), world.rand);
				if (randomSoil != null) {
					player.sendStatusMessage(new TranslationTextComponent("hint.bonsaitrees.pot_has_no_soil",
							randomSoil.ingredient.getMatchingStacks()[0].getDisplayName()), true);
				} else {
					BetterDungeons.LOGGER.warn("There is no soil available. Please check the config and logs for errors!");
				}

				return ActionResultType.SUCCESS;
			}

			SoilInfo potSoil = RecipesInit.soilRecipeHelper.getSoilForItem(world, pot.getSoilStack());
			if (!SoilCompatibility.INSTANCE.canTreeGrowOnSoil(sapling, potSoil)) {
				player.sendStatusMessage(new TranslationTextComponent("hint.bonsaitrees.incompatible_soil"), true);
				return ActionResultType.SUCCESS;
			}

			if (player.isCreative()) {
				ItemStack saplingStack = playerStack.copy();
				saplingStack.setCount(1);
				pot.setSapling(saplingStack);
			} else {
				pot.setSapling(playerStack.split(1));
			}
			return ActionResultType.SUCCESS;
		}

		boolean playerHasAxe = canCutBonsaiTree(playerStack, player);
		if (playerHasAxe) {
			// No sapling in pot
			if (!pot.hasSapling()) {
				return ActionResultType.FAIL;
			}

			boolean inWorkingCondition = !playerStack.isDamageable() || playerStack.getDamage() + 1 < playerStack.getMaxDamage();
			if (pot.getProgress() >= 1.0f && inWorkingCondition) {
				pot.dropLoot();
				pot.setSapling(pot.saplingStack);
				playerStack.attemptDamageItem(1, rand, (ServerPlayerEntity) player);
				return ActionResultType.SUCCESS;
			} else if (pot.growTicks >= 20 && pot.getProgress() <= 0.75f) {
				// Not ready and still under 75%
				pot.dropSapling();
				return ActionResultType.SUCCESS;
			}

			return ActionResultType.SUCCESS;
		}

		boolean playerHasShovel = playerStack.getItem().getHarvestLevel(playerStack, ToolType.SHOVEL, player,
				Blocks.DIRT.getDefaultState()) != -1;
		if (playerHasShovel) {
			if (pot.hasSapling()) {
				player.sendStatusMessage(new TranslationTextComponent("hint.bonsaitrees.can_not_remove_soil_with_sapling"), true);
				return ActionResultType.FAIL;
			}

			if (!pot.hasSoil()) {
				return ActionResultType.FAIL;
			}

			pot.dropSoil();
			return ActionResultType.SUCCESS;
		}

		return super.onBlockActivated(state, world, pos, player, handIn, hit);
	}

	private boolean canCutBonsaiTree(ItemStack stack, PlayerEntity player) {
		return stack.getItem().getHarvestLevel(stack, ToolType.AXE, player, Blocks.OAK_PLANKS.getDefaultState()) != -1;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(BlockStateProperties.WATERLOGGED);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		FluidState fluidState = context.getWorld().getFluidState(context.getPos());
		return super.getStateForPlacement(context).with(BlockStateProperties.WATERLOGGED,
				Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos,
			BlockPos facingPos) {
		if (stateIn.get(BlockStateProperties.WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}

		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return !state.get(BlockStateProperties.WATERLOGGED);
	}

	@SuppressWarnings("deprecation")
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
		return shape;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean canGrow(IBlockReader world, BlockPos pos, BlockState state, boolean isClient) {
		if (!(world.getTileEntity(pos) instanceof BonsaiPotTileEntity)) {
			return false;
		}

		BonsaiPotTileEntity tile = (BonsaiPotTileEntity) world.getTileEntity(pos);
		return tile.isGrowing();
	}

	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, BlockState state) {
		if (!(world.getTileEntity(pos) instanceof BonsaiPotTileEntity)) {
			return false;
		}

		BonsaiPotTileEntity tile = (BonsaiPotTileEntity) world.getTileEntity(pos);
		if (!tile.isGrowing()) {
			return false;
		}

		return (double) world.rand.nextFloat() < 0.45D;
	}

	@Override
	public void grow(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
		if (!(world.getTileEntity(pos) instanceof BonsaiPotTileEntity)) {
			return;
		}

		BonsaiPotTileEntity tile = (BonsaiPotTileEntity) world.getTileEntity(pos);
		tile.boostProgress();
	}
}