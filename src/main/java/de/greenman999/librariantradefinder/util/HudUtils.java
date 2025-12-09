package de.greenman999.librariantradefinder.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class HudUtils {
    // Chat messages
    public static void chatMessage(Text text){
        if (MinecraftClient.getInstance() == null) return;
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(text);
    }


    // Overlay messaages
    public static void overlayMessage(Text text, boolean tinted){
        if (MinecraftClient.getInstance() == null) return;
        MinecraftClient.getInstance().inGameHud.setOverlayMessage(text, tinted);
    }

    // Text
    public static Text textTranslatable(Formatting color, String messageKey, Object... args){
        return Text.translatable(messageKey, args).styled(style -> style.withColor(color));
    }

    public static Text textTranslatable(String messageKey, Object... args){
        return Text.translatable(messageKey, args);
    }

    public static Text text(String message, Formatting color){
        return Text.literal(message).styled(style -> style.withColor(color));
    }

}
