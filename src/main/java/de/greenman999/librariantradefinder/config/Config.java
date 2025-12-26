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

import de.greenman999.librariantradefinder.util.IntegerRange;
import de.greenman999.librariantradefinder.util.RegistryHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@ConfigSerializable
public class Config {

	/**
	 * When in BREAKING phase, stop search if the tool has less than 5 durability left
	 */
	private boolean preventToolBreaking = true;

	/**
	 * Teleport to dropped items (e.g. lecterns) between BREAKING and PLACING phase
	 * to automatically pick items up that dropped behind the player or lectern
	 */
	private boolean teleportToDroppedItems = false;

	/**
	 * Before interacting with the world (breaking/placing/checking villager),
	 * look at the target block/entity to prevent bugs.
	 * Look back to original position after the interaction.
	 */
	private boolean smartLookMode = true;

	/**
	 * Slow down the search process to make it more human-like
	 * and less likely to be detected as a bot.
	 * This is done by lerping the look movements and adding small delays
	 * between actions.
	 */
	private boolean slowMode = false;

	/**
	 * The range of ticks to delay between actions in slow mode.
	 * Minimum is inclusive, maximum is inclusive.
	 * Picked randomly per action.
	 */
	private IntegerRange actionDelayTicks = new IntegerRange(1, 3);

	/**
	 * Automatically buy the trade when a matching trade is found.
	 * This should lock the trade.
	 */
	private boolean buyOnTradeFound = false;

	/**
	 * Notify the player with a sound when a matching trade is found.
	 */
	private boolean notifyOnTradeFound = true;

	/**
	 * The type of second trade to look for.
	 * If null, only the enchanted book trade is considered.
	 */
	@Nullable
	private SecondTradeType secondTradeType = null;

	public enum SecondTradeType {
		SELL_PAPER,
		BUY_BOOKSHELF,
	}

	/**
	 * Display the trades in front of the villager.
	 */
	private boolean displayTradesOnVillager = false;

	/**
	 * The timeout in ticks to wait after PLACING phase
	 * if the villager does not take the job.
	 * If the timeout is reached, the mod will switch to BREAKING phase
	 * to reroll the villager.
	 */
	private int rerollTimeoutTicks = 100;

	private HashMap<Identifier, EnchantmentEntry> enchantments = new HashMap<>();

	@ConfigSerializable
	public static class EnchantmentEntry {

		private boolean enabled;
		private int minLevel;
		private int maxPrice;

		public EnchantmentEntry(boolean enabled, int minLevel, int maxPrice) {
			this.enabled = enabled;
			this.minLevel = minLevel;
			this.maxPrice = maxPrice;
		}

		public EnchantmentEntry() {
			// Configurate
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public int getMinLevel() {
			return minLevel;
		}

		public void setMinLevel(int minLevel) {
			this.minLevel = minLevel;
		}

		public int getMaxPrice() {
			return maxPrice;
		}

		public void setMaxPrice(int maxPrice) {
			this.maxPrice = maxPrice;
		}

	}

	public boolean shouldPreventToolBreaking() {
		return preventToolBreaking;
	}

	public void setPreventToolBreaking(boolean preventToolBreaking) {
		this.preventToolBreaking = preventToolBreaking;
	}

	public boolean shouldTeleportToDroppedItems() {
		return teleportToDroppedItems;
	}

	public void setTeleportToDroppedItems(boolean teleportToDroppedItems) {
		this.teleportToDroppedItems = teleportToDroppedItems;
	}

	public boolean isSmartLookMode() {
		return smartLookMode;
	}

	public void setSmartLookMode(boolean smartLookMode) {
		this.smartLookMode = smartLookMode;
	}

	public boolean isSlowMode() {
		return slowMode;
	}

	public void setSlowMode(boolean slowMode) {
		this.slowMode = slowMode;
	}

	public IntegerRange getActionDelayTicks() {
		return actionDelayTicks;
	}

	public void setActionDelayTicks(IntegerRange actionDelayTicks) {
		this.actionDelayTicks = actionDelayTicks;
	}

	public boolean shouldBuyOnTradeFound() {
		return buyOnTradeFound;
	}

	public void setBuyOnTradeFound(boolean buyOnTradeFound) {
		this.buyOnTradeFound = buyOnTradeFound;
	}

	public boolean shouldNotifyOnTradeFound() {
		return notifyOnTradeFound;
	}

	public void setNotifyOnTradeFound(boolean notifyOnTradeFound) {
		this.notifyOnTradeFound = notifyOnTradeFound;
	}

	public @Nullable SecondTradeType getSecondTradeType() {
		return secondTradeType;
	}

	public void setSecondTradeType(@Nullable SecondTradeType secondTradeType) {
		this.secondTradeType = secondTradeType;
	}

	public boolean shouldDisplayTradesOnVillager() {
		return displayTradesOnVillager;
	}

