package de.greenman999.librariantradefinder;

import de.greenman999.librariantradefinder.config.TradeFinderConfig;
import de.greenman999.librariantradefinder.util.HudUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;


@SuppressWarnings("DuplicatedCode")
public class TradeFinder {

    public static TradeState state = TradeState.IDLE;
    public static Villager villager = null;
    public static BlockPos lecternPos = null;

    public static boolean searchAll = true;

    // When searching a single enchantment
    public static Enchantment enchantment = null;
    public static int maxBookPrice = 0;
    public static int minLevel = 0;

    public static int tries = 0;

    private static Vec3 prevPos = null;

    private static int placeDelay = 3;
    private static int interactDelay = 2;

    private static boolean startedBreakLook = false;
    private static boolean startedPlaceLook = false;
    private static boolean startedCheckLook = false;
    private static boolean finishedBreakLook = false;
    private static boolean finishedPlaceLook = false;
    private static boolean finishedCheckLook = false;

    public static void stop() {
        state = TradeState.IDLE;

        enchantment = null;
        maxBookPrice = 0;
        minLevel = 0;
        tries = 0;

        Minecraft.getInstance().gui.setOverlayMessage(Component.literal(""), false);
    }

    public static int searchList() {
        if(TradeFinderConfig.INSTANCE.enchantments.values().stream().noneMatch(e -> e.enabled)) {
            HudUtils.chatMessage(HudUtils.textTranslatable(ChatFormatting.RED, "commands.tradefinder.search.no-enchantments"));
            return 0;
        }
        searchAll = true;
        state = TradeState.CHECK;
        if(TradeFinder.villager == null || TradeFinder.lecternPos == null) {
            HudUtils.chatMessage(HudUtils.textTranslatable(ChatFormatting.RED, "commands.tradefinder.start.not-selected"));
            stop();
            return 0;
        }
        HudUtils.chatMessage(HudUtils.textTranslatable(ChatFormatting.GREEN, "commands.tradefinder.start.success-list"));
        tries = 0;
        return 1;
    }

    public static int searchSingle(Enchantment enchantment, int minLevel, int maxBookPrice) {
        TradeFinder.enchantment = enchantment;
        TradeFinder.minLevel = Math.min(minLevel, enchantment.getMaxLevel());
        TradeFinder.maxBookPrice = maxBookPrice;

        searchAll = false;
        state = TradeState.CHECK;
        tries = 0;

		if(TradeFinder.villager == null || TradeFinder.lecternPos == null) {
            HudUtils.chatMessage(HudUtils.textTranslatable(ChatFormatting.RED, "commands.tradefinder.start.not-selected"));
            stop();
            return 0;
        }
        HudUtils.chatMessage(HudUtils.textTranslatable(ChatFormatting.GREEN, "commands.tradefinder.start.success-single"));
        return 1;
    }

    public static boolean select() {
        Minecraft mc = Minecraft.getInstance();
        HitResult hitResult = null;
        if (Minecraft.getInstance().player != null) {
            hitResult = Minecraft.getInstance().player.pick(3.0, 0.0F, false);
        }
        if (hitResult != null && (!(hitResult.getType().equals(HitResult.Type.BLOCK)) || hitResult.getType().equals(HitResult.Type.ENTITY))) {
            HudUtils.chatMessage(HudUtils.textTranslatable(ChatFormatting.RED, "commands.tradefinder.select.not-looking-at-lectern"));
            return false;
        }
        BlockPos blockPos = null;
        if (hitResult != null) {
            blockPos = ((BlockHitResult) hitResult).getBlockPos();
        }
        Block block = null;
        if (Minecraft.getInstance().level != null) {
            block = Minecraft.getInstance().level.getBlockState(blockPos).getBlock();
        }
        if(!(block instanceof LecternBlock)) {
            HudUtils.chatMessage(HudUtils.textTranslatable(ChatFormatting.RED, "commands.tradefinder.select.not-looking-at-lectern"));
            return false;
        }

        double closestDistance = Double.POSITIVE_INFINITY;
        Entity closestEntity = null;

        assert Minecraft.getInstance().level != null;
        for(Entity entity : Minecraft.getInstance().level.entitiesForRendering()) {
            Vec3 entityPos = entity.position();
            if (blockPos != null && entity instanceof Villager && ((Villager) entity).getVillagerData().profession().is(VillagerProfession.LIBRARIAN) && entityPos.distanceTo(blockPos.getCenter()) < closestDistance) {
                closestDistance = entityPos.distanceTo(blockPos.getCenter());
                closestEntity = entity;
            }
        }

        Villager foundVillager = (Villager) closestEntity;
        if(foundVillager == null) {
            HudUtils.chatMessage(HudUtils.textTranslatable(ChatFormatting.RED, "commands.tradefinder.select.no-librarian-found"));
            return false;
        }

        villager = foundVillager;
        lecternPos = blockPos;

        HudUtils.chatMessage(HudUtils.textTranslatable(ChatFormatting.GREEN, "commands.tradefinder.select.success"));
        return true;
    }

