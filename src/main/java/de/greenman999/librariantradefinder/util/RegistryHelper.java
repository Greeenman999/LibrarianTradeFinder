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

package de.greenman999.librariantradefinder.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Objects;
import java.util.Optional;

public class RegistryHelper {

	/**
	 * Retrieves the enchantment registry from the current Minecraft level.
	 *
	 * @return The enchantment registry, or null if the level is not loaded.
	 */
	public static Registry<Enchantment> getEnchantmentRegistry() {
		if (Minecraft.getInstance().level == null) {
			return null;
		}
		return Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
	}

	/**
	 * Fetches an enchantment by its identifier.
	 *
	 * @param id The identifier of the enchantment.
	 * @return The enchantment, or null if it does not exist or the registry is unavailable.
	 */
	public static Enchantment getEnchantmentById(Identifier id) {
		Registry<Enchantment> enchantmentRegistry = getEnchantmentRegistry();
		if (enchantmentRegistry == null || !enchantmentRegistry.containsKey(id) || enchantmentRegistry.get(id).isEmpty())
			return null;
		return enchantmentRegistry.get(id).get().value();
	}

	/**
	 * Calculates the maximum emerald cost for an enchantment at a given level.
	 *
	 * @param id    The identifier of the enchantment.
	 * @param level The level of the enchantment. If null, the maximum level of the enchantment is used.
	 * @return The maximum emerald cost, capped at 64.
	 */
	public static int getMaxEmeraldCost(Identifier id, Integer level) {
		Optional<Holder.Reference<Enchantment>> enchantment = Objects.requireNonNull(getEnchantmentRegistry()).get(id);
		if (enchantment.isEmpty())
			return 64;
		if (level == null) {
			level = enchantment.get().value().getMaxLevel();
		}
		int cost = 6 + 13 * level;
		if (enchantment.get().is(EnchantmentTags.DOUBLE_TRADE_PRICE)) {
			cost *= 2;
		}

		if (cost > 64) {
			cost = 64;
		}
		return cost;
	}

	/**
	 * Calculates the minimum emerald cost for an enchantment at a given level.
	 *
	 * @param id    The identifier of the enchantment.
	 * @param level The level of the enchantment. If null, the maximum level of the enchantment is used.
	 * @return The minimum emerald cost, capped at 64.
	 */
	public static int getMinEmeraldCost(Identifier id, Integer level) {
		Optional<Holder.Reference<Enchantment>> enchantment = Objects.requireNonNull(getEnchantmentRegistry()).get(id);
		if (enchantment.isEmpty())
			return 1;
		if (level == null) {
			level = enchantment.get().value().getMaxLevel();
		}
		int cost = 2 + 3 * level;
		if (enchantment.get().is(EnchantmentTags.DOUBLE_TRADE_PRICE)) {
			cost *= 2;
		}

		if (cost > 64) {
			cost = 64;
		}
		return cost;
	}
}