	public void setDisplayTradesOnVillager(boolean displayTradesOnVillager) {
		this.displayTradesOnVillager = displayTradesOnVillager;
	}

	public int getRerollTimeoutTicks() {
		return rerollTimeoutTicks;
	}

	public void setRerollTimeoutTicks(int rerollTimeoutTicks) {
		this.rerollTimeoutTicks = rerollTimeoutTicks;
	}

	public LinkedHashMap<Identifier, EnchantmentEntry> getEnchantments() {
		LinkedHashMap<Identifier, EnchantmentEntry> sortedEnchantments = new LinkedHashMap<>();
		Registry<Enchantment> enchantmentRegistry = RegistryHelper.getEnchantmentRegistry();
		if (enchantmentRegistry == null) {
			return sortedEnchantments;
		}
		for (Enchantment enchantment : enchantmentRegistry) {
			Identifier id = enchantmentRegistry.getKey(enchantment);

			final TagKey<Enchantment> tradeableTag = TagKey.create(enchantmentRegistry.key(), Identifier.fromNamespaceAndPath("minecraft","tradeable"));

			ResourceKey<Enchantment> enchantmentKey = enchantmentRegistry.getResourceKey(enchantment).orElseThrow();
			Optional<Holder.Reference<Enchantment>> reference = enchantmentRegistry.get(enchantmentKey);
			boolean isTradeable = reference.map(enchantmentReference -> enchantmentReference.is(tradeableTag)).orElse(false);
			if (!isTradeable) {
				continue;
			}

			if (enchantments.containsKey(id)) {
				sortedEnchantments.put(id, enchantments.get(id));
			} else {
				sortedEnchantments.put(id, new EnchantmentEntry(false, enchantment.getMaxLevel(), RegistryHelper.getMaxEmeraldCost(id)));
			}
		}

		sortedEnchantments = new LinkedHashMap<>(sortedEnchantments.entrySet().stream()
				.sorted(Map.Entry.comparingByKey(Comparator.comparing(enchantmentKey ->
						Enchantment.getFullname(
								enchantmentRegistry.get(enchantmentKey).orElseThrow(),
								enchantmentRegistry.get(enchantmentKey).orElseThrow().value().getMaxLevel()
						).copy().withStyle(ChatFormatting.WHITE).getString())))
				.toList()
				.stream()
				.collect(java.util.stream.Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(oldValue, newValue) -> oldValue,
						LinkedHashMap::new
				))
		);

		return sortedEnchantments;
	}

	public HashMap<Identifier, EnchantmentEntry> getEnchantmentsRaw() {
		return enchantments;
	}

	public void updateEnchantment(Identifier id, Function<EnchantmentEntry, EnchantmentEntry> updater) {
		EnchantmentEntry entry = enchantments.get(id);
		if (entry != null) {
			enchantments.put(id, updater.apply(entry));
		} else {
			Enchantment enchantment = RegistryHelper.getEnchantmentById(id);
			if (enchantment == null) {
				return;
			}
			EnchantmentEntry newEntry = new EnchantmentEntry(true, enchantment.getMaxLevel(), RegistryHelper.getMaxEmeraldCost(id));
			enchantments.put(id, updater.apply(newEntry));
		}
	}

	public void enableEnchantment(Identifier id, boolean enabled) {
		Enchantment enchantment = RegistryHelper.getEnchantmentById(id);
		if (enchantment == null) {
			return;
		}

		EnchantmentEntry entry = enchantments.get(id);
		if (entry != null) {
			entry.setEnabled(enabled);
		} else {
			enchantments.put(id, new EnchantmentEntry(enabled, enchantment.getMaxLevel(), RegistryHelper.getMaxEmeraldCost(id)));
		}
	}

	public void setEnchantmentMinLevel(Identifier id, int minLevel) {
		Enchantment enchantment = RegistryHelper.getEnchantmentById(id);
		if (enchantment == null) {
			return;
		}

		EnchantmentEntry entry = enchantments.get(id);
		if (entry != null) {
			entry.setMinLevel(minLevel);
		} else {
			enchantments.put(id, new EnchantmentEntry(true, minLevel, enchantment.getMaxCost(minLevel)));
		}
	}

	public void setEnchantmentMaxPrice(Identifier id, int maxPrice) {
		Enchantment enchantment = RegistryHelper.getEnchantmentById(id);
		if (enchantment == null) {
			return;
		}

		EnchantmentEntry entry = enchantments.get(id);
		if (entry != null) {
			entry.setMaxPrice(maxPrice);
		} else {
			enchantments.put(id, new EnchantmentEntry(true, enchantment.getMaxLevel(), maxPrice));
		}
	}

	public void setEnchantments(HashMap<Identifier, EnchantmentEntry> enchantments) {
		this.enchantments = enchantments;
	}
}
