/*
 * A minecraft mod that helps you find the enchantments you need from a Librarian Villager.
 * Copyright (C) 2026. Greenman999
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

package de.greenman999.librariantradefinder.gui.components.options

import de.greenman999.librariantradefinder.gui.components.SliderComponent
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.effect
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.provideDelegate
import gg.essential.elementa.effects.OutlineEffect
import java.awt.Color

class IntegerOptionComponent(optionKey: String, initialValue: Int, val min: Int, val max: Int, initialDisabled: Boolean = false, tooltip: Boolean = true) : OptionComponent(optionKey, initialDisabled, tooltip) {

	val slider by SliderComponent(normalizeValue(initialValue)).constrain {
		x = 1.pixels(alignOpposite = true)
		y = CenterConstraint()

		width = 60.pixels()
		height = 14.pixels()
	}.formatText {
		denormalizeValue(it).toString()
	} childOf this

	init {
	    slider.outline.effect(OutlineEffect(Color(255, 255, 255), 1f))
	}

	fun normalizeValue(value: Int): Float {
		return (value - min).toFloat() / (max - min).toFloat()
	}

	fun denormalizeValue(value: Float): Int {
		return (value * (max - min)).toInt() + min
	}

}
