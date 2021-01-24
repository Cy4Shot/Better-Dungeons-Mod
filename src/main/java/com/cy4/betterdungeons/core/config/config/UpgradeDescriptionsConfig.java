package com.cy4.betterdungeons.core.config.config;

import java.util.HashMap;

import com.cy4.betterdungeons.core.config.Config;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

public class UpgradeDescriptionsConfig extends Config {

	@Expose
	private HashMap<String, JsonElement> descriptions;

	@Override
	public String getName() {
		return "upgrade_descriptions";
	}

	public IFormattableTextComponent getDescriptionFor(String skillName) {
		JsonElement element = descriptions.get(skillName);
		if (element == null) {
			return StringTextComponent.Serializer.getComponentFromJsonLenient("[" + "{text:'No description for ', color:'#192022'},"
					+ "{text: '" + skillName + "', color: '#fcf5c5'}," + "{text: ', yet', color: '#192022'}" + "]");
		}
		return StringTextComponent.Serializer.getComponentFromJson(element);
	}

	@Override
	protected void reset() {
		this.descriptions = new HashMap<>();
	}
}