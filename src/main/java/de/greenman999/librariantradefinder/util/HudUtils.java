package de.greenman999.librariantradefinder.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class HudUtils {
    // Chat messages
    public static void chatMessage(Component text){
        if (Minecraft.getInstance() == null) return;
        Minecraft.getInstance().gui.getChat().addMessage(text);
    }


    // Overlay messaages
    public static void overlayMessage(Component text, boolean tinted){
        if (Minecraft.getInstance() == null) return;
        Minecraft.getInstance().gui.setOverlayMessage(text, tinted);
    }

    // Text
    public static Component textTranslatable(ChatFormatting color, String messageKey, Object... args){
        return Component.translatable(messageKey, args).withStyle(style -> style.withColor(color));
    }

    public static Component textTranslatable(String messageKey, Object... args){
        return Component.translatable(messageKey, args);
    }

    public static Component text(String message, ChatFormatting color){
        return Component.literal(message).withStyle(style -> style.withColor(color));
    }

}
