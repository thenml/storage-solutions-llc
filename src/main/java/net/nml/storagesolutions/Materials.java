package net.nml.storagesolutions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class Materials {
	private static HashMap<Identifier, Integer> materials = new HashMap<>();
	private static Set<String> matchStrings = Set.of("_block", "_ingot", "_chestplate", "_pickaxe");

	public static void initialize() {
		// time profiling
		long start = System.currentTimeMillis();

		for (Map.Entry<RegistryKey<Item>, Item> entry : Registries.ITEM.getEntrySet()) {
			Item item = entry.getValue();
			Identifier id = entry.getKey().getValue();

			if (isValidMaterial(entry.getKey(), item, id)) {
				StorageSolutionsLLC.LOGGER.info("Found: " + id);
			}
		}

		long end = System.currentTimeMillis();
		StorageSolutionsLLC.LOGGER.info("Time to find all materials: " + (end - start) + "ms");
		materials.forEach((k, v) -> {
			StorageSolutionsLLC.LOGGER.info(k + ": " + v);
		});
	}

	public static String getTier(int slotCount) {
		// TODO: rename the tiers?
		if (slotCount < 27)
			return "lite";
		if (slotCount < 40)
			return "basic";
		if (slotCount < 54)
			return "copper";
		if (slotCount < 71)
			return "iron";
		if (slotCount < 108)
			return "gold";
		if (slotCount < 162)
			return "diamond";
		if (slotCount < 216)
			return "sculk";
		if (slotCount < 324)
			return "netherite";
		if (slotCount < 432)
			return "legendary";
		if (slotCount < 648)
			return "mythic";
		if (slotCount < 1026)
			return "godlike";
		return "creative";
	}

	private static boolean isValidMaterial(RegistryKey<Item> key, Item item, Identifier id) {
		matchStrings.forEach((s) -> {
			if (id.toString().endsWith(s)) {
				Identifier material = new Identifier(id.toString().substring(0, id.toString().length() - s.length()));

				if (materials.containsKey(material))
					materials.put(material, materials.get(material) + 1);
				else
					materials.put(material, 1);
			}
		});
		return false;
	}
}
