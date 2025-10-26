package de.greenman999.librariantradefinder;

import de.greenman999.librariantradefinder.config.TradeFinderConfig;
import de.greenman999.librariantradefinder.screens.ControlUi;
import de.greenman999.librariantradefinder.util.HudUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(net.fabricmc.api.EnvType.CLIENT)
public class LibrarianTradeFinder implements ClientModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("librarian-trade-finder");
	public static final KeyBinding.Category CATEGORY = new KeyBinding.Category(Identifier.of("librarian-trade-finder"));

	private static KeyBinding selectKeyBinding;
	private static KeyBinding toggleKeyBinding;
	private static KeyBinding configKeyBinding;

    private static boolean scheduleOpenConfig = false;

	@SuppressWarnings("unchecked")
	@Override
	public void onInitializeClient() {
		selectKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.librarian-trade-finder.select",
				GLFW.GLFW_KEY_I,
				CATEGORY
		));

		toggleKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.librarian-trade-finder.toggle",
				GLFW.GLFW_KEY_O,
				CATEGORY
		));

		configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.librarian-trade-finder.config",
				GLFW.GLFW_KEY_C,
				CATEGORY
		));

        Commands.registerCommands();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			TradeFinder.tick();
            if (scheduleOpenConfig) {
                client.setScreen(new ControlUi(client.currentScreen));
                scheduleOpenConfig = false;
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

		// TODO: Deprecated, move to mixin
		HudRenderCallback.EVENT.register((a, b) -> RotationTools.render());

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> getConfig().load());
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, manager, success) -> {
			if (MinecraftClient.getInstance().world != null) {
				getConfig().load();
			} else {
				LOGGER.warn("Data pack reload event received, but world is not loaded yet. Skipping config reload.");
			}
		});

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (hand == Hand.OFF_HAND || hitResult == null || !world.isClient())
                return ActionResult.PASS;
            if (hitResult.getEntity() instanceof VillagerEntity villager && villager.getVillagerData().profession().matchesKey(VillagerProfession.LIBRARIAN) && TradeFinder.state == TradeState.SELECT_MANUAL && TradeFinder.villager == null) {
                TradeFinder.villager = villager;
                HudUtils.chatMessageTranslatable("commands.tradefinder.select.librarian", Formatting.GREEN);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (hand == Hand.OFF_HAND || hitResult == null || !world.isClient())
                return ActionResult.PASS;
            BlockPos blockPos = hitResult.getBlockPos();
            if (world.getBlockState(blockPos).getBlock() == Blocks.LECTERN && TradeFinder.state == TradeState.SELECT_MANUAL && TradeFinder.lecternPos == null) {
                TradeFinder.lecternPos = blockPos;
                HudUtils.chatMessageTranslatable("commands.tradefinder.select.lectern", Formatting.GREEN);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        LOGGER.info("Librarian Trade Finder initialized.");
	}



    public static TradeFinderConfig getConfig() {
		return TradeFinderConfig.INSTANCE;
	}

    public static void openConfig(){
        scheduleOpenConfig = true;
    }

}