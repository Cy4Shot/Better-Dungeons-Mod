package com.cy4.betterdungeons.common.te;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.cy4.betterdungeons.common.recipe.KeyCreationTableRecipe;
import com.cy4.betterdungeons.common.recipe.RequiredItem;
import com.cy4.betterdungeons.core.config.DungeonsConfig;
import com.cy4.betterdungeons.core.init.ItemInit;
import com.cy4.betterdungeons.core.init.TileEntityTypesInit;
import com.cy4.betterdungeons.core.network.data.PlayerKeyCreationTableData;
import com.cy4.betterdungeons.core.util.math.VectorHelper;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class KeyCreationTableTileEntity extends TileEntity implements ITickableTileEntity {

	private HashMap<UUID, KeyCreationTableRecipe> nearbyPlayerRecipes = new HashMap<>();
	private boolean containsKey = false;
	private int infusionTimer = -1;

	private ItemStackHandler itemHandler = createHandler();
	private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

	public KeyCreationTableTileEntity() {
		super(TileEntityTypesInit.KEY_CREATION_TABLE_TILE_ENTITY_TYPE.get());
	}

	public void setContainsKey(boolean containsKey) {
		this.containsKey = containsKey;
	}

	public boolean containsKey() {
		return containsKey;
	}

	public int getInfusionTimer() {
		return infusionTimer;
	}

	public void sendUpdates() {
		this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
		this.world.notifyNeighborsOfStateChange(pos, this.getBlockState().getBlock());
		markDirty();
	}

	@Override
	public void tick() {
		World world = this.getWorld();
		if (world.isRemote)
			return;

		if (!containsKey) {
			if (!nearbyPlayerRecipes.isEmpty())
				nearbyPlayerRecipes.clear();
			return;
		}
		double x = this.getPos().getX() + 0.5d;
		double y = this.getPos().getY() + 0.5d;
		double z = this.getPos().getZ() + 0.5d;

		PlayerKeyCreationTableData data = PlayerKeyCreationTableData.get((ServerWorld) world);
		getNearbyPlayers(world, data, x, y, z, DungeonsConfig.KEY_CREATION_TABLE.PLAYER_RANGE_CHECK);
		pullNearbyItems(world, data, x, y, z, DungeonsConfig.KEY_CREATION_TABLE.ITEM_RANGE_CHECK);
		sendUpdates();

		if (infusionTimer > 0) {
			infusionTimer--;
		} else if (infusionTimer == 0) {
			completeInfusion(world);
			infusionTimer = -1;
		}

	}

	private void completeInfusion(World world) {
		this.containsKey = false;

		world.addEntity(new ItemEntity(world, getPos().getX() + .5d, pos.getY() + 1.5d, pos.getZ() + .5d,
				new ItemStack(ItemInit.DUNGEON_KEY.get())));

	}

	public void startInfusionTimer(int seconds) {
		System.out.println("Start Infusion");
		infusionTimer = seconds * 20;
	}

	private void getNearbyPlayers(World world, PlayerKeyCreationTableData data, double x, double y, double z,
			double range) {
		nearbyPlayerRecipes.clear();
		List<PlayerEntity> players = world.getEntitiesWithinAABB(PlayerEntity.class, getAABB(range, x, y, z));
		for (PlayerEntity p : players) {
			if (data.getRecipes().containsKey(p.getUniqueID())) {
				KeyCreationTableRecipe recipe = data.getRecipe(p.getUniqueID());
				nearbyPlayerRecipes.put(p.getUniqueID(), recipe);
			}
		}
	}

	private void pullNearbyItems(World world, PlayerKeyCreationTableData data, double x, double y, double z,
			double range) {

		float speed = DungeonsConfig.KEY_CREATION_TABLE.PULL_SPEED / 20f; // blocks per second

		List<ItemEntity> entities = world.getEntitiesWithinAABB(ItemEntity.class, getAABB(range, x, y, z));
		for (ItemEntity itemEntity : entities) {
			for (UUID id : nearbyPlayerRecipes.keySet()) {
				KeyCreationTableRecipe recipe = nearbyPlayerRecipes.get(id);
				List<RequiredItem> itemsToPull = recipe.getRequiredItems();
				if (itemsToPull == null)
					return;
				for (RequiredItem required : itemsToPull) {
					if (required.reachedAmountRequired()) {
						continue;
					}
					if (required.isItemEqual(itemEntity.getItem())) {
						int excess = required.getRemainder(itemEntity.getItem().getCount());
						moveItemTowardPedestal(itemEntity, speed);
						if (isItemInRange(itemEntity)) {
							if (excess > 0) {
								required.setCurrentAmount(required.getAmountRequired());
								itemEntity.getItem().setCount(excess);
							} else {
								required.addAmount(itemEntity.getItem().getCount());
								itemEntity.getItem().setCount(excess);
								itemEntity.remove();
							}
							data.update(recipe.getPlayer(), recipe);
						}
					}
				}
			}
		}
	}

	private void moveItemTowardPedestal(ItemEntity itemEntity, float speed) {
		Vector3d target = VectorHelper.getVectorFromPos(this.getPos());
		Vector3d current = VectorHelper.getVectorFromPos(itemEntity.getPosition());

		Vector3d velocity = VectorHelper.getMovementVelocity(current, target, speed);

		itemEntity.addVelocity(velocity.x, velocity.y, velocity.z);
	}

	private boolean isItemInRange(ItemEntity itemEntity) {
		BlockPos itemPos = itemEntity.getPosition();

		if (itemPos.distanceSq(getPos()) <= (2 * 2)) {
			return true;
		}
		return false;
	}

	public AxisAlignedBB getAABB(double range, double x, double y, double z) {
		return new AxisAlignedBB(x - range, y - range, z - range, x + range, y + range, z + range);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putBoolean("containsKey", containsKey);
		ListNBT playerList = new ListNBT();
		ListNBT recipeList = new ListNBT();

		this.nearbyPlayerRecipes.forEach((uuid, recipe) -> {
			playerList.add(StringNBT.valueOf(uuid.toString()));
			recipeList.add(KeyCreationTableRecipe.serialize(recipe));
		});

		compound.put("PlayerEntries", playerList);
		compound.put("AltarRecipeEntries", recipeList);
		return super.write(compound);
	}

	@Override
	public void read(BlockState state, CompoundNBT compound) {
		containsKey = compound.getBoolean("containsKey");
		ListNBT playerList = compound.getList("PlayerEntries", Constants.NBT.TAG_STRING);
		ListNBT recipeList = compound.getList("AltarRecipeEntries", Constants.NBT.TAG_COMPOUND);

		if (playerList.size() != recipeList.size()) {
			throw new IllegalStateException("Map doesn't have the same amount of keys as values");
		}

		for (int i = 0; i < playerList.size(); i++) {
			UUID playerUUID = UUID.fromString(playerList.getString(i));
			nearbyPlayerRecipes.put(playerUUID, KeyCreationTableRecipe.deserialize(recipeList.getCompound(i)));
		}
		super.read(state, compound);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		tag.putBoolean("containsKey", containsKey);
		ListNBT playerList = new ListNBT();
		ListNBT recipeList = new ListNBT();

		this.nearbyPlayerRecipes.forEach((uuid, recipe) -> {
			playerList.add(StringNBT.valueOf(uuid.toString()));
			recipeList.add(KeyCreationTableRecipe.serialize(recipe));
		});

		tag.put("PlayerEntries", playerList);
		tag.put("AltarRecipeEntries", recipeList);
		return tag;
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		read(state, tag);
	}

	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT tag = pkt.getNbtCompound();
		handleUpdateTag(getBlockState(), tag);
	}

	private ItemStackHandler createHandler() {
		return new ItemStackHandler(1) {

			@Override
			protected void onContentsChanged(int slot) {
				// To make sure the TE persists when the chunk is saved later we need to
				// mark it dirty every time the item handler changes
				sendUpdates();
			}

			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
				for (UUID id : nearbyPlayerRecipes.keySet()) {
					KeyCreationTableRecipe recipe = nearbyPlayerRecipes.get(id);
					List<RequiredItem> items = recipe.getRequiredItems();
					for (RequiredItem item : items) {
						if (item.isItemEqual(stack)) {
							return true;
						}
					}
				}
				return false;
			}

			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				for (UUID id : nearbyPlayerRecipes.keySet()) {
					KeyCreationTableRecipe recipe = nearbyPlayerRecipes.get(id);
					List<RequiredItem> items = recipe.getRequiredItems();
					for (RequiredItem item : items) {
						if (item.reachedAmountRequired()) {
							return stack;
						}
						if (item.isItemEqual(stack)) {
							int amount = stack.getCount();
							int excess = item.getRemainder(amount);
							if (excess > 0) {
								item.setCurrentAmount(item.getAmountRequired());
								stack.setCount(excess);
								return ItemHandlerHelper.copyStackWithSize(stack, excess);
							} else {
								item.addAmount(stack.getCount());
								return ItemStack.EMPTY;
							}
						}
					}
				}
				return stack;
			}
		};
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return handler.cast();
		}
		return super.getCapability(cap, side);
	}

	public HashMap<UUID, KeyCreationTableRecipe> getNearbyPlayerRecipes() {
		return nearbyPlayerRecipes;
	}
	
	
}