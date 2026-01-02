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
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.utils.withAlpha
import java.awt.Color

private const val CHECKED_SIZE = 80
private val ANIMATION_STRATEGY = Animations.OUT_EXP
private const val ANIMATION_LENGTH = 0.3f
private val DISABLED_COLOR = Color.WHITE.withAlpha(0.5f)

class CheckboxComponent(initialChecked: Boolean = false, val disabledState: BasicState<Boolean>) : UIContainer() {

	var checked: Boolean = initialChecked
		set(value) {
			field = value
			listener?.invoke(this, value)
		}

	private fun getInnerColor(): Color {
		return if (disabledState.get()) {
			DISABLED_COLOR
		} else {
			if (!checked) Color.WHITE.withAlpha(0) else Color.WHITE
		}
	}

	val colorState = BasicState(
		if (disabledState.get()) {
			DISABLED_COLOR
		} else {
			Color.WHITE
		}
	)

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

		color = getInnerColor().toConstraint()
	} childOf this

	init {
	    constrain {
			width = 10.pixels()
			height = 10.pixels()
		}

		effect(OutlineEffect(colorState.get(), 1f).bindColor(colorState))

		onMouseClick {
			if (disabledState.get()) return@onMouseClick
			checked = !checked
			updateChecked()
		}

		withHandCursor()

		disabledState.onSetValue {
			if (it) {
				colorState.set(DISABLED_COLOR)
			} else {
				colorState.set(Color.WHITE)
			}
			updateChecked()
		}
	}

	fun updateChecked() {
		innerBox.animate {
			val size = if (checked) {
				CHECKED_SIZE
			} else {
				0
			}
			setWidthAnimation(ANIMATION_STRATEGY, ANIMATION_LENGTH, size.percent())
			setHeightAnimation(ANIMATION_STRATEGY, ANIMATION_LENGTH, size.percent())
			setColorAnimation(ANIMATION_STRATEGY, ANIMATION_LENGTH, getInnerColor().toConstraint())
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
