package de.greenman999.librariantradefinder.mixin;

import de.greenman999.librariantradefinder.LibrarianTradeFinder;
import de.greenman999.librariantradefinder.TradeFinder;
import de.greenman999.librariantradefinder.TradeState;
import de.greenman999.librariantradefinder.config.TradeFinderConfig;
import io.netty.channel.ChannelHandlerContext;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Holder;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicBoolean;

@Environment(net.fabricmc.api.EnvType.CLIENT)
@Mixin(Connection.class)
public class ClientConnectionMixin {

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onChannelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if(packet instanceof ClientboundOpenScreenPacket openScreenS2CPacket) {
            if(openScreenS2CPacket.getType() == MenuType.MERCHANT && !(TradeFinder.state.equals(TradeState.IDLE))) {
                ClientPacketListener networkHandler = Minecraft.getInstance().getConnection();
                if(networkHandler != null) {
                    networkHandler.send(new ServerboundContainerClosePacket(openScreenS2CPacket.getContainerId()));
                }
                ci.cancel();
            }
        }else if(packet instanceof ClientboundMerchantOffersPacket setTradeOffersS2CPacket
                && !TradeFinder.state.equals(TradeState.IDLE)) {
            // Pop the queue on EVERY merchant-offers packet while a search is active, not just
            // in WAITING. Otherwise a packet that arrives during BREAK (e.g. the response to a
            // watchdog re-interact) is dropped without draining its queue entry, leaving the
            // queue out of sync. Next cycle pops the now-stale entry and rejects the real
            // current packet, forcing another full watchdog timeout — visible to the user as
            // the search getting stuck on "checking trade" for ~2 seconds.
            Integer expectedEpoch = TradeFinder.pendingPacketEpochs.pollFirst();
            if (expectedEpoch == null
                    || !TradeFinder.state.equals(TradeState.WAITING_FOR_PACKET)
                    || expectedEpoch != TradeFinder.currentEpoch.get()) {
                return;
            }

            AtomicBoolean found = new AtomicBoolean(false);
            for(MerchantOffer tradeOffer : setTradeOffersS2CPacket.getOffers()) {
                if(!tradeOffer.getResult().getItem().equals(Items.ENCHANTED_BOOK)) continue;
                EnchantmentHelper.getEnchantmentsForCrafting(tradeOffer.getResult()).entrySet().forEach((enchantmentEntry) -> {
                    Enchantment enchantment = enchantmentEntry.getKey().value();
                    int level = enchantmentEntry.getIntValue();

                    if (LibrarianTradeFinder.getConfig().debugLogOffers) {
                        debugLogOffer(tradeOffer, enchantment, level);
                    }

                    int maxBookPrice;
                    int minLevel;
                    if (TradeFinder.searchAll) {
                        TradeFinderConfig.EnchantmentOption enchantmentOption = LibrarianTradeFinder.getConfig().findOptionForEnchantment(enchantment);
                        if (enchantmentOption == null || !enchantmentOption.isEnabled()) return;
                        maxBookPrice = enchantmentOption.getMaxPrice();
                        minLevel = enchantmentOption.getLevel();
                    }
                    else {
                        if (!Enchantment.getFullname(Holder.direct(enchantment), enchantment.getMaxLevel()).equals(
                                Enchantment.getFullname(Holder.direct(TradeFinder.enchantment), enchantment.getMaxLevel()))) return;
                        maxBookPrice = TradeFinder.maxBookPrice;
                        minLevel = TradeFinder.minLevel;
                    }
                    if(tradeOffer.getBaseCostA().getCount() <= maxBookPrice && level >= minLevel) {
                        foundEnchantment(found, tradeOffer, enchantment, level);
                    }
                });
            }
            if(!found.get()) {
                TradeFinder.currentEpoch.incrementAndGet();
                TradeFinder.state = TradeState.BREAK;
                TradeFinder.tries++;
            }
        }
    }

    @Unique
    private void foundEnchantment(AtomicBoolean found, MerchantOffer tradeOffer, Enchantment enchantment, int level) {
        int attempts = TradeFinder.tries; // Save the attempts BEFORE calling stop()
        TradeFinder.stop();
        found.set(true);

        Component baseMessage = Component.translatable(
                "librarian-trade-finder.found",
                Enchantment.getFullname(Holder.direct(enchantment), level),
                tradeOffer.getBaseCostA().getCount(),
                Component.literal(String.valueOf(attempts))
                        .withStyle(style -> style.withColor(0xcc1141))
        ).withStyle(ChatFormatting.GREEN);

        Component finalMessage;
        if (LibrarianTradeFinder.getConfig().showCostRange) {
            int min = TradeFinderConfig.computedMinPrice(enchantment, level);
            int max = TradeFinderConfig.computedMaxPrice(enchantment, level);
            finalMessage = Component.empty()
                    .append(baseMessage)
                    .append(Component.literal(" "))
                    .append(Component.translatable(
                            "librarian-trade-finder.found.cost-range", min, max
                    ).withStyle(ChatFormatting.AQUA));
        } else {
            finalMessage = baseMessage;
        }

        // Chat add must run on the render thread. addMessage internally touches RenderSystem
        // (e.g. text-width measurement); calling it from netty caused the client to disconnect
        // with "Rendersystem called from wrong thread".
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().gui.getChat().addMessage(finalMessage));
    }

    @Unique
    private void debugLogOffer(MerchantOffer tradeOffer, Enchantment enchantment, int level) {
        int min = TradeFinderConfig.computedMinPrice(enchantment, level);
        int max = TradeFinderConfig.computedMaxPrice(enchantment, level);
        Component message = Component.translatable(
                "librarian-trade-finder.debug.offer",
                Enchantment.getFullname(Holder.direct(enchantment), level),
                tradeOffer.getBaseCostA().getCount(),
                min, max
        ).withStyle(ChatFormatting.GRAY);
        // Defer to render thread — see foundEnchantment for why.
        Minecraft.getInstance().execute(() -> Minecraft.getInstance().gui.getChat().addMessage(message));
    }


}
