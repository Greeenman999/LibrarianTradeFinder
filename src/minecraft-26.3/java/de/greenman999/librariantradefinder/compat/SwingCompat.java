package de.greenman999.librariantradefinder.compat;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.SwingAnimation;

public class SwingCompat {
    public static void attack(LocalPlayer player) {
        SwingAnimation animation = player.getMainHandItem().getOrDefault(DataComponents.ATTACK_ANIMATION, SwingAnimation.DEFAULT);
        player.swing(InteractionHand.MAIN_HAND, animation, false);
    }

    public static void interact(LocalPlayer player, InteractionHand hand) {
        SwingAnimation animation = player.getItemInHand(hand).getOrDefault(DataComponents.INTERACT_ANIMATION, SwingAnimation.DEFAULT);
        player.swing(hand, animation, false);
    }
}
