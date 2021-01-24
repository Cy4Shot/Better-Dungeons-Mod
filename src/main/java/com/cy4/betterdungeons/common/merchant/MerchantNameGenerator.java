package com.cy4.betterdungeons.common.merchant;

import java.util.Random;

public class MerchantNameGenerator {

	public static final String[] names = { "'Arry", "Gertrude", "Sam", "Margaret", "Nayaa", "Bopp", "Bea", "Ciwifor", "Procraftitor",
			"Squidathor", "Niazo", "Baxin", "Pugminor", "Billwick", "Pencicama", "Apoctor", "Susibino", "Lexica", "Hermes", "Posidon",
			"Gennady", "Sven", "Aurora", "Diana", "Alice", "Bob", "Requiem", "Chaos", "The Dentist", "Brot", "Sid", "X", "Crow", "Zen",
			"Flux", "Slab", "Volta", "Calamis", "Shadow", "Ghost", "Novis", "Fax", "Rune", "Lumina", "Necros", "Flo", "Solaris", "Skye",
			"Glo", "Plasma", "Eras", "Bolt", "Bane", "Angel", "Light", "Chanlon", "Tensen", "Java", "Eclipse", "Lotus", "Squidarkel",
			"Imperion", "Giles", "Conway", "Rindle", "GUSTAF", "Zonnle", "Teole", "Oliesta", "Ugweeza", "Wondle", "Larie", "Treezle",
			"Quozle", "Driv", "Yantoo", "Restar", "Intost", "Mondle", "Numbeezle", "Sid", "Dave", "George", "Urus", "Totestones", "Helga",
			"Birtha", "Girdena", "Soldenae", "Arnemeno", "Proctor", "Elizabeth", "Abigail", "Rebecca" };

	public static String getName() {
		return names[new Random().nextInt(names.length)];
	}

}
