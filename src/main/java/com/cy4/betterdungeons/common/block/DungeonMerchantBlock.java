package com.cy4.betterdungeons.common.block;

import javax.annotation.Nullable;

import com.cy4.betterdungeons.common.container.DungeonMerchantContainer;
import com.cy4.betterdungeons.common.item.MerchantItem;
import com.cy4.betterdungeons.common.merchant.Merchant;
import com.cy4.betterdungeons.common.te.DungeonMerchantTileEntity;
import com.cy4.betterdungeons.core.init.TileEntityTypesInit;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class DungeonMerchantBlock extends Block {

	public DungeonMerchantBlock() {
		super(Properties.create(Material.ROCK, MaterialColor.DIAMOND).setRequiresTool().hardnessAndResistance(3f, 3600000.0F));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return TileEntityTypesInit.DUNGEON_MERCHANT_TILE_ENTITY_TYPE.get().create();
	}

	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockRayTraceResult hit) {
		ItemStack heldStack = player.getHeldItem(hand);
		DungeonMerchantTileEntity machine = getMerchantTile(world, pos, state);
		if (machine == null)
			return ActionResultType.SUCCESS;

		if (!world.isRemote() && player.isSneaking()) {
			ItemStack core = machine.getMerchantStack();
			if (!player.addItemStackToInventory(core)) {
				player.dropItem(core, false);
			}
			machine.sendUpdates();
			return ActionResultType.SUCCESS;
		}

		if (heldStack.getItem() instanceof MerchantItem) {
			Merchant coreToInsert = MerchantItem.getCoreFromStack(heldStack);
			if (coreToInsert != null) {
				machine.addCore(coreToInsert);
				heldStack.shrink(1);
			}

			return ActionResultType.SUCCESS;

		} else {
			if (world.isRemote) {
				return ActionResultType.SUCCESS;
			}

			NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
				@Override
				public ITextComponent getDisplayName() {
					return new StringTextComponent("Dungeon Merchant");
				}

				@Nullable
				@Override
				public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
					return new DungeonMerchantContainer(windowId, world, pos, playerInventory, playerEntity);
				}
			}, (buffer) -> {
				buffer.writeBlockPos(pos);
			});
		}
		return super.onBlockActivated(state, world, pos, player, hand, hit);
	}

	public static DungeonMerchantTileEntity getMerchantTile(World world, BlockPos pos, BlockState state) {
		TileEntity tileEntity = world.getTileEntity(pos);

		if ((!(tileEntity instanceof DungeonMerchantTileEntity)))
			return null;

		return (DungeonMerchantTileEntity) tileEntity;
	}

}
