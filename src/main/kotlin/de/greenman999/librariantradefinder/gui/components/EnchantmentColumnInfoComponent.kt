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

package de.greenman999.librariantradefinder.gui.components

import de.greenman999.librariantradefinder.util.translatable
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.minus
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.provideDelegate
import gg.essential.elementa.dsl.toConstraint
import java.awt.Color

class EnchantmentColumnInfoComponent : UIContainer() {

	val enchantmentInfo by UIText(translatable("librariantradefinder.gui.enchantment-column-info.enchantment")).constrain {
		x = 5.pixels()
		y = 0.pixels()

		color = Color.WHITE.toConstraint()
	} childOf this

	// 8 for scrollbar and padding, 5 for padding inside EnchantmentComponent, 51 for width of emeralds slider (include outline)
	val maxPriceInfoX = 100.percent() - 8.pixels() - 5.pixels() - 51.pixels()
	val maxPriceInfo by UIText(translatable("librariantradefinder.gui.enchantment-column-info.max-price")).constrain {
		x = maxPriceInfoX
		y = 0.pixels()

		color = Color.WHITE.toConstraint()
	} childOf this

	// 40 for width of min level info, 5 for padding
	val levelInfoX = maxPriceInfoX - 40.pixels() - 5.pixels()
	val levelInfo by UIText(translatable("librariantradefinder.gui.enchantment-column-info.level")).constrain {
		x = levelInfoX
		y = 0.pixels()

		color = Color.WHITE.toConstraint()
	} childOf this
}
