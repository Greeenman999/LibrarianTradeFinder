/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 rotgruengelb, and stonecutter-mod-template contributors
 * Copyright (c) 2025 murder_spagurder
 * See the LICENSE file in the project root for license terms.
 */

package de.greenman999.librariantradefinder.mixin;

import de.greenman999.librariantradefinder.LibrarianTradeFinder;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public class ExampleMixin {

	@Inject(method = "loadLevel", at = @At("RETURN"))
	private void afterLoadLevel(CallbackInfo ci) {
		LibrarianTradeFinder.LOGGER.info("Level Loaded!");
	}

}
