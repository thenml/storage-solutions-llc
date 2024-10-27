package net.nml.storagesolutions;

import java.util.HashMap;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.nml.storagesolutions.registers.RegisterBlockTypes;
import net.nml.storagesolutions.registers.RegisterOthers;
import net.nml.storagesolutions.renderers.MaterialChestBlockEntityRenderer;
import net.nml.storagesolutions.screenhandlers.DynamicSlotScreenHandler;

public class StorageSolutionsLLCClient implements ClientModInitializer {
	// TODO: reset when reloading textures
	public static HashMap<Identifier, BakedModel> MODIFIED_MODELS = new HashMap<>();

	@Override
	public void onInitializeClient() {
		// i have no idea why its like this, it worked fine without infering types
		HandledScreens.<DynamicSlotScreenHandler, CottonInventoryScreen<DynamicSlotScreenHandler>>register(
				RegisterOthers.DYNAMIC_SLOT_SCREEN_HANDLER_TYPE,
				CottonInventoryScreen<DynamicSlotScreenHandler>::new);
		BlockEntityRendererFactories.register(RegisterBlockTypes.MATERIAL_CHEST_BLOCK_ENTITY,
				MaterialChestBlockEntityRenderer::new);

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
				.registerReloadListener(new SimpleSynchronousResourceReloadListener() {
					@Override
					public Identifier getFabricId() {
						return new Identifier(StorageSolutionsLLC.MOD_ID, "modified_models");
					}

					@Override
					public void reload(ResourceManager manager) {
						MODIFIED_MODELS.clear();
					}
				});

		TierTextures.initialize();
	}
}