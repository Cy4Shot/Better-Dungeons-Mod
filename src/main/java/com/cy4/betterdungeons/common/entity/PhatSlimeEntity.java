package com.cy4.betterdungeons.common.entity;

import java.util.Random;

import com.cy4.betterdungeons.common.entity.ai.RegenAI;
import com.cy4.betterdungeons.common.world.spawner.EntityScaler;
import com.cy4.betterdungeons.core.network.stats.DungeonRun;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;

public class PhatSlimeEntity extends SlimeEntity implements IBoss {

	public boolean shouldBlockSlimeSplit;
	public final ServerBossInfo bossInfo;
	public RegenAI<PhatSlimeEntity> regenAfterAWhile;

	public PhatSlimeEntity(EntityType<? extends SlimeEntity> type, World worldIn) {
		super(type, worldIn);
		setSlimeSize(3, false);
		bossInfo = new ServerBossInfo(getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS);
		regenAfterAWhile = new RegenAI<>(this);
	}

	@Override
	protected void dropLoot(DamageSource damageSource, boolean attackedRecently) {
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
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

		this.getTags().add("VaultBoss");
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

}