package de.greenman999.librariantradefinder.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import gg.essential.universal.UScreen;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(UScreen.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class UScreenMixin {

    //? if >=1.21.9 && !neoforge {
    @Inject(method = "keyPressed", at = @At("RETURN"), cancellable = true)
    private void modifyKeyPressed(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
            cir.setReturnValue(true);
        }
    }
    //?} else {
    /*@Inject(method = "keyPressed", at = @At("RETURN"), cancellable = true)
    private void modifyKeyPressed(int keyCode, int scanCode, int modifierCode, CallbackInfoReturnable<Boolean> cir) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            cir.setReturnValue(true);
        }
    }
    *///?}

}
