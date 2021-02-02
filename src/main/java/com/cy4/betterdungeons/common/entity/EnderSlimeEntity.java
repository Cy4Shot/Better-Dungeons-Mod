package com.cy4.betterdungeons.common.entity;

import java.util.Random;

import javax.annotation.Nullable;

import com.cy4.betterdungeons.common.entity.ai.AOEGoal;
import com.cy4.betterdungeons.common.entity.ai.RegenAI;
import com.cy4.betterdungeons.common.entity.ai.TeleportGoal;
import com.cy4.betterdungeons.common.entity.ai.TeleportRandomly;
import com.cy4.betterdungeons.common.world.spawner.EntityScaler;
import com.cy4.betterdungeons.core.network.stats.DungeonRun;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;

public class EnderSlimeEntity extends SlimeEntity implements IBoss {

	private static final DataParameter<Byte> SPELL = EntityDataManager.createKey(EnderSlimeEntity.class, DataSerializers.BYTE);
	protected int spellTicks;
	private SpellType activeSpell = SpellType.NONE;

	@SuppressWarnings("unchecked")
	public TeleportRandomly<SlimeEntity> teleportTask = new TeleportRandomly<>(this, (entity, source, amount) -> {
		if (!(source.getTrueSource() instanceof LivingEntity)) {
			return 0.2D;
		}
		return 0.0D;
	});

	public boolean shouldBlockSlimeSplit;
	public final ServerBossInfo bossInfo;
	public RegenAI<SlimeEntity> regenAfterAWhile;

	public EnderSlimeEntity(EntityType<? extends SlimeEntity> type, World worldIn) {
		super(type, worldIn);
		setSlimeSize(3, false);
		bossInfo = new ServerBossInfo(getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS);
		regenAfterAWhile = new RegenAI<>(this);
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		return super.isInvulnerableTo(source) || source.isProjectile();
	}

	@Override
	protected void dropLoot(DamageSource damageSource, boolean attackedRecently) {
	}

	@Override
	protected void registerData() {
		super.registerData();
		this.dataManager.register(SPELL, (byte) 0);
	}

	protected int getSpellTicks() {
		return this.spellTicks;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, TeleportGoal.builder(this).start(entity -> {
			return entity.getAttackTarget() != null && entity.ticksExisted % 60 == 0;
		}).to(entity -> {
			return entity.getAttackTarget().getPositionVec().add((entity.rand.nextDouble() - 0.5D) * 8.0D, entity.rand.nextInt(16) - 8,
					(entity.rand.nextDouble() - 0.5D) * 8.0D);
		}).build());

