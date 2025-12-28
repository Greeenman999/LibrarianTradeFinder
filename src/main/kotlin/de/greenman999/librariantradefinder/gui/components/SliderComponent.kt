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

	init {
		val outline by UIContainer().constrain {
			x = 0.pixels()
			y = 0.pixels()

			width = 100.percent()
			height = 100.percent()
		} childOf this effect OutlineEffect(Color.DARK_GRAY, 1f)

		val handle by UIBlock(Color.LIGHT_GRAY).constrain {
			x = basicXConstraint {
				((this@SliderComponent.getWidth() - getWidth()) * value) + this@SliderComponent.getLeft()
			}
			y = 0.pixels()

			width = 5.pixels()
			height = 100.percent()
		}.onMouseClick { event ->
			dragging = true
			offset = event.relativeX - (getWidth() / 2f)
		}.onMouseRelease {
			dragging = false
			offset = 0f
		} childOf this

		onMouseClick { event ->
			dragging = true
			//value = event.relativeX.coerceIn(0f, this@SliderComponent.getWidth()) / this@SliderComponent.getWidth()
			updateValue(event.relativeX)
			event.stopPropagation()
		}
		onMouseRelease {
			dragging = false
			offset = 0f
		}
		onMouseDrag { mouseX, mouseY, mouseButton ->
			if (!dragging) return@onMouseDrag

			updateValue(mouseX)
			//val x = mouseX - offset
			//value = x.coerceIn(0f, this@SliderComponent.getWidth()) / this@SliderComponent.getWidth()
		}
	}

	fun updateValue(mouseX: Float) {
		val offsetX = mouseX - offset
		val clampedX = offsetX.coerceIn(0f, this@SliderComponent.getWidth())
		value = clampedX / this@SliderComponent.getWidth()
	}
}
