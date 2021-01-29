package com.cy4.betterdungeons.common.upgrade.type.ability;

import com.cy4.betterdungeons.common.upgrade.UpgradeNode;
import com.cy4.betterdungeons.common.upgrade.UpgradeTree;
import com.cy4.betterdungeons.core.network.data.PlayerUpgradeData;
import com.google.gson.annotations.Expose;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion;
import net.minecraft.world.Explosion.Mode;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GroundSlamUpgrade extends PlayerAbility {

	@Expose
	private final int strength;

	public GroundSlamUpgrade(int cost, int strength, int cooldown) {
		super(cost, Behavior.RELEASE_TO_PERFORM, cooldown * 20);
		this.strength = strength;
	}

	public int getStrength() {
		return strength;
	}

	@Override
	public void onAction(PlayerEntity player, boolean active) {
		player.addVelocity(0, (10 + strength * 0.15) / 4f, 0);
		player.velocityChanged = true;
	}

	@SubscribeEvent
	public static void onLivingDamaged(LivingDamageEvent event) {
		if (event.getEntityLiving() instanceof PlayerEntity) {
			if (event.getSource().equals(DamageSource.FALL)) {
				if (!event.getEntityLiving().getEntityWorld().isRemote()) {
					ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
					UpgradeTree abilityTree = PlayerUpgradeData.get(player.getServerWorld()).getUpgrades(player);

					System.out.println("YO!!! my ability tree be like " + abilityTree.getPlayerID() + " and my player go " + player.getUniqueID()); 

					if (!abilityTree.isActive())
						return;

					System.out.println("YO!!! my ability tree do be kinda active.  thats pretty cool i guess.");

					UpgradeNode<?> focusedAbilityNode = abilityTree.getFocusedUpgrade();

					System.out.println("YO!!! my node be like " + focusedAbilityNode.getGroup().getParentName());

					if (focusedAbilityNode != null) {
						PlayerAbility focusedAbility = (PlayerAbility) focusedAbilityNode.getUpgrade();

						System.out.println("YO!!! my ability be like costing" + focusedAbility.getCost());

						if (focusedAbility instanceof GroundSlamUpgrade) {
							Explosion e = new Explosion(player.getEntityWorld(), player, new DamageSource("ground_slam").setExplosion(),
									null, event.getEntityLiving().getPosition().getX(), event.getEntityLiving().getPosition().getY(),
									event.getEntityLiving().getPosition().getZ(), 5 + ((GroundSlamUpgrade) focusedAbility).getStrength(),
									false, Mode.NONE);
							e.doExplosionA();
							e.doExplosionB(true);
						}
					}
				}
			}
			if (event.getSource().getDamageType().equals("ground_slam")) {
				event.setCanceled(true);
			}
		}
	}
}
