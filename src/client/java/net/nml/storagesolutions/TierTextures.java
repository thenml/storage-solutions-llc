package net.nml.storagesolutions;

import java.util.HashMap;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

public class TierTextures {
	private static HashMap<String, SpriteIdentifier> textures = new HashMap<>();

	private static void addTexture(String name) {
		textures.put(name, new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
				new Identifier(StorageSolutionsLLC.MOD_ID, name)));
	}

	public static SpriteIdentifier getChestTexture(int slots) {
		if (slots < 27)
			return textures.get("entity/chest/lite");
		if (slots < 40)
			return textures.get("entity/chest/basic");
		if (slots < 54)
			return textures.get("entity/chest/copper");
		if (slots < 71)
			return textures.get("entity/chest/iron");
		if (slots < 108)
			return textures.get("entity/chest/gold");
		if (slots < 162)
			return textures.get("entity/chest/diamond");
		if (slots < 216)
			return textures.get("entity/chest/sculk");
		if (slots < 324)
			return textures.get("entity/chest/netherite");
		if (slots < 432)
			return textures.get("entity/chest/legendary");
		if (slots < 648)
			return textures.get("entity/chest/mythic");
		if (slots < 1026)
			return textures.get("entity/chest/godlike");
		return textures.get("entity/chest/creative");
	}

	public static void initialize() {
		addTexture("entity/chest/lite");
		addTexture("entity/chest/basic");
		addTexture("entity/chest/copper");
		addTexture("entity/chest/iron");
		addTexture("entity/chest/gold");
		addTexture("entity/chest/diamond");
		addTexture("entity/chest/sculk");
		addTexture("entity/chest/netherite");
		addTexture("entity/chest/legendary");
		addTexture("entity/chest/mythic");
		addTexture("entity/chest/godlike");
		addTexture("entity/chest/creative");
	}
}
