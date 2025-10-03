package de.greenman999.librariantradefinder.mixin;

import de.greenman999.librariantradefinder.TradeFinder;
import de.greenman999.librariantradefinder.TradeState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends HandledScreen<MerchantScreenHandler> {
    public MerchantScreenMixin(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (TradeFinder.state == TradeState.BUY && !this.handler.getRecipes().isEmpty()) { //waiting for trade offers to load

            int recipe = 0;
            for (TradeOffer offer : this.handler.getRecipes()) {
                if (offer.getSellItem().getItem() == Items.ENCHANTED_BOOK) {
                    break;
                }
                recipe++;
            }

            if (client != null && client.getNetworkHandler() != null && client.interactionManager != null && client.player != null) {
                client.getNetworkHandler().sendPacket(new SelectMerchantTradeC2SPacket(recipe));
                client.interactionManager.clickSlot(handler.syncId, 2, 0, SlotActionType.PICKUP, client.player);
                handler.sendContentUpdates();
                client.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(handler.syncId));
                client.setScreen(null);
            }

            TradeFinder.stop();
        }
    }
}
