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

import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.input.UITextInput
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.minus
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.plus
import gg.essential.elementa.dsl.provideDelegate
import gg.essential.elementa.dsl.toConstraint
import gg.essential.elementa.state.BasicState
import java.awt.Color

private const val PADDING = 8f

class SearchbarComponent : UIContainer() {

	val background by UIRoundedRectangle(4f).constrain {
		x = 0.pixels()
		y = 0.pixels()

		width = 100.percent()
		height = 100.percent()

		color = Color(0, 0, 0, 150).toConstraint()
	} childOf this

	val magnifyingGlassIcon by UIText("\uD83D\uDD0D").constrain {
		x = PADDING.pixels()
		y = CenterConstraint()

		height = 10.pixels()
	} childOf background

	val inputField by UITextInput("Search enchantments...", shadow = false).constrain {
		x = SiblingConstraint(padding = PADDING)
		y = CenterConstraint()

		width = (100.percent() - (PADDING * 3).pixels() - magnifyingGlassIcon.getWidth().pixels())
		height = 8.pixels()
		color = Color(130, 130, 130).toConstraint()
	} childOf background

	val searchTextState = BasicState("")

	init {
		background.onMouseClick {
			inputField.grabWindowFocus()
		}

		inputField.onUpdate { text -> searchTextState.set(text) }
	}

}
