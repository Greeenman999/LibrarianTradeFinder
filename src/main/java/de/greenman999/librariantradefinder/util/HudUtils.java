package de.greenman999.librariantradefinder.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class HudUtils {
    // Chat messages
    public static void chatMessage(Component text){
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        mc.player.sendSystemMessage(text);
    }

    // Overlay messages
    public static void overlayMessage(Component text, boolean tinted){
        Minecraft.getInstance().gui.hud.setOverlayMessage(text, tinted);
    }

    // Text
    public static Component textTranslatable(ChatFormatting color, String messageKey, Object... args){
        return Component.translatable(messageKey, args).withStyle(style -> style.withColor(color));
    }

    public static Component textTranslatable(String messageKey, Object... args){
        return Component.translatable(messageKey, args);
    }

}
