package de.greenman999.mixin;

import de.greenman999.TradeFinder;
import de.greenman999.TradeState;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicBoolean;

@Environment(net.fabricmc.api.EnvType.CLIENT)
@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onChannelRead0(io.netty.channel.ChannelHandlerContext channelHandlerContext, net.minecraft.network.Packet<?> packet, CallbackInfo ci) {
        if(packet instanceof OpenScreenS2CPacket openScreenS2CPacket) {
            if(openScreenS2CPacket.getScreenHandlerType() == ScreenHandlerType.MERCHANT && (TradeFinder.state.equals(TradeState.CHECK) || TradeFinder.state.equals(TradeState.WAITING_FOR_PACKET))) {
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(openScreenS2CPacket.getSyncId()));
                ci.cancel();
            }
        }else if(packet instanceof SetTradeOffersS2CPacket setTradeOffersS2CPacket && TradeFinder.state.equals(TradeState.WAITING_FOR_PACKET)) {
            AtomicBoolean found = new AtomicBoolean(false);
            for(TradeOffer tradeOffer : setTradeOffersS2CPacket.getOffers()) {
                if(!tradeOffer.getSellItem().getItem().equals(Items.ENCHANTED_BOOK)) continue;
                EnchantmentHelper.get(tradeOffer.getSellItem()).forEach((enchantment, level) -> {
                    if(enchantment.equals(TradeFinder.enchantment) && tradeOffer.getOriginalFirstBuyItem().getCount() <= TradeFinder.maxBookPrice && level == enchantment.getMaxLevel()) {
                        TradeFinder.stop();
                        found.set(true);
                        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(
                                Text.literal("Found enchantment ").formatted(Formatting.GREEN)
                                .append(enchantment.getName(enchantment.getMaxLevel()))
                                        .append(Text.literal(" for ").formatted(Formatting.GREEN))
                                        .append(Text.literal(String.valueOf(tradeOffer.getOriginalFirstBuyItem().getCount())).formatted(Formatting.GRAY))
                                        .append(Text.literal(" emeralds!")).formatted(Formatting.GREEN));
                    }
                });
            }
            if(!found.get()) {
                TradeFinder.state = TradeState.BREAK;
                TradeFinder.tries++;
            }
        }
    }

}
