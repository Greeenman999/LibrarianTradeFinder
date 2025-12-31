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

import de.greenman999.librariantradefinder.util.withHandCursor
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.animate
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.effect
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.provideDelegate
import gg.essential.elementa.dsl.toConstraint
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.utils.withAlpha
import java.awt.Color

private const val CHECKED_SIZE = 80
private val ANIMATION_STRATEGY = Animations.OUT_EXP
private const val ANIMATION_LENGTH = 0.3f

class CheckboxComponent(initialChecked: Boolean = false) : UIContainer() {

	var checked: Boolean = initialChecked
		set(value) {
			field = value
			listener?.invoke(this, value)
		}

	var listener: (UIComponent.(Boolean) -> Unit)? = null

	val innerBox by UIBlock().constrain {
		width = if (checked) {
			CHECKED_SIZE.percent()
		} else {
			0.percent()
		}
		height = if (checked) {
			CHECKED_SIZE.percent()
		} else {
			0.percent()
		}
		x = CenterConstraint()
		y = CenterConstraint()

		color = if (checked) {
			Color.WHITE.toConstraint()
		} else {
			Color.WHITE.withAlpha(0).toConstraint()
		}
	} childOf this

	init {
	    constrain {
			width = 10.pixels()
			height = 10.pixels()
		}

		effect(OutlineEffect(Color.WHITE, 1f))

		onMouseClick {
			checked = !checked
			updateChecked()
		}

		withHandCursor()
	}

	fun updateChecked() {
		if (checked) {
			innerBox.animate {
				setWidthAnimation(ANIMATION_STRATEGY, ANIMATION_LENGTH, CHECKED_SIZE.percent())
				setHeightAnimation(ANIMATION_STRATEGY, ANIMATION_LENGTH, CHECKED_SIZE.percent())
				setColorAnimation(ANIMATION_STRATEGY, ANIMATION_LENGTH, Color.WHITE.toConstraint())
			}
		} else {
			innerBox.animate {
				setWidthAnimation(ANIMATION_STRATEGY, ANIMATION_LENGTH, 0.percent())
				setHeightAnimation(ANIMATION_STRATEGY, ANIMATION_LENGTH, 0.percent())
				setColorAnimation(ANIMATION_STRATEGY, ANIMATION_LENGTH, Color.WHITE.withAlpha(0).toConstraint())
			}
		}
	}

	fun check(newChecked: Boolean) {
		if (checked != newChecked) {
			checked = newChecked
			updateChecked()
		}
	}

	fun onUpdate(callback: UIComponent.(Boolean) -> Unit) = apply {
		listener = callback
	}
}
