package net.nml.storagesolutions.registers;

import java.util.Iterator;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.nml.storagesolutions.StorageSolutionsLLC;
import net.nml.storagesolutions.recipes.TieredBlockRecipe;
import net.nml.storagesolutions.screenhandlers.DynamicSlotScreenHandler;

public class RegisterOthers {
	public static final Identifier DYNAMIC_SLOT_SCREEN = new Identifier(StorageSolutionsLLC.MOD_ID,
			"dynamic_slot_screen");
	public static final ScreenHandlerType<DynamicSlotScreenHandler> DYNAMIC_SLOT_SCREEN_HANDLER_TYPE = Registry
			.register(Registries.SCREEN_HANDLER, new Identifier(StorageSolutionsLLC.MOD_ID, "dynamic_slot_screen_type"),
					new ExtendedScreenHandlerType<>(
							(syncId, playerInventory, buf) -> new DynamicSlotScreenHandler(syncId, playerInventory,
									buf.readInt(), ScreenHandlerContext.EMPTY)));

	public static final RecipeSerializer<TieredBlockRecipe> MATERIAL_CHEST_RECIPE = registerRecipe(
			new Identifier(StorageSolutionsLLC.MOD_ID, "material_chest").toString(),
			new SpecialRecipeSerializer<>((id, category) -> {
				return new TieredBlockRecipe(id, category, Blocks.CHEST,
						RegisterBlocks.MATERIAL_CHEST_BLOCK);
			}));

	public static void initialize() {
	}

	private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S registerRecipe(String id, S serializer) {
		return Registry.register(Registries.RECIPE_SERIALIZER, id, serializer);
	}

	private static void registerCommands() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher
				.register(CommandManager.literal("spawnitems")
						.executes(context -> {
							context.getSource().sendFeedback(() -> Text.literal("Spawned items"), false);
							Iterator<Item> items = Registries.ITEM.iterator();
							items.forEachRemaining(item -> context.getSource().getEntity().dropItem(item));
							return 1;
						})));
	}
}
