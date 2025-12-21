/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 rotgruengelb, and stonecutter-mod-template contributors
 * Copyright (c) 2025 murder_spagurder
 * Copyright (c) 2025 Greenman999
 * See the LICENSE file in the project root for license terms.
 */

package de.greenman999.librariantradefinder;

import de.greenman999.librariantradefinder.config.Config;
import de.greenman999.librariantradefinder.config.ConfigManager;
import de.greenman999.librariantradefinder.platform.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? fabric {
import de.greenman999.librariantradefinder.platform.fabric.FabricPlatform;
//?} neoforge {
/*import de.greenman999.librariantradefinder.platform.neoforge.NeoforgePlatform;
*///?} forge {
/*import de.greenman999.librariantradefinder.platform.forge.ForgePlatform;
*///?}

@SuppressWarnings("LoggingSimilarMessage")
public class LibrarianTradeFinder {

	public static final String MOD_ID = /*$ mod_id*/ "librariantradefinder";
	public static final String MOD_VERSION = /*$ mod_version*/ "0.1.0-alpha.1";
	public static final String MOD_FRIENDLY_NAME = /*$ mod_name*/ "Librarian Trade Finder";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Platform PLATFORM = createPlatformInstance();

	private final ConfigManager configManager = new ConfigManager();

	private static LibrarianTradeFinder instance;

	public LibrarianTradeFinder() {
		instance = this;
	}

	public static void onInitialize() {
		LOGGER.info("Initializing {} on {}", MOD_ID, LibrarianTradeFinder.xplat().loader());
		LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
	}

	public static void onInitializeClient() {
		LOGGER.info("Initializing {} Client on {}", MOD_ID, LibrarianTradeFinder.xplat().loader());
		LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
	}

	public ConfigManager getConfigManager() {
		return this.configManager;
	}

	public Config getConfig() {
		return this.configManager.getConfig();
	}

	public static LibrarianTradeFinder getInstance() {
		return instance;
	}

	public static Platform xplat() {
		return PLATFORM;
	}

	private static Platform createPlatformInstance() {
		//? fabric {
		return new FabricPlatform();
		//?} neoforge {
		/*return new NeoforgePlatform();
		 *///?} forge {
		/*return new ForgePlatform();
		*///?}
	}
}
