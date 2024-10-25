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
		return textures.get("entity/chest/" + Materials.getTier(slots));
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