    public static void selectManual() {
        villager = null;
        lecternPos = null;
        state = TradeState.SELECT_MANUAL;
    }

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();

        if(state == TradeState.IDLE) return;
        LocalPlayer player = mc.player;
        if(player == null) return;

        if((TradeFinder.villager == null || TradeFinder.lecternPos == null) && state != TradeState.SELECT_MANUAL) {
            HudUtils.chatMessage(HudUtils.textTranslatable(ChatFormatting.RED, "commands.tradefinder.start.not-selected"));
            return;
        }
        
        switch (state) {
            case CHECK -> HudUtils.overlayMessage(HudUtils.textTranslatable(ChatFormatting.GRAY, "librarian-trade-finder.actionbar.status.check", tries), false);
            case BREAK -> HudUtils.overlayMessage(HudUtils.textTranslatable(ChatFormatting.GRAY, "librarian-trade-finder.actionbar.status.break", tries), false);
            case PLACE -> HudUtils.overlayMessage(HudUtils.textTranslatable(ChatFormatting.GRAY, "librarian-trade-finder.actionbar.status.place", tries), false);
            case SELECT_MANUAL -> HudUtils.overlayMessage(HudUtils.textTranslatable(ChatFormatting.GRAY, "librarian-trade-finder.actionbar.status.select-manual"), false);
        }

