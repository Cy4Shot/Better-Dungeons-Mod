package com.cy4.betterdungeons.common.upgrade.type.ability;

import com.cy4.betterdungeons.core.util.math.MathUtils;
import com.google.gson.annotations.Expose;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

public class DashUpgrade extends PlayerAbility {

	@Expose
	private final int extraRadius;

	public DashUpgrade(int cost, int extraRadius) {
		super(cost, Behavior.RELEASE_TO_PERFORM, 200);
		this.extraRadius = extraRadius;
	}

	public int getExtraRadius() {
		return extraRadius;
	}

	@Override
	public void onAction(PlayerEntity player, boolean active) {
		Vector3d lookVector = player.getLookVec();
		double magnitude = (10 + extraRadius) * 0.15;
		double extraPitch = 10;
		Vector3d dashVector = new Vector3d(lookVector.getX(), lookVector.getY(), lookVector.getZ());
		float initialYaw = (float) MathUtils.extractYaw(dashVector);
		dashVector = MathUtils.rotateYaw(dashVector, initialYaw);
		double dashPitch = Math.toDegrees(MathUtils.extractPitch(dashVector));
		if (dashPitch + extraPitch > 90) {
			dashVector = new Vector3d(0, 1, 0);
			dashPitch = 90;
		} else {
			dashVector = MathUtils.rotateRoll(dashVector, (float) Math.toRadians(-extraPitch));
			dashVector = MathUtils.rotateYaw(dashVector, -initialYaw);
			dashVector = dashVector.normalize();
		}
		double coef = 1.6 - MathUtils.map(Math.abs(dashPitch), 0.0d, 90.0d, 0.6, 1.0d);
		dashVector = dashVector.scale(magnitude * coef);
		player.addVelocity(dashVector.getX(), dashVector.getY(), dashVector.getZ());
		player.velocityChanged = true;
	}

}