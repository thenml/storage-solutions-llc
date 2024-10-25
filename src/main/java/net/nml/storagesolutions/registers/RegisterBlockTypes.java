package net.nml.storagesolutions.registers;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.nml.storagesolutions.StorageSolutionsLLC;
import net.nml.storagesolutions.chest.MaterialChestBlockEntity;

public class RegisterBlockTypes {

	public static final BlockEntityType<MaterialChestBlockEntity> MATERIAL_CHEST_BLOCK_ENTITY = register(
			"material_chest",
			FabricBlockEntityTypeBuilder.create(MaterialChestBlockEntity::new, RegisterBlocks.MATERIAL_CHEST_BLOCK)
					.build(null));

	public static <T extends BlockEntityType<?>> T register(String name, T blockEntityType) {
		return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(StorageSolutionsLLC.MOD_ID, name),
				blockEntityType);
	}

	public static void initialize() {
	}
}
