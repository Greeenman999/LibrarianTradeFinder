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
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.level.block.Blocks;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(net.fabricmc.api.EnvType.CLIENT)
public class LibrarianTradeFinder implements ClientModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("librarian-trade-finder");
	public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(Identifier.parse("librarian-trade-finder"));

	private static KeyMapping selectKeyBinding;
	private static KeyMapping toggleKeyBinding;
	private static KeyMapping configKeyBinding;

    private static boolean scheduleOpenConfig = false;

	@SuppressWarnings("unchecked")
	@Override
	public void onInitializeClient() {
		selectKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.librarian-trade-finder.select",
				GLFW.GLFW_KEY_I,
				CATEGORY
		));

		toggleKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.librarian-trade-finder.toggle",
				GLFW.GLFW_KEY_O,
				CATEGORY
		));

		configKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.librarian-trade-finder.config",
				GLFW.GLFW_KEY_C,
				CATEGORY
		));

        Commands.registerCommands();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			TradeFinder.tick();
            if (scheduleOpenConfig) {
                client.setScreen(new ControlUi(client.screen));
                scheduleOpenConfig = false;
            }
			LocalPlayer player = client.player;
			if(player == null) return;

			while (selectKeyBinding.consumeClick()) {
				TradeFinder.select();
			}

			while (toggleKeyBinding.consumeClick()) {
				if(TradeFinder.state == TradeState.IDLE) {
					TradeFinder.searchList();
				} else {
					TradeFinder.stop();
					player.displayClientMessage(Component.translatable("commands.tradefinder.stop.success").withStyle(ChatFormatting.GREEN), false);
				}
			}

			while (configKeyBinding.consumeClick()) {
				if(client.screen == null) {
					client.setScreen(new ControlUi(null));
				}
			}
		});

		// TODO: Deprecated, move to mixin
		HudRenderCallback.EVENT.register((a, b) -> RotationTools.render());

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> getConfig().load());
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, manager, success) -> {
			if (Minecraft.getInstance().level != null) {
				getConfig().load();
			} else {
				LOGGER.warn("Data pack reload event received, but world is not loaded yet. Skipping config reload.");
			}
		});

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (hand == InteractionHand.OFF_HAND || hitResult == null || !world.isClientSide())
                return InteractionResult.PASS;
            if (hitResult.getEntity() instanceof Villager villager && villager.getVillagerData().profession().is(VillagerProfession.LIBRARIAN) && TradeFinder.state == TradeState.SELECT_MANUAL && TradeFinder.villager == null) {
                TradeFinder.villager = villager;
                HudUtils.chatMessage(HudUtils.textTranslatable(ChatFormatting.GREEN, "commands.tradefinder.select.librarian"));
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (hand == InteractionHand.OFF_HAND || hitResult == null || !world.isClientSide())
                return InteractionResult.PASS;
            BlockPos blockPos = hitResult.getBlockPos();
            if (world.getBlockState(blockPos).getBlock() == Blocks.LECTERN && TradeFinder.state == TradeState.SELECT_MANUAL && TradeFinder.lecternPos == null) {
                TradeFinder.lecternPos = blockPos;
                HudUtils.chatMessage(HudUtils.textTranslatable(ChatFormatting.GREEN, "commands.tradefinder.select.lectern"));
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
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