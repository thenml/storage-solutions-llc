package net.nml.storagesolutions;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.nml.storagesolutions.blocks.MaterialChestBlockEntityTypes;
import net.nml.storagesolutions.blocks.MaterialChests;
import net.nml.storagesolutions.screenhandlers.DynamicSlotScreenHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageSolutionsLLC implements ModInitializer {
	public static final String MOD_ID = "storage-solutions-llc";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Identifier DYNAMIC_SLOT_SCREEN = new Identifier(MOD_ID, "dynamic_slot_screen");

	public static final ScreenHandlerType<DynamicSlotScreenHandler> DYNAMIC_SLOT_SCREEN_HANDLER_TYPE = Registry
			.register(Registries.SCREEN_HANDLER, new Identifier(MOD_ID, "dynamic_slot_screen_type"),
					new ExtendedScreenHandlerType<>(
							(syncId, playerInventory, buf) -> new DynamicSlotScreenHandler(syncId, playerInventory,
									buf.readInt(), ScreenHandlerContext.EMPTY)));

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		MaterialChests.initialize();
		MaterialChestBlockEntityTypes.initialize();
		// Materials.initialize();
	}
}