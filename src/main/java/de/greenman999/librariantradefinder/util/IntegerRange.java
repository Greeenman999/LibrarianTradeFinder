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

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Objects;

/**
 * Generic range for configuration.
 * Use Range.closed(min, max) for inclusive bounds.
 */
@ConfigSerializable
public final class IntegerRange {
	private int min;
	private int max;

	public IntegerRange(int min, int max) {
		this.min = min;
		this.max = max;
		if (min > max) {
			throw new IllegalArgumentException("min must be <= max");
		}
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public boolean contains(int value) {
		return value >= min && value <= max;
	}

	public int randomInRange(java.util.Random random) {
		return min + random.nextInt(max - min + 1);
	}

	/**
	 * Returns value clamped into the range: min if value < min, max if value > max.
	 */
	public int clamp(int value) {
		if (value < min) {
			return min;
		} else if (value > max) {
			return max;
		}
		return value;
	}

	@Override
	public String toString() {
		return "IntegerRange[" + min + "," + max + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof IntegerRange range)) return false;
		return min == range.min && max == range.max;
	}
}