		this.goalSelector.addGoal(1, new AOEGoal<>(this, e -> !(e instanceof IBoss)));
		this.goalSelector.addGoal(1, new EnderSlimeEntity.AttackSpellGoal());

		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false));
		this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(100.0D);
	}

	@Override
	public void spawnInTheWorld(DungeonRun raid, ServerWorld world, BlockPos pos) {
		this.spawnInTheWorld(raid, world, pos, 3);
	}

	public void spawnInTheWorld(DungeonRun raid, ServerWorld world, BlockPos pos, int size) {
		this.setSlimeSize(size, false);
		this.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 0.2D, pos.getZ() + 0.5D, 0.0F, 0.0F);
		world.summonEntity(this);

		this.getTags().add("DungeonBoss");
		this.bossInfo.setVisible(true);

		if (raid != null) {
			EntityScaler.scaleVault(this, raid.level, new Random(), EntityScaler.Type.BOSS);
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (!(source.getTrueSource() instanceof PlayerEntity) && source != DamageSource.OUT_OF_WORLD) {
			return false;
		}

		if (this.isInvulnerableTo(source) || source == DamageSource.FALL) {
			return false;
		} else if (teleportTask.attackEntityFrom(source, amount)) {
			return true;
		}

		regenAfterAWhile.onDamageTaken();
		return super.attackEntityFrom(source, amount);
	}

	@Override
	protected void dealDamage(LivingEntity entityIn) {
		if (this.isAlive()) {
			int i = this.getSlimeSize();
			if (this.getDistanceSq(entityIn) < 0.8D * (double) i * 0.8D * (double) i && this.canEntityBeSeen(entityIn)
					&& entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), this.func_225512_er_())) {
				this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
				this.applyEnchantments(this, entityIn);
			}
		}
	}

	@Override
	public void remove(boolean keepData) {
		shouldBlockSlimeSplit = true;
		super.remove(keepData);
	}

	@Override
	public int getSlimeSize() {
		return shouldBlockSlimeSplit ? 0 : super.getSlimeSize();
	}

	@Override
	public ServerBossInfo getServerBossInfo() {
		return bossInfo;
	}

	@Override
	public void tick() {
		super.tick();

		if (!this.world.isRemote) {
			this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
			this.regenAfterAWhile.tick();
		}

		if (this.world.isRemote && this.isSpellcasting()) {
			SpellType spellcastingillagerentity$spelltype = this.getSpellType();
			double d0 = spellcastingillagerentity$spelltype.particleSpeed[0];
			double d1 = spellcastingillagerentity$spelltype.particleSpeed[1];
			double d2 = spellcastingillagerentity$spelltype.particleSpeed[2];
			float f = this.renderYawOffset * ((float) Math.PI / 180F) + MathHelper.cos((float) this.ticksExisted * 0.6662F) * 0.25F;
			float f1 = MathHelper.cos(f);
			float f2 = MathHelper.sin(f);
			this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.getPosX() + (double) f1 * 0.6D, this.getPosY() + 1.8D,
					this.getPosZ() + (double) f2 * 0.6D, d0, d1, d2);
			this.world.addParticle(ParticleTypes.ENTITY_EFFECT, this.getPosX() - (double) f1 * 0.6D, this.getPosY() + 1.8D,
					this.getPosZ() - (double) f2 * 0.6D, d0, d1, d2);
		}
	}

	@Override
	public void addTrackingPlayer(ServerPlayerEntity player) {
		super.addTrackingPlayer(player);
		this.bossInfo.addPlayer(player);
	}

	@Override
	public void removeTrackingPlayer(ServerPlayerEntity player) {
		super.removeTrackingPlayer(player);
		this.bossInfo.removePlayer(player);
	}

	public boolean isSpellcasting() {
		if (this.world.isRemote) {
			return this.dataManager.get(SPELL) > 0;
		} else {
			return this.spellTicks > 0;
		}
	}

	public void setSpellType(SpellType spellType) {
		this.activeSpell = spellType;
		this.dataManager.set(SPELL, (byte) spellType.id);
	}

	protected SpellType getSpellType() {
		return !this.world.isRemote ? this.activeSpell : SpellType.getFromId(this.dataManager.get(SPELL));
	}

	protected void updateAITasks() {
		super.updateAITasks();
		if (this.spellTicks > 0) {
			--this.spellTicks;
		}

	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.spellTicks = compound.getInt("SpellTicks");
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putInt("SpellTicks", this.spellTicks);
	}

	public SoundEvent getSpellSound() {
		return SoundEvents.ENTITY_ENDERMAN_TELEPORT;
	}

	class AttackSpellGoal extends EnderSlimeEntity.UseSpellGoal {
		private AttackSpellGoal() {
		}

		protected int getCastingTime() {
			return 40;
		}

		protected int getCastingInterval() {
			return 100;
		}

		protected void castSpell() {
			LivingEntity livingentity = EnderSlimeEntity.this.getAttackTarget();
			double d0 = Math.min(livingentity.getPosY(), EnderSlimeEntity.this.getPosY());
			double d1 = Math.max(livingentity.getPosY(), EnderSlimeEntity.this.getPosY()) + 1.0D;
			float f = (float) MathHelper.atan2(livingentity.getPosZ() - EnderSlimeEntity.this.getPosZ(),
					livingentity.getPosX() - EnderSlimeEntity.this.getPosX());
			if (EnderSlimeEntity.this.getDistanceSq(livingentity) < 9.0D) {
				for (int i = 0; i < 5; ++i) {
					float f1 = f + (float) i * (float) Math.PI * 0.4F;
					this.spawnFangs(EnderSlimeEntity.this.getPosX() + (double) MathHelper.cos(f1) * 1.5D,
							EnderSlimeEntity.this.getPosZ() + (double) MathHelper.sin(f1) * 1.5D, d0, d1, f1, 0);
				}

				for (int k = 0; k < 8; ++k) {
					float f2 = f + (float) k * (float) Math.PI * 2.0F / 8.0F + 1.2566371F;
					this.spawnFangs(EnderSlimeEntity.this.getPosX() + (double) MathHelper.cos(f2) * 2.5D,
							EnderSlimeEntity.this.getPosZ() + (double) MathHelper.sin(f2) * 2.5D, d0, d1, f2, 3);
				}
			} else {
				for (int l = 0; l < 16; ++l) {
					double d2 = 1.25D * (double) (l + 1);
					int j = 1 * l;
					this.spawnFangs(EnderSlimeEntity.this.getPosX() + (double) MathHelper.cos(f) * d2,
							EnderSlimeEntity.this.getPosZ() + (double) MathHelper.sin(f) * d2, d0, d1, f, j);
				}
			}

		}

		private void spawnFangs(double p_190876_1_, double p_190876_3_, double p_190876_5_, double p_190876_7_, float p_190876_9_,
				int p_190876_10_) {
			BlockPos blockpos = new BlockPos(p_190876_1_, p_190876_7_, p_190876_3_);
			boolean flag = false;
			double d0 = 0.0D;

			do {
				BlockPos blockpos1 = blockpos.down();
				BlockState blockstate = EnderSlimeEntity.this.world.getBlockState(blockpos1);
				if (blockstate.isSolidSide(EnderSlimeEntity.this.world, blockpos1, Direction.UP)) {
					if (!EnderSlimeEntity.this.world.isAirBlock(blockpos)) {
						BlockState blockstate1 = EnderSlimeEntity.this.world.getBlockState(blockpos);
						VoxelShape voxelshape = blockstate1.getCollisionShape(EnderSlimeEntity.this.world, blockpos);
						if (!voxelshape.isEmpty()) {
							d0 = voxelshape.getEnd(Direction.Axis.Y);
						}
					}

					flag = true;
					break;
				}

				blockpos = blockpos.down();
			} while (blockpos.getY() >= MathHelper.floor(p_190876_5_) - 1);

			if (flag) {
				EnderSlimeEntity.this.world.addEntity(new SlimeSpikesEntity(EnderSlimeEntity.this.world, p_190876_1_,
						(double) blockpos.getY() + d0, p_190876_3_, p_190876_9_, p_190876_10_, EnderSlimeEntity.this));
			}

		}

		protected SoundEvent getSpellPrepareSound() {
			return SoundEvents.ENTITY_ENDERMITE_HURT;
		}

		protected SpellType getSpellType() {
			return SpellType.FANGS;
		}
	}

	public abstract class UseSpellGoal extends Goal {
		protected int spellWarmup;
		protected int spellCooldown;

		protected UseSpellGoal() {
		}

		public boolean shouldExecute() {
			LivingEntity livingentity = EnderSlimeEntity.this.getAttackTarget();
			if (livingentity != null && livingentity.isAlive()) {
				if (EnderSlimeEntity.this.isSpellcasting()) {
					return false;
				} else {
					return EnderSlimeEntity.this.ticksExisted >= this.spellCooldown;
				}
			} else {
				return false;
			}
		}

		public boolean shouldContinueExecuting() {
			LivingEntity livingentity = EnderSlimeEntity.this.getAttackTarget();
			return livingentity != null && livingentity.isAlive() && this.spellWarmup > 0;
		}

		public void startExecuting() {
			this.spellWarmup = this.getCastWarmupTime();
			EnderSlimeEntity.this.spellTicks = this.getCastingTime();
			this.spellCooldown = EnderSlimeEntity.this.ticksExisted + this.getCastingInterval();
			SoundEvent soundevent = this.getSpellPrepareSound();
			if (soundevent != null) {
				EnderSlimeEntity.this.playSound(soundevent, 1.0F, 1.0F);
			}

			EnderSlimeEntity.this.setSpellType(this.getSpellType());
		}

		public void tick() {
			--this.spellWarmup;
			if (this.spellWarmup == 0) {
				this.castSpell();
				EnderSlimeEntity.this.playSound(EnderSlimeEntity.this.getSpellSound(), 1.0F, 1.0F);
			}

		}

		protected abstract void castSpell();

		protected int getCastWarmupTime() {
			return 20;
		}

		protected abstract int getCastingTime();

		protected abstract int getCastingInterval();

		@Nullable
		protected abstract SoundEvent getSpellPrepareSound();

		protected abstract SpellType getSpellType();
	}

	public static enum SpellType {
		NONE(0, 0.0D, 0.0D, 0.0D), SUMMON_VEX(1, 0.7D, 0.7D, 0.8D), FANGS(2, 0.4D, 0.3D, 0.35D), WOLOLO(3, 0.7D, 0.5D, 0.2D),
		DISAPPEAR(4, 0.3D, 0.3D, 0.8D), BLINDNESS(5, 0.1D, 0.1D, 0.2D);

		private final int id;
		private final double[] particleSpeed;

		private SpellType(int idIn, double xParticleSpeed, double yParticleSpeed, double zParticleSpeed) {
			this.id = idIn;
			this.particleSpeed = new double[] { xParticleSpeed, yParticleSpeed, zParticleSpeed };
		}

		public static SpellType getFromId(int idIn) {
			for (SpellType spellcastingillagerentity$spelltype : values()) {
				if (idIn == spellcastingillagerentity$spelltype.id) {
					return spellcastingillagerentity$spelltype;
				}
			}

			return NONE;
		}
	}

}