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
