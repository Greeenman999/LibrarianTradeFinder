/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 rotgruengelb, and stonecutter-mod-template contributors
 * See the LICENSE file in the project root for license terms.
 */

package de.greenman999.librariantradefinder.platform.fabric;

//? fabric {

import de.greenman999.librariantradefinder.LibrarianTradeFinder;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ModInitializer;

@Entrypoint("main")
public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		LibrarianTradeFinder.onInitialize();
		FabricEventSubscriber.registerEvents();
	}
}
//?}
