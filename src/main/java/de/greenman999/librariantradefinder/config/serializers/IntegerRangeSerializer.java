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

package de.greenman999.librariantradefinder.config.serializers;

import de.greenman999.librariantradefinder.util.IntegerRange;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;

public class IntegerRangeSerializer implements TypeSerializer<IntegerRange> {
	public static final IntegerRangeSerializer INSTANCE = new IntegerRangeSerializer();

	private static final String MIN_KEY = "min";
	private static final String MAX_KEY = "max";

	private ConfigurationNode nonVirtualNode(final ConfigurationNode source, final Object... path) throws SerializationException {
		if (!source.hasChild(path)) {
			throw new SerializationException("Required field " + Arrays.toString(path) + " was not present in node");
		}
		return source.node(path);
	}

	@Override
	public IntegerRange deserialize(@NonNull Type type, @NonNull ConfigurationNode source) throws SerializationException {
		final ConfigurationNode minNode = nonVirtualNode(source, MIN_KEY);
		final ConfigurationNode maxNode = nonVirtualNode(source, MAX_KEY);

		final int min = minNode.getInt();
		final int max = maxNode.getInt();

		if (min > max) {
			throw new SerializationException(minNode, int.class, "min must be <= max");
		}

		return new IntegerRange(min, max);
	}

	@Override
	public void serialize(@NonNull Type type, @Nullable IntegerRange range, @NonNull ConfigurationNode target) throws SerializationException {
		if (range == null) {
			target.raw(null);
			return;
		}

		target.node(MIN_KEY).set(range.getMin());
		target.node(MAX_KEY).set(range.getMax());
	}
}
