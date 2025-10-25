package de.greenman999.librariantradefinder.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class HudUtils {
    // Chat messages
    public static void chatMessageTranslatable(String messageKey, Formatting color){
        chatMessage(Text.translatable(messageKey).styled(style -> style.withColor(color)));
    }

    public static void chatMessageTranslatable(String messageKey){
        chatMessage(Text.translatable(messageKey));
    }

    public static void chatMessage(String message, Formatting color){
        chatMessage(Text.literal(message).styled(style -> style.withColor(color)));
    }

    public static void chatMessage(String message){
        chatMessage(Text.literal(message));
    }

    public static void chatMessage(Text text){
        if (MinecraftClient.getInstance() == null) return;
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(text);
    }


    // Overlay messaages
    public static void overlayMessageTranslatable(String messageKey, Formatting color, boolean tinted){
        overlayMessage(Text.translatable(messageKey).styled(style -> style.withColor(color)), tinted);
    }

    public static void overlayMessageTranslatable(String messageKey, boolean tinted){
        overlayMessage(Text.translatable(messageKey), tinted);
    }

    public static void overlayMessage(String message, Formatting color, boolean tinted){
        overlayMessage(Text.literal(message).styled(style -> style.withColor(color)), tinted);
    }

    public static void overlayMessage(Text text, boolean tinted){
        if (MinecraftClient.getInstance() == null) return;
        MinecraftClient.getInstance().inGameHud.setOverlayMessage(text, tinted);
    }
}
