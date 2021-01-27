package com.cy4.betterdungeons.common.upgrade.type;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.cy4.betterdungeons.common.upgrade.Restrictions;
import com.google.gson.annotations.Expose;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class Research extends PlayerUpgrade {

	@Expose
	protected Set<String> modIds;
	@Expose
	protected Restrictions restrictions;

	public Research(int cost, String... modIds) {
		super(cost);
		this.modIds = new HashSet<>();
		this.restrictions = Restrictions.forMods();

		Collections.addAll(this.modIds, modIds);
	}

	public Set<String> getModIds() {
		return modIds;
	}

	public Restrictions getRestrictions() {
		return restrictions;
	}

	public Research withRestrictions(boolean hittability, boolean entityIntr, boolean blockIntr, boolean usability, boolean craftability) {
		this.restrictions.set(Restrictions.Type.HITTABILITY, hittability);
		this.restrictions.set(Restrictions.Type.ENTITY_INTERACTABILITY, entityIntr);
		this.restrictions.set(Restrictions.Type.BLOCK_INTERACTABILITY, blockIntr);
		this.restrictions.set(Restrictions.Type.USABILITY, usability);
		this.restrictions.set(Restrictions.Type.CRAFTABILITY, craftability);
		return this;
	}

	public boolean restricts(Item item, Restrictions.Type restrictionType) {
		if (!this.restrictions.restricts(restrictionType))
			return false;
		ResourceLocation registryName = item.getRegistryName();
		if (registryName == null)
			return false;
		return modIds.contains(registryName.getNamespace());
	}

	public boolean restricts(Block block, Restrictions.Type restrictionType) {
		if (!this.restrictions.restricts(restrictionType))
			return false;
		ResourceLocation registryName = block.getRegistryName();
		if (registryName == null)
			return false;
		return modIds.contains(registryName.getNamespace());
	}

	public boolean restricts(EntityType<?> entityType, Restrictions.Type restrictionType) {
		if (!this.restrictions.restricts(restrictionType))
			return false;
		ResourceLocation registryName = entityType.getRegistryName();
		if (registryName == null)
			return false;
		return modIds.contains(registryName.getNamespace());
	}

}
