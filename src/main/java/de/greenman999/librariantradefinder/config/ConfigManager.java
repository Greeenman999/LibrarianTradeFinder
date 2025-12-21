/*
 * A minecraft mod that helps you find the enchantments you need from a Librarian Villager.
 * Copyright (C) 2025. Greenman999
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package de.greenman999.librariantradefinder.config;

import de.greenman999.librariantradefinder.LibrarianTradeFinder;
import de.greenman999.librariantradefinder.config.serializers.IntegerRangeSerializer;
import de.greenman999.librariantradefinder.util.IntegerRange;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;

import java.nio.file.Path;

public class ConfigManager {

	private Config config;

	public Config getConfig() {
		if (config == null) {
			loadConfig();
		}
		return config;
	}

	public void loadConfig() {
		final GsonConfigurationLoader loader = createLoader();

		try {
			final ConfigurationNode node = loader.load();
			this.config = node.get(Config.class);
		} catch (ConfigurateException e) {
			throw new RuntimeException(e);
		}

		this.save();
	}

	public void save() {
		if (config == null) {
			throw new IllegalStateException("Config is not loaded");
		}

		final GsonConfigurationLoader loader = createLoader();

		try {
			final ConfigurationNode node = loader.createNode();
			node.set(Config.class, this.config);
			loader.save(node);
		} catch (ConfigurateException e) {
			throw new RuntimeException(e);
		}
	}

	private GsonConfigurationLoader createLoader() {
		final Path configDirectory = LibrarianTradeFinder.xplat().configDirectory();
		return GsonConfigurationLoader.builder()
				.path(configDirectory.resolve(LibrarianTradeFinder.MOD_ID + ".json"))
				.defaultOptions(opts -> opts.serializers(builder -> builder.register(IntegerRange.class, IntegerRangeSerializer.INSTANCE)))
				.build();
	}
}
