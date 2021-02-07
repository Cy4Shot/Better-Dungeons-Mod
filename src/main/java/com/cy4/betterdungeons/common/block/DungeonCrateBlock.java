package com.cy4.betterdungeons.common.block;

import javax.annotation.Nullable;

import com.cy4.betterdungeons.BetterDungeons;
import com.cy4.betterdungeons.common.container.DungeonCrateContainer;
import com.cy4.betterdungeons.common.te.DungeonCrateTileEntity;
import com.cy4.betterdungeons.core.init.TileEntityTypesInit;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class DungeonCrateBlock extends Block {
	public DungeonCrateBlock() {
		super(Properties.create(Material.IRON, MaterialColor.IRON).hardnessAndResistance(2.0F, 3600000.0F).sound(SoundType.METAL));
	}

	public static ItemStack getCrateWithLoot(DungeonCrateBlock crateType, NonNullList<ItemStack> items) {
		if (items.size() > 27) {
			BetterDungeons.LOGGER.error("Attempted to get a crate with more than 27 items. Check crate loot table.");
			return ItemStack.EMPTY;
		}
		ItemStack crate = new ItemStack(crateType);
		CompoundNBT nbt = new CompoundNBT();
		ItemStackHelper.saveAllItems(nbt, items);
		if (!nbt.isEmpty()) {
			crate.setTagInfo("BlockEntityTag", nbt);
		}
		return crate;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return TileEntityTypesInit.DUNGEON_CRATE_TILE_ENTITY_TYPE.get().create();
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn,
			BlockRayTraceResult hit) {
		if (!world.isRemote) {
			TileEntity tileEntity = world.getTileEntity(pos);
			if (tileEntity instanceof DungeonCrateTileEntity) {
				INamedContainerProvider containerProvider = new INamedContainerProvider() {
					@Override
					public ITextComponent getDisplayName() {
						return new TranslationTextComponent("container.Dungeon.Dungeon_crate");
					}

					@Override
					public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
						return new DungeonCrateContainer(i, world, pos, playerInventory);
					}
				};
				NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getPos());
			} else {
				throw new IllegalStateException("Our named container provider is missing!");
			}
		}
		return ActionResultType.SUCCESS;
	}

	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		if (worldIn.isRemote)
			super.onBlockHarvested(worldIn, pos, state, player);
		
		if (player.isCreative()) {
			super.onBlockHarvested(worldIn, pos, state, player);
			return;
		}

		DungeonCrateBlock block = (DungeonCrateBlock) state.getBlock();
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof DungeonCrateTileEntity) {
			DungeonCrateTileEntity crate = (DungeonCrateTileEntity) tileentity;

			ItemStack itemstack = new ItemStack(block);
			CompoundNBT compoundnbt = crate.saveToNbt();
			if (!compoundnbt.isEmpty()) {
				itemstack.setTagInfo("BlockEntityTag", compoundnbt);
			}

			ItemEntity itementity = new ItemEntity(worldIn, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D,
					(double) pos.getZ() + 0.5D, itemstack);
			itementity.setDefaultPickupDelay();
			worldIn.addEntity(itementity);

		}

		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (worldIn.isRemote)
			return;

		CompoundNBT compoundnbt = stack.getChildTag("BlockEntityTag");
		if (compoundnbt == null)
			return;

		DungeonCrateTileEntity crate = getCrateTileEntity(worldIn, pos);
		if (crate == null)
			return;

		crate.loadFromNBT(compoundnbt);

		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}

	private DungeonCrateTileEntity getCrateTileEntity(World worldIn, BlockPos pos) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te == null || !(te instanceof DungeonCrateTileEntity))
			return null;
		DungeonCrateTileEntity crate = (DungeonCrateTileEntity) worldIn.getTileEntity(pos);
		return crate;
	}
}
