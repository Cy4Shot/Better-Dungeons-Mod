package com.cy4.betterdungeons.common.te;

import java.util.List;

import javax.annotation.Nullable;

import com.cy4.betterdungeons.common.recipe.sapling.SaplingInfo;
import com.cy4.betterdungeons.common.recipe.soil.SoilInfo;
import com.cy4.betterdungeons.core.init.RecipesInit;
import com.cy4.betterdungeons.core.init.TileEntityTypesInit;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

//YOOOOINK! Stolen form Bonsai Pots Mod!
public class BonsaiPotTileEntity extends TileEntity implements ITickableTileEntity {
	private boolean initialized = false;

	public ItemStack soilStack = ItemStack.EMPTY;
	public ItemStack saplingStack = ItemStack.EMPTY;
	public int modelRotation = -1;
	public int growTicks;
	protected int requiredTicks;
	protected ResourceLocation treeId;
	public SaplingInfo saplingInfo;
	public SoilInfo soilInfo;

	public BonsaiPotTileEntity() {
		super(TileEntityTypesInit.BONSAI_POT_TILE_ENTITY_TYPE.get());
	}

	@SuppressWarnings("rawtypes")
	public BonsaiPotTileEntity(TileEntityType type) {
		super(type);
	}

	protected void updateInfoObjects() {
		this.saplingInfo = null;
		if (this.saplingStack != null && !this.saplingStack.isEmpty()) {
			this.saplingInfo = RecipesInit.saplingRecipeHelper.getSaplingInfoForItem(world, this.saplingStack);
			if (this.saplingInfo != null) { // This shouldn't happen, but does in case of Immersive Portals?!
				this.treeId = this.saplingInfo.getId();
			}
		}

		this.soilInfo = null;
		if (this.soilStack != null && !this.soilStack.isEmpty()) {
			this.soilInfo = RecipesInit.soilRecipeHelper.getSoilForItem(world, this.soilStack);
		}

		if (this.soilInfo != null && this.saplingInfo != null) {
			int ticks = this.saplingInfo.getRequiredTicks();
			float soilModifier = this.soilInfo.getTickModifier();

			this.requiredTicks = (int) Math.ceil(ticks * soilModifier);
		} else {
			this.requiredTicks = Integer.MAX_VALUE;
		}
	}

	public boolean hasSapling() {
		return saplingStack != null && !saplingStack.isEmpty();
	}

	public ItemStack getSaplingStack() {
		return saplingStack.copy();
	}

	public ResourceLocation getTreeId() {
		return treeId;
	}

	public void setSapling(ItemStack saplingStack) {
		this.saplingStack = saplingStack.copy();
		this.growTicks = 0;
		this.modelRotation = world.rand.nextInt(4);
		this.updateInfoObjects();
		this.markDirty();
	}

	public void dropSapling() {
		if (!this.hasSapling()) {
			return;
		}

		this.spawnItem(this.getSaplingStack());
		this.setSapling(ItemStack.EMPTY);
	}

	public boolean hasSoil() {
		return soilStack != null && !soilStack.isEmpty() && soilStack.getItem() instanceof BlockItem;
	}

	public ItemStack getSoilStack() {
		return soilStack.copy();
	}

	public BlockState getSoilBlockState() {
		if (!hasSoil()) {
			return null;
		}

		BlockItem soilBlock = (BlockItem) soilStack.getItem();
		return soilBlock.getBlock().getDefaultState();
	}

	public void setSoil(ItemStack soilStack) {
		this.soilStack = soilStack.copy();
		this.updateInfoObjects();
		this.markDirty();
		this.notifyClients();
	}

	public void dropSoil() {
		if (!this.hasSoil()) {
			return;
		}

		this.spawnItem(this.getSoilStack());
		this.setSoil(ItemStack.EMPTY);
	}

	public boolean isGrowing() {
		return hasSapling() && this.growTicks < this.requiredTicks;
	}

	public double getProgress() {
		if (this.getSaplingStack().isEmpty() || this.getSoilStack().isEmpty() || this.requiredTicks == 0) {
			return 0;
		}

		return (double) this.growTicks / (double) this.requiredTicks;
	}

	public double getProgress(float partialTicks) {
		if (this.getSaplingStack().isEmpty() || this.getSoilStack().isEmpty() || this.requiredTicks == 0) {
			return 0;
		}

		double result = ((double) this.growTicks + partialTicks) / (double) this.requiredTicks;
		if (result >= 0.999) {
			result = 1.0d;
		}
		return result;
	}

