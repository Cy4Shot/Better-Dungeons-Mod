package com.cy4.betterdungeons.common.upgrade.type;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.annotations.Expose;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class EffectUpgrade extends PlayerUpgrade {

	@Expose
	private final String effect;
	@Expose
	private final int amplifier;
	@Expose
	private final String type;

	@SuppressWarnings("deprecation")
	public EffectUpgrade(Effect effect, int amplifier, Type type) {
		this(Registry.EFFECTS.getKey(effect).toString(), amplifier, type.toString());
	}

	public EffectUpgrade(String effect, int amplifier, String type) {
		super();
		this.effect = effect;
		this.amplifier = amplifier;
		this.type = type;
	}

	@SuppressWarnings("deprecation")
	public Effect getEffect() {
		return Registry.EFFECTS.getOrDefault(new ResourceLocation(this.effect));
	}

	public int getAmplifier() {
		return this.amplifier;
	}

	public Type getType() {
		return Type.fromString(this.type);
	}

	@Override
	public void tick(PlayerEntity player) {
		EffectInstance activeEffect = player.getActivePotionEffect(this.getEffect());

		EffectInstance newEffect = new EffectInstance(this.getEffect(), Integer.MAX_VALUE, this.getAmplifier(), false,
				this.getType().showParticles, this.getType().showIcon);

		if (activeEffect == null) {
			player.addPotionEffect(newEffect);
		}
	}

	@Override
	public void onRemoved(PlayerEntity player) {
		player.removePotionEffect(this.getEffect());
	}

	public enum Type {
		HIDDEN("hidden", false, false), PARTICLES_ONLY("particles_only", true, false), ICON_ONLY("icon_only", false, true),
		ALL("all", true, true);

		private static Map<String, Type> STRING_TO_TYPE = Arrays.stream(values()).collect(Collectors.toMap(Type::toString, o -> o));

		private final String name;
		public final boolean showParticles;
		public final boolean showIcon;

		Type(String name, boolean showParticles, boolean showIcon) {
			this.name = name;
			this.showParticles = showParticles;
			this.showIcon = showIcon;
		}

		public static Type fromString(String type) {
			return STRING_TO_TYPE.get(type);
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

}