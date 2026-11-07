package de.greenman999.librariantradefinder.compat;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;

public class SwingCompat {
    public static void attack(LocalPlayer player) {
        player.swing(InteractionHand.MAIN_HAND);
    }

    public static void interact(LocalPlayer player, InteractionHand hand) {
        player.swing(hand);
    }
}
