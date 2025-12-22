/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 rotgruengelb, and stonecutter-mod-template contributors
 * See the LICENSE file in the project root for license terms.
 */

package de.greenman999.librariantradefinder.platform.neoforge;

//? neoforge {

/*import de.greenman999.librariantradefinder.LibrarianTradeFinder;
import de.greenman999.librariantradefinder.gui.ConfigScreen;
import gg.essential.universal.UResolution;
import gg.essential.universal.UScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(LibrarianTradeFinder.MOD_ID)
public class NeoforgeEntrypoint {

	public NeoforgeEntrypoint() {
		LibrarianTradeFinder.onInitialize();
		ModLoadingContext.get().registerExtensionPoint(
				IConfigScreenFactory.class,
				() -> (clientGui, parent) -> {
					parent.onClose();
					Screen screen = new ConfigScreen(parent);
					screen.init(UResolution.getScaledWidth(), UResolution.getScaledHeight());
					return screen;
				}
		);
	}
}
*///?}
