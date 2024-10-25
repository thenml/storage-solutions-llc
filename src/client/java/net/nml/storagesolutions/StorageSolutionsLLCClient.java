package net.nml.storagesolutions;

import java.util.HashMap;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.Identifier;
import net.nml.storagesolutions.blocks.MaterialChestBlockEntityTypes;
import net.nml.storagesolutions.renderers.MaterialChestBlockEntityRenderer;

public class StorageSolutionsLLCClient implements ClientModInitializer {
	// TODO: reset when reloading textures
	public static HashMap<Identifier, BakedModel> MODIFIED_MODELS = new HashMap<>();

	@Override
	public void onInitializeClient() {
		HandledScreens.register(StorageSolutionsLLC.DYNAMIC_SLOT_SCREEN_HANDLER_TYPE, CottonInventoryScreen::new);
		BlockEntityRendererFactories.register(MaterialChestBlockEntityTypes.MATERIAL_CHEST_BLOCK_ENTITY,
				MaterialChestBlockEntityRenderer::new);

		TierTextures.initialize();
	}
}