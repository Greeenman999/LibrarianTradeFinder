package de.greenman999.mixin;

import de.greenman999.LibrarianTradeFinder;
import de.greenman999.TradeFinder;
import de.greenman999.TradeState;
import de.greenman999.config.TradeFinderConfig;
import io.netty.channel.ChannelHandlerContext;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicBoolean;

@Environment(net.fabricmc.api.EnvType.CLIENT)
@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onChannelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if(packet instanceof OpenScreenS2CPacket openScreenS2CPacket) {
            if(openScreenS2CPacket.getScreenHandlerType() == ScreenHandlerType.MERCHANT && !(TradeFinder.state.equals(TradeState.IDLE))) {
                ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
                if(networkHandler != null) {
                    networkHandler.sendPacket(new CloseHandledScreenC2SPacket(openScreenS2CPacket.getSyncId()));
                }
                ci.cancel();
            }
        }else if(packet instanceof SetTradeOffersS2CPacket setTradeOffersS2CPacket && TradeFinder.state.equals(TradeState.WAITING_FOR_PACKET)) {
            AtomicBoolean found = new AtomicBoolean(false);
            for(TradeOffer tradeOffer : setTradeOffersS2CPacket.getOffers()) {
                if(!tradeOffer.getSellItem().getItem().equals(Items.ENCHANTED_BOOK)) continue;
                EnchantmentHelper.getEnchantments(tradeOffer.getSellItem()).getEnchantmentsMap().forEach((enchantmentEntry) -> {
                    Enchantment enchantment = enchantmentEntry.getKey().value();
                    int level = enchantmentEntry.getIntValue();
                    System.out.println(enchantment.getName(level));
                    int maxBookPrice;
                    int minLevel;
                    if (TradeFinder.searchAll) {
                        TradeFinderConfig.EnchantmentOption enchantmentOption = LibrarianTradeFinder.getConfig().enchantments.get(enchantment);
                        if (enchantmentOption == null || !enchantmentOption.isEnabled()) return;
                        maxBookPrice = enchantmentOption.getMaxPrice();
                        minLevel = enchantmentOption.getLevel();
                    }
                    else {
                        if (!enchantment.getName(enchantment.getMaxLevel()).equals(TradeFinder.enchantment.getName(enchantment.getMaxLevel()))) return;
                        maxBookPrice = TradeFinder.maxBookPrice;
                        minLevel = TradeFinder.minLevel;
                    }
                    if(tradeOffer.getOriginalFirstBuyItem().getCount() <= maxBookPrice && level >= minLevel) {
                        foundEnchantment(found, tradeOffer, enchantment, level);
                    }
                });
            }
            if(!found.get()) {
                TradeFinder.state = TradeState.BREAK;
                TradeFinder.tries++;
            }
        }
    }

    @Unique
    private void foundEnchantment(AtomicBoolean found, TradeOffer tradeOffer, Enchantment enchantment, int level) {
        TradeFinder.stop();
        found.set(true);
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.translatable("librarian-trade-finder.found", enchantment.getName(level), tradeOffer.getOriginalFirstBuyItem().getCount()).formatted(Formatting.GREEN));
    }

}