        if((state == TradeState.CHECK || state == TradeState.WAITING_FOR_PACKET) && villager.getVillagerData().profession().is(VillagerProfession.LIBRARIAN)) {
            Vec3 villagerPosition = new Vec3(villager.getX(), villager.getY() + (double) villager.getEyeHeight(Pose.STANDING), villager.getZ());

            if(LibrarianTradeFinder.getConfig().legitMode && LibrarianTradeFinder.getConfig().slowMode) {
                if(RotationTools.isRotated && !finishedCheckLook) {
                    finishedCheckLook = true;
                    startedCheckLook = false;
                    RotationTools.isRotated = false;
                }else if(!startedCheckLook && !finishedCheckLook) {
                    RotationTools.smoothLookAt(villagerPosition, 3);
                    startedCheckLook = true;
                    return;
                }else if(!finishedCheckLook) {
                    return;
                }
            } else if (LibrarianTradeFinder.getConfig().legitMode) {
                player.lookAt(EntityAnchorArgument.Anchor.EYES, villagerPosition);
            }

            if(LibrarianTradeFinder.getConfig().slowMode) {
                if(interactDelay > 0) {
                    interactDelay--;
                    return;
                }
                interactDelay = 2;
            }

            InteractionResult result = null;
            if (mc.gameMode != null) {
                result = mc.gameMode.interact(mc.player, villager, InteractionHand.MAIN_HAND);
                mc.player.swing(InteractionHand.MAIN_HAND, true);
                mc.player.connection
                        .send(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
                mc.player.connection
                        .send(ServerboundInteractPacket.createInteractionPacket(villager, false, InteractionHand.MAIN_HAND));
            }
            if(result == InteractionResult.SUCCESS) {
                finishedBreakLook = false;
                state = TradeState.WAITING_FOR_PACKET;
            }else {
                HudUtils.chatMessage(HudUtils.textTranslatable("librarian-trade-finder.check.interact.failed", ChatFormatting.RED));
                stop();
            }

        } else if(state == TradeState.BREAK) {
            BlockPos toPlace = lecternPos.below();
            if(LibrarianTradeFinder.getConfig().legitMode && LibrarianTradeFinder.getConfig().slowMode) {
                if(RotationTools.isRotated && !finishedBreakLook) {
                    finishedBreakLook = true;
                    startedBreakLook = false;
                    RotationTools.isRotated = false;
                }else if(!startedBreakLook && !finishedBreakLook) {
                    RotationTools.smoothLookAt(new Vec3(toPlace.getX() + 0.5, toPlace.getY() + 1.0, toPlace.getZ() + 0.5), 3);
                    startedBreakLook = true;
                    return;
                }else if(!finishedBreakLook) {
                    return;
                }
            } else if (LibrarianTradeFinder.getConfig().legitMode) {
                player.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(toPlace.getX() + 0.5, toPlace.getY() + 1.0, toPlace.getZ() + 0.5));
            }
            Inventory inventory = player.getInventory();
            ItemStack mainHand = inventory.getSelectedItem();
            if(mainHand.getItem() instanceof AxeItem) {
                int remainingDurability = mainHand.getMaxDamage() - mainHand.getDamageValue();
                if(remainingDurability <= 5 && LibrarianTradeFinder.getConfig().preventAxeBreaking) {
                    stop();
                    HudUtils.chatMessage(HudUtils.textTranslatable("librarian-trade-finder.break.axe.breaking", ChatFormatting.RED));
                    return;
                }
            }
            if (mc.level != null && mc.level.getBlockState(lecternPos).getBlock() instanceof LecternBlock && mc.gameMode != null) {
                player.swing(InteractionHand.MAIN_HAND, true);
                mc.gameMode.continueDestroyBlock(lecternPos, Direction.UP);
                player.connection
                        .send(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
            }else {
                finishedPlaceLook = false;
                state = TradeState.PLACE;
                if(LibrarianTradeFinder.getConfig().tpToVillager && mc.getConnection() != null) {
                    prevPos = mc.player.position();
                    mc.getConnection().send(new ServerboundMovePlayerPacket.Pos(villager.getX(), villager.getY(), villager.getZ(), true, false));
                }
            }

        } else if (state == TradeState.PLACE) {
            if(LibrarianTradeFinder.getConfig().tpToVillager && mc.getConnection() != null) {
                mc.getConnection().send(new ServerboundMovePlayerPacket.Pos(prevPos.x, prevPos.y, prevPos.z, true, false));
            }

            BlockPos toPlace = lecternPos.below();
            if(LibrarianTradeFinder.getConfig().legitMode && LibrarianTradeFinder.getConfig().slowMode) {
                //mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(toPlace.getX() + 0.5, toPlace.getY() + 1.0, toPlace.getZ() + 0.5));
                if(RotationTools.isRotated && !finishedPlaceLook) {
                    finishedPlaceLook = true;
                    startedPlaceLook = false;
                    RotationTools.isRotated = false;
                }else if(!startedPlaceLook && !finishedPlaceLook) {
                    RotationTools.smoothLookAt(new Vec3(toPlace.getX() + 0.5, toPlace.getY() + 1.0, toPlace.getZ() + 0.5), 3);
                    startedPlaceLook = true;
                    return;
                }else if(!finishedPlaceLook) {
                    return;
                }

            } else if (LibrarianTradeFinder.getConfig().legitMode) {
                player.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(toPlace.getX() + 0.5, toPlace.getY() + 1.0, toPlace.getZ() + 0.5));
            }

            if(LibrarianTradeFinder.getConfig().slowMode) {
                if(placeDelay > 0) {
                    placeDelay--;
                    return;
                }
                placeDelay = 3;
            }


            if(!mc.player.getOffhandItem().getItem().equals(Items.LECTERN) && mc.gameMode != null) {
                if (mc.player.inventoryMenu == mc.player.containerMenu) {
                    for (int i = 9; i < 45; i++) {
                        if (mc.player.getInventory().getItem(i >= 36 ? i - 36 : i).getItem() == Items.LECTERN) {
                            boolean itemInOffhand = !mc.player.getOffhandItem().isEmpty();
                            mc.gameMode.handleInventoryMouseClick(mc.player.containerMenu.containerId, i, 0, ClickType.PICKUP, mc.player);
                            mc.gameMode.handleInventoryMouseClick(mc.player.containerMenu.containerId, 45, 0, ClickType.PICKUP, mc.player);

                            if (itemInOffhand)
                                mc.gameMode.handleInventoryMouseClick(mc.player.containerMenu.containerId, i, 0, ClickType.PICKUP, mc.player);

                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < 9; i++) {
                        if (mc.player.getInventory().getItem(i).getItem() == Items.LECTERN) {
                            if (i != mc.player.getInventory().getSelectedSlot()) {
                                mc.player.getInventory().setSelectedSlot(i);
                                mc.player.connection.send(new ServerboundSetCarriedItemPacket(i));
                            }

                            mc.player.connection.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
                            break;
                        }
                    }
                }
            }

            BlockHitResult hit = new BlockHitResult(new Vec3(lecternPos.getX(), lecternPos.getY(),
                    lecternPos.getZ()), Direction.UP, lecternPos.below(), false);
            if (mc.gameMode != null) {
                mc.gameMode.useItemOn(mc.player, InteractionHand.OFF_HAND, hit);
                player.swing(InteractionHand.OFF_HAND, true);
                player.connection
                        .send(new ServerboundSwingPacket(InteractionHand.OFF_HAND));
            }

            finishedCheckLook = false;
            state = TradeState.CHECK;
        }
        else if (state == TradeState.SELECT_MANUAL) {
            if (villager != null && lecternPos != null) {
                state = TradeState.IDLE;
            }
        }
    }
}
