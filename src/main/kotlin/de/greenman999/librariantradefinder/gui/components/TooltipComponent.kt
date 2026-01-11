/*
 * This file contains code from github.com/EssentialGG/Vigilance, licensed under LGPL-3.0
 * Copyright (C) 2022. EssentialGG Team and contributors.
 * See NOTICE.md for more information.
 * You should have received a copy of the LGPL-3.0 license under licenses/LGPL.
 *
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
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIPoint
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.components.UIShape
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.dsl.basicYConstraint
import gg.essential.elementa.dsl.boundTo
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.minus
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.plus
import gg.essential.elementa.dsl.provideDelegate
import gg.essential.elementa.dsl.times
import gg.essential.elementa.dsl.toConstraint
import gg.essential.elementa.state.State
import gg.essential.elementa.utils.ObservableClearEvent
import gg.essential.elementa.utils.ObservableRemoveEvent
import java.awt.Color
import java.util.Observer

private const val PADDING = 2

// TODO: Add support for positioning tooltips to the left/right/top of the parent component if there is not enough space
class TooltipComponent(val tooltipParent: UIComponent) : UIContainer() {

	val box by UIRoundedRectangle(4f).constrain {
		x = 0.pixels()
		y = 4.pixels()

		width = 100.pixels() + (PADDING * 2).pixels()
		height = ChildBasedSizeConstraint() + PADDING.pixels() * 2
		color = Color(0, 0, 0, 200).toConstraint()
	} childOf this

	init {
		constrain {
			x = CenterConstraint() boundTo tooltipParent
			y = basicYConstraint {
				tooltipParent.getBottom() + 2
			}

			width = ChildBasedMaxSizeConstraint()
			height = ChildBasedSizeConstraint()
		}

		val arrowHolder by UIContainer().constrain {
			x = CenterConstraint() boundTo this@TooltipComponent
			y = 0.pixels()
			width = 8.pixels()
			height = 4.pixels()
		} childOf this

		(UIShape(box.getColor()) childOf arrowHolder).apply {
			addVertex(UIPoint(0f, 4f))
			addVertex(UIPoint(4f, 0f))
			addVertex(UIPoint(8f, 4f))
		}
	}

	val text by UIWrappedText(centered = true).constrain {
		x = PADDING.pixels()
		y = PADDING.pixels()

		width = 100.percent() - (PADDING * 2).pixels()

		color = Color.WHITE.toConstraint()
	} childOf box

	private var removalListeners = mutableListOf<() -> Unit>()

	fun bindVisibility(visibleState: State<Boolean>) = apply {
		visibleState.onSetValue {
			if (it) {
				this.showTooltip()
			} else {
				this.hideTooltip()
			}
		}
	}

	fun bindVisibility(component: UIComponent) = apply {
		component.onMouseEnter {
			showTooltip()
		}
		component.onMouseLeave {
			hideTooltip()
		}
	}

	fun bindText(textState: State<String>) = apply {
		text.bindText(textState)
	}

	fun showTooltip(nextFrame: Boolean = true) {
		if (nextFrame) {
			return Window.enqueueRenderOperation { showTooltip(false) }
		}

		val window = Window.of(tooltipParent)
		if (this@TooltipComponent in window.children) {
			return
		}

		window.addChild(this@TooltipComponent)
		isFloating = true

		// When our logical parent is removed from the component tree, we also need to remove ourselves (our actual
		// parent is the window, so that is not going to happen by itself).
		// We need to do that asap because our constraints may depend on our logical parent and may error when evaluated
		// after our logical parent got removed.
		// Elementa has no unmount event, so instead we listen for changes to the children list of all our parents.
		fun UIComponent.onRemoved(listener: () -> Unit) {
			if (parent == this) {
				return
			}

			val observer = Observer { _, event ->
				if (event is ObservableClearEvent<*> || event is ObservableRemoveEvent<*> && event.element.value == this) {
					listener()
				}
			}
			parent.children.addObserver(observer)
			removalListeners.add { parent.children.deleteObserver(observer) }

			parent.onRemoved(listener)
		}
		tooltipParent.onRemoved {
			hideTooltip(nextFrame = false)
		}
	}

	fun hideTooltip(nextFrame: Boolean = true) {
		if (nextFrame) {
			return Window.enqueueRenderOperation { hideTooltip(nextFrame = false) }
		}

		val window = Window.ofOrNull(this) ?: return

		isFloating = false
		window.removeChild(this)

		removalListeners.forEach { it() }
		removalListeners.clear()
	}

}
