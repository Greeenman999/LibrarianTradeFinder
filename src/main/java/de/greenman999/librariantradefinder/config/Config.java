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
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

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
}
