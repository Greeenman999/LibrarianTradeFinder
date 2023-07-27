package de.greenman999;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import de.greenman999.config.TradeFinderConfig;
import de.greenman999.screens.ControlUi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

@Environment(net.fabricmc.api.EnvType.CLIENT)
public class LibrarianTradeFinder implements ClientModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("librarian-trade-finder");
	private boolean openConfigScreen;

	private static KeyBinding selectKeyBinding;
	private static KeyBinding toggleKeyBinding;
	private static KeyBinding configKeyBinding;

	@Override
	public void onInitializeClient() {

		selectKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.librarian-trade-finder.select",
				GLFW.GLFW_KEY_I,
				"key.categories.librarian-trade-finder"
		));

		toggleKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.librarian-trade-finder.toggle",
				GLFW.GLFW_KEY_O,
				"key.categories.librarian-trade-finder"
		));

		configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.librarian-trade-finder.config",
				GLFW.GLFW_KEY_C,
				"key.categories.librarian-trade-finder"
		));

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
				dispatcher.register(literal("tradefinder")
						.then(literal("select").executes(context -> {
							return (TradeFinder.select() ? 1 : 0);
						}))
						.then(literal("search").executes(context -> {
							return TradeFinder.searchList();
						})
							.then(argument("enchantment", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.ENCHANTMENT)).executes(context -> {
								RegistryEntry<Enchantment> enchantmentRegistryEntry = context.getArgument("enchantment", RegistryEntry.class);
								Enchantment enchantment = enchantmentRegistryEntry.value();

								return TradeFinder.searchSingle(enchantment, 1, 64);
							})
								.then(argument("level", IntegerArgumentType.integer(1, 5)).executes(context -> {
									RegistryEntry<Enchantment> enchantmentRegistryEntry = context.getArgument("enchantment", RegistryEntry.class);
									Enchantment enchantment = enchantmentRegistryEntry.value();
									int level = IntegerArgumentType.getInteger(context, "level");

									return TradeFinder.searchSingle(enchantment, level, 64);
								})
									.then(argument("maxPrice", IntegerArgumentType.integer(1, 64)).executes(context -> {
										RegistryEntry<Enchantment> enchantmentRegistryEntry = context.getArgument("enchantment", RegistryEntry.class);
										Enchantment enchantment = enchantmentRegistryEntry.value();
										int level = IntegerArgumentType.getInteger(context, "level");
										int bookPrice = IntegerArgumentType.getInteger(context, "maxPrice");

										return TradeFinder.searchSingle(enchantment, level, bookPrice);
									})))
							)
						)
						.then(literal("config").executes(context -> {
							openConfigScreen = true;
							return 1;
						}))
						.then(literal("stop").executes(context -> {
							TradeFinder.stop();
							context.getSource().sendFeedback(Text.translatable("commands.tradefinder.stop.success").styled(style -> style.withColor(TextColor.fromFormatting(Formatting.GREEN))));
							return 1;
						}))
				));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			TradeFinder.tick();
			if(openConfigScreen) {
				openConfigScreen = false;
				client.setScreen(new ControlUi(client.currentScreen));
			}
			ClientPlayerEntity player = client.player;
			if(player == null) return;

			while (selectKeyBinding.wasPressed()) {
				TradeFinder.select();
			}

			while (toggleKeyBinding.wasPressed()) {
				if(TradeFinder.state == TradeState.IDLE) {
					TradeFinder.searchList();
				} else {
					TradeFinder.stop();
					player.sendMessage(Text.translatable("commands.tradefinder.stop.success").formatted(Formatting.GREEN), false);
				}
			}

			while (configKeyBinding.wasPressed()) {
				if(client.currentScreen == null) {
					client.setScreen(new ControlUi(null));
				}
			}
		});

		getConfig().load();

		LOGGER.info("Librarian Trade Finder initialized.");
	}

	public static TradeFinderConfig getConfig() {
		return TradeFinderConfig.INSTANCE;
	}

}