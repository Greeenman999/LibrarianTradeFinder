/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 rotgruengelb, and stonecutter-mod-template contributors
 * See the LICENSE file in the project root for license terms.
 */

package de.greenman999.librariantradefinder.event;

import de.greenman999.librariantradefinder.LibrarianTradeFinder;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public class ExampleEventHandler {

	public static void onPlayerHurt(ServerPlayer player) {
		LibrarianTradeFinder.LOGGER.info("Player {} was hurt!", Objects.requireNonNull(player.getGameProfile().name()));
	}
}
