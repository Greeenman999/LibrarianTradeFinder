package de.greenman999;

import de.greenman999.config.TradeFinderConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.LecternBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
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

    public static int placeDelay = 3;
    public static int interactDelay = 2;

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
        LibrarianTradeFinder.getConfig().mode = TradeFinderConfig.TradeMode.SINGLE;
        state = TradeState.CHECK;
    }

    public static void searchList() {
        maxBookPrice = 64;
        LibrarianTradeFinder.getConfig().mode = TradeFinderConfig.TradeMode.LIST;
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

        if((state == TradeState.CHECK || state == TradeState.WAITING_FOR_PACKET) && villager.getVillagerData().getProfession().equals(VillagerProfession.LIBRARIAN)) {
            Vec3d villagerPosition = new Vec3d(villager.getX(), villager.getY() + (double) villager.getEyeHeight(EntityPose.STANDING), villager.getZ());

            MinecraftClient.getInstance().player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, villagerPosition);
            /*if(interactDelay > 0) {
                interactDelay--;
                return;
            }
            interactDelay = 2;*/

            ActionResult result = MinecraftClient.getInstance().interactionManager.interactEntity(MinecraftClient.getInstance().player, villager, Hand.MAIN_HAND);
            if(result == ActionResult.SUCCESS) {
                state = TradeState.WAITING_FOR_PACKET;
            }else {
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("Failed to interact with villager. Try again.").styled(style -> style.withColor(TextColor.fromFormatting(Formatting.RED))));
                stop();
            }

        } else if(state == TradeState.BREAK) {
            BlockPos toPlace = lecternPos.down();
            MinecraftClient.getInstance().player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(toPlace.getX() + 0.5, toPlace.getY() + 1.0, toPlace.getZ() + 0.5));
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            PlayerInventory inventory = player.getInventory();
            ItemStack mainHand = inventory.getMainHandStack();
            if(mainHand.getItem() instanceof AxeItem) {
                int remainingDurability = mainHand.getMaxDamage() - mainHand.getDamage();
                if(remainingDurability <= 5 && LibrarianTradeFinder.getConfig().preventAxeBreaking) {
                    stop();
                    MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("The searching process was stopped because your axe is about to break.").formatted(Formatting.RED));
                    return;
                }
            }
            if(MinecraftClient.getInstance().world.getBlockState(lecternPos).getBlock() instanceof LecternBlock) {
                MinecraftClient.getInstance().player.swingHand(Hand.MAIN_HAND, true);
                MinecraftClient.getInstance().interactionManager.updateBlockBreakingProgress(lecternPos, Direction.UP);
                MinecraftClient.getInstance().player.networkHandler
                        .sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            }else {
                state = TradeState.PLACE;
            }

        } else if (state == TradeState.PLACE) {
            BlockPos toPlace = lecternPos.down();
            MinecraftClient.getInstance().player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(toPlace.getX() + 0.5, toPlace.getY() + 1.0, toPlace.getZ() + 0.5));

            /*if(placeDelay > 0) {
                placeDelay--;
                return;
            }
            placeDelay = 3;*/
            BlockHitResult hit = new BlockHitResult(new Vec3d(lecternPos.getX(), lecternPos.getY(),
                    lecternPos.getZ()), Direction.UP, lecternPos.down(), false);
            MinecraftClient.getInstance().interactionManager.interactBlock(MinecraftClient.getInstance().player, Hand.OFF_HAND, hit);

            state = TradeState.CHECK;
        }
    }
}
