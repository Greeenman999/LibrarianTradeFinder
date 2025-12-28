package de.greenman999.librariantradefinder.gui.components

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.dsl.basicXConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.effect
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.provideDelegate
import gg.essential.elementa.effects.OutlineEffect
import java.awt.Color

class SliderComponent : UIContainer() {

	private var value: Float = 0f // Ranges from 0f to 1f
	private var dragging = false
	private var offset = 0f // Offset between mouse x and handle left edge when dragging starts

	val handleWidth = 3f
	fun availableWidth() = this.getWidth() - handleWidth

	init {
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

		// Handle dragging on the whole slider area
		this.onMouseClick { event ->
			dragging = true
			// Center the handle on the mouse cursor
			offset = handle.getWidth() / 2
			updateValue(event.relativeX)
			event.stopPropagation()
		}
		this.onMouseRelease {
			dragging = false
			// Reset offset
			offset = 0f
		}
		this.onMouseDrag { mouseX, _, _ ->
			if (!dragging) return@onMouseDrag

			updateValue(mouseX)
		}
	}

	fun updateValue(mouseX: Float) {
		// Subtract offset from mouse x to get handle left edge position
		val offsetX = mouseX - offset
		val clampedX = offsetX.coerceIn(0f, availableWidth())
		value = clampedX / availableWidth()
	}
}
