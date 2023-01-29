package de.greenman999;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.LecternBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillagerProfession;

public class TradeFinder {

    public static TradeState state = TradeState.IDLE;
    public static VillagerEntity villager = null;
    public static BlockPos lecternPos = null;
    public static Enchantment enchantment = null;
    public static int maxBookPrice = 0;

    public static int tries = 0;

    public static void stop() {
        state = TradeState.IDLE;
        villager = null;
        lecternPos = null;
        enchantment = null;
        maxBookPrice = 0;
        tries = 0;
    }

    public static void search(Enchantment enchantment, int bookPrice) {
        TradeFinder.enchantment = enchantment;
        TradeFinder.maxBookPrice = bookPrice;
        state = TradeState.CHECK;
    }

    public static void select(VillagerEntity villagerEntity, BlockPos blockPos) {
        villager = villagerEntity;
        lecternPos = blockPos;
    }

    public static void tick() {
        if(state == TradeState.IDLE) return;
        switch (state) {
            case CHECK -> MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.literal("checking trade --- attempt: " + tries).formatted(Formatting.GRAY), false);
            case BREAK -> MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.literal("breaking lectern --- attempt: " + tries).formatted(Formatting.GRAY), false);
            case PLACE -> MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.literal("placing lectern --- attempt: " + tries).formatted(Formatting.GRAY), false);
        }

        if(state == TradeState.CHECK && villager.getVillagerData().getProfession().equals(VillagerProfession.LIBRARIAN)) {
            ActionResult result = MinecraftClient.getInstance().interactionManager.interactEntity(MinecraftClient.getInstance().player, villager, Hand.MAIN_HAND);
            System.out.println(result);
            if(result == ActionResult.SUCCESS) {
                state = TradeState.WAITING_FOR_PACKET;
            }else {
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("Failed to interact with villager. Try again.").styled(style -> style.withColor(TextColor.fromFormatting(Formatting.RED))));
                stop();
            }

        }else if(state == TradeState.BREAK) {
            if(MinecraftClient.getInstance().world.getBlockState(lecternPos).getBlock() instanceof LecternBlock) {
                MinecraftClient.getInstance().player.swingHand(Hand.MAIN_HAND, true);
                MinecraftClient.getInstance().interactionManager.updateBlockBreakingProgress(lecternPos, Direction.UP);
                MinecraftClient.getInstance().player.networkHandler
                        .sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            }else {
                state = TradeState.PLACE;
            }

        } else if (state == TradeState.PLACE) {
            BlockHitResult hit = new BlockHitResult(new Vec3d(lecternPos.getX(), lecternPos.getY(),
                    lecternPos.getZ()), Direction.UP, lecternPos, false);
            MinecraftClient.getInstance().interactionManager.interactBlock(MinecraftClient.getInstance().player,Hand.OFF_HAND, hit);
            state = TradeState.CHECK;
        }
    }
}
