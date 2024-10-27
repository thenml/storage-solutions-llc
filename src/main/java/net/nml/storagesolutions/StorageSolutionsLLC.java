package net.nml.storagesolutions;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.nml.storagesolutions.registers.RegisterBlockTypes;
import net.nml.storagesolutions.registers.RegisterBlocks;
import net.nml.storagesolutions.registers.RegisterOthers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageSolutionsLLC implements ModInitializer {
	public static final String MOD_ID = "storage-solutions-llc";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		RegisterBlocks.initialize();
		RegisterBlockTypes.initialize();
		RegisterOthers.initialize();

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new Materials());
	}
}