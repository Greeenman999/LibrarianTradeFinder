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

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.animate
import gg.essential.elementa.dsl.basicXConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.effect
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.provideDelegate
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.state.BasicState
import gg.essential.universal.USound
import java.awt.Color

class SliderComponent(initialValue: Float) : UIContainer() {

	private var onValueChange: UIComponent.(Float) -> Unit = {}
	private var onValueSave: UIComponent.(Float) -> Unit = {}
	private var formatText: UIComponent.(Float) -> String = { v -> v.toString() }

	private var value: Float = initialValue // Ranges from 0f to 1f
	private var dragging = false
	private var offset = 0f // Offset between mouse x and handle left edge when dragging starts

	val handleWidth = 3f
	fun availableWidth() = this.getWidth() - handleWidth

	val textState: BasicState<String> = BasicState("")

	val outline by UIContainer().constrain {
		x = 0.pixels()
		y = 0.pixels()

		width = 100.percent()
		height = 100.percent()
	} childOf this effect OutlineEffect(Color.DARK_GRAY, 1f)

	// The draggable handle
	val handle by UIBlock(Color.LIGHT_GRAY).constrain {
		x = basicXConstraint {
			(availableWidth() * value) + this@SliderComponent.getLeft()
		}
		y = 0.pixels()

		width = handleWidth.pixels()
		height = 100.percent()
	}.onMouseClick { event ->
		dragging = true
		// Calculate offset between mouse x and handle left edge
		offset = event.relativeX
		event.stopPropagation()
	} childOf this

	val text by UIText().bindText(textState).constrain {
		x = CenterConstraint()
		y = CenterConstraint()
	} childOf this

	init {
		// Handle dragging on the whole slider area
		this.onMouseClick { event ->
			dragging = true
			// Center the handle on the mouse cursor
			offset = handle.getWidth() / 2
			updateValue(event.relativeX)
			event.stopPropagation()
		}
		this.onMouseRelease {
			if (!dragging) return@onMouseRelease
			USound.playButtonPress()
			dragging = false
			// Reset offset
			offset = 0f
			onValueSave(value)
		}
		this.onMouseDrag { mouseX, _, _ ->
			if (!dragging) return@onMouseDrag

			updateValue(mouseX)
		}
	}

	private fun updateValue(mouseX: Float) {
		// Subtract offset from mouse x to get handle left edge position
		val offsetX = mouseX - offset
		val clampedX = offsetX.coerceIn(0f, availableWidth())
		if (availableWidth() == 0f) return
		value = clampedX / availableWidth()
		onValueChange(value)
		textState.set(formatText(value))
	}

	fun updateSliderValue(newValue: Float) {
		val clampedValue = newValue.coerceIn(0f, 1f)
		handle.animate {
			setXAnimation(Animations.OUT_EXP, 0.25f, basicXConstraint {
				(availableWidth() * clampedValue) + this@SliderComponent.getLeft()
			})
			onComplete {
				value = clampedValue
				handle.setX(basicXConstraint {
					(availableWidth() * value) + this@SliderComponent.getLeft()
				})
				reRenderText()
			}
		}
	}

	fun reRenderText() {
		textState.set(formatText(value))
	}

	fun onValueChange(listener: UIComponent.(Float) -> Unit) = apply {
		onValueChange = listener
	}

	fun onValueSave(listener: UIComponent.(Float) -> Unit) = apply {
		onValueSave = listener
	}

	fun formatText(formatter: UIComponent.(Float) -> String) = apply {
		formatText = formatter
		textState.set(formatter(value))
	}
}
