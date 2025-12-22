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

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class IdentifierSerializer implements TypeSerializer<Identifier> {

	public static final IdentifierSerializer INSTANCE = new IdentifierSerializer();

	@Override
	public Identifier deserialize(@NonNull Type type, @NonNull ConfigurationNode node) throws SerializationException {
		if (node.getString() == null) {
			throw new SerializationException("Identifier string is null");
		}
		return Identifier.tryParse(node.getString());
	}

	@Override
	public void serialize(@NonNull Type type, @Nullable Identifier identifier, @NonNull ConfigurationNode node) throws SerializationException {
		if (identifier == null) {
			node.raw(null);
		} else {
			node.set(identifier.toString());
		}
	}
}