	public void updateGrowth() {
		if (this.getSaplingStack().isEmpty() || this.getSoilStack().isEmpty()) {
			this.setGrowTicks(0);
			return;
		}
		
		notifyClients();

		if (canGrowIntoBlockAbove()) {
			if (getProgress() < 1.0f) {
				this.setGrowTicks(growTicks + 1);
			}
		} else {
			if (getProgress() > 0.3f) {
				this.setGrowTicks((int) Math.ceil(this.requiredTicks * 0.3f));
			}
		}
	}

	private boolean canGrowIntoBlockAbove() {
		if (world == null) {
			return false;
		}

		BlockPos upPos = pos.up();
		if (world.isAirBlock(upPos)) {
			return true;
		}

		BlockState blockState = world.getBlockState(upPos);
		VoxelShape collisionShape = blockState.getCollisionShape(world, upPos);
		if (collisionShape == null || collisionShape.equals(VoxelShapes.empty())) {
			return true;
		}

		return false;
	}

	public void setGrowTicks(int growTicks) {
		this.growTicks = growTicks;
		this.markDirty();
	}

	public void boostProgress() {
		if (!isGrowing()) {
			return;
		}

		this.growTicks += this.requiredTicks / 4;
		if (this.growTicks >= this.requiredTicks) {
			this.growTicks = this.requiredTicks;
		}

		notifyClients();
	}

	protected void initialize() {
		if (world == null || this.world.isRemote) {
			return;
		}

		if (this.modelRotation == -1) {
			this.modelRotation = this.world.rand.nextInt(4);
		}
	}

	private void checkWaterlogged() {
		if (this.isWaterlogged()) {
			if (getProgress() >= 1.0f) {
				this.dropLoot();
				this.setSapling(this.saplingStack);
			}
		}
	}

	@SuppressWarnings("resource")
	@Override
	public void tick() {
		if (!this.getWorld().isRemote && !this.initialized) {
			initialize();
			this.initialized = true;
		}

		this.checkWaterlogged();
		this.updateGrowth();
	}

	public void dropLoot() {
		if (this.saplingInfo == null || this.soilInfo == null) {
			this.updateInfoObjects();
		}

		List<ItemStack> drops = this.saplingInfo.getRandomizedDrops(this.world.rand);
		for (ItemStack stack : drops) {
			this.spawnItem(stack);
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(this.getPos()).grow(1.0d).expand(0.0d, 1.0d, 0.0d);
	}

	protected void spawnItem(ItemStack stack) {
		ItemEntity entityItem = new ItemEntity(world, getPos().getX() + 0.5f, getPos().getY() + 0.7f, getPos().getZ() + 0.5f, stack);
		entityItem.lifespan = 1200;
		entityItem.setPickupDelay(5);

		entityItem.setMotion(0.0f, 0.10f, 0.0f);

		world.addEntity(entityItem);
	}

	public boolean isWaterlogged() {
		if (!this.world.getBlockState(this.getPos()).hasProperty(BlockStateProperties.WATERLOGGED)) {
			return false;
		}

		return this.world.getBlockState(this.getPos()).get(BlockStateProperties.WATERLOGGED);
	}

	public void notifyClients() {
		this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
		this.world.notifyNeighborsOfStateChange(pos, this.getBlockState().getBlock());
		markDirty();
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("modelRotation", modelRotation);
		compound.putInt("growTicks", growTicks);
		compound.putInt("requiredTicks", requiredTicks);
		compound.putString("treeId", treeId == null ? "" : treeId.toString());
		compound.put("soilStack", soilStack.serializeNBT());
		compound.put("saplingStack", saplingStack.serializeNBT());
		return super.write(compound);
	}

	@Override
	public void read(BlockState state, CompoundNBT compound) {
		modelRotation = compound.getInt("modelRotation");
		growTicks = compound.getInt("growTicks");
		requiredTicks = compound.getInt("requiredTicks");
		treeId = compound.getString("treeId") == "" ? null : new ResourceLocation(compound.getString("treeId"));
		soilStack = ItemStack.read(compound.getCompound("soilStack"));
		saplingStack = ItemStack.read(compound.getCompound("saplingStack"));
		super.read(state, compound);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		tag.putInt("modelRotation", modelRotation);
		tag.putInt("growTicks", growTicks);
		tag.putInt("requiredTicks", requiredTicks);
		tag.putString("treeId", treeId == null ? "" : treeId.toString());
		tag.put("soilStack", soilStack.serializeNBT());
		tag.put("saplingStack", saplingStack.serializeNBT());
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
}