package net.nml.storagesolutions;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class Materials implements SimpleSynchronousResourceReloadListener {
	private static final Gson GSON = new Gson();
	public static final HashMap<Block, Integer> blocks = new HashMap<>();

	public static String getTier(int slotCount) {
		// TODO: make the tiers data driven
		if (slotCount < 27)
			return "lite";
		if (slotCount < 40)
			return "basic";
		if (slotCount < 52)
			return "copper";
		if (slotCount < 70)
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

	private static void readOverrides(ResourceManager manager) {
		Identifier overridesId = new Identifier(StorageSolutionsLLC.MOD_ID, "material_overrides.json");

		try {
			// Try to get the resource for overrides.json
			for (Resource resource : manager.getAllResources(overridesId)) {
				// Parse the JSON file
				try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(),
						StandardCharsets.UTF_8)) {
					JsonObject jsonObject = GSON.fromJson(reader, JsonObject.class);
					jsonObject.entrySet().forEach(entry -> {
						try {
							Block block = Registries.BLOCK.get(new Identifier(entry.getKey()));
							int slotCount = Utils.floorSlots(entry.getValue().getAsInt());
							if (block == Blocks.AIR) {
								StorageSolutionsLLC.LOGGER.warn("Could not find block " + entry.getKey());
								return;
							}
							blocks.put(block, slotCount);
						} catch (Exception e) {
							StorageSolutionsLLC.LOGGER.warn("Invalid entry in overrides.json: {}", entry, e);
						}
					});
				}
			}
		} catch (Exception e) {
			StorageSolutionsLLC.LOGGER.error("Failed to load overrides.json", e);
		}
	}

	private static void findBlocks() {
		HashSet<ToolMaterial> checkedMaterial = new HashSet<>();
		HashSet<Identifier> checkedItem = new HashSet<>();

		for (Map.Entry<RegistryKey<Item>, Item> entry : Registries.ITEM.getEntrySet()) {
			// TODO: instead of searching for tool materials, use some other method
			if (entry.getValue() instanceof ToolItem tool) {
				ToolMaterial material = tool.getMaterial();
				if (checkedMaterial.contains(material))
					continue;
				checkedMaterial.add(material);
				for (ItemStack stack : material.getRepairIngredient().getMatchingStacks()) {
					Identifier itemId = Registries.ITEM.getId(stack.getItem());
					if (checkedItem.contains(itemId))
						continue;
					checkedItem.add(itemId);
					Identifier blockId = new Identifier(itemId.getNamespace(), itemId.getPath() + "_block");

					Block correspondingBlock = Registries.BLOCK.get(blockId);

					if (correspondingBlock == Blocks.AIR) {
						// If direct match doesn't exist, try removing the last word and adding "_block"
						String[] parts = itemId.getPath().split("_");
						if (parts.length > 1) {
							// Build the base name without the last part
							StringBuilder baseNameBuilder = new StringBuilder();
							for (int i = 0; i < parts.length - 1; i++) {
								if (i > 0)
									baseNameBuilder.append("_");
								baseNameBuilder.append(parts[i]);
							}
							baseNameBuilder.append("_block");

							// Create the new block ID with the modified name
							blockId = new Identifier(itemId.getNamespace(), baseNameBuilder.toString());
							correspondingBlock = Registries.BLOCK.get(blockId);
						}
					}

					if (correspondingBlock == Blocks.AIR) {
						StorageSolutionsLLC.LOGGER.error("Could not find corresponding block for " + itemId);
					} else {
						if (blocks.containsKey(correspondingBlock))
							continue;
						int slotCount = MathHelper.floor(material.getDurability() / 13.5f);
						blocks.put(correspondingBlock, Utils.floorSlots(slotCount));
						// StorageSolutionsLLC.LOGGER.info("Found " + correspondingBlock + " with " +
						// slotCount + " slots");
					}
				}
			}
		}
		StorageSolutionsLLC.LOGGER.info("Found " + blocks.size() + " blocks");
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier(StorageSolutionsLLC.MOD_ID, "materials");
	}

	@Override
	public void reload(ResourceManager manager) {
		blocks.clear();
		readOverrides(manager);
		findBlocks();
	}
}
