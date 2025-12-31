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

package de.greenman999.librariantradefinder.gui.components.options

import de.greenman999.librariantradefinder.gui.components.CheckboxComponent
import de.greenman999.librariantradefinder.gui.components.TooltipComponent
import de.greenman999.librariantradefinder.util.translatable
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.CoerceAtLeastConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.plus
import gg.essential.elementa.dsl.provideDelegate
import gg.essential.elementa.state.BasicState

class BooleanOptionComponent(val optionKey: String, initialChecked: Boolean, var disabled: Boolean = false) : UIContainer() {

	init {
	    constrain {
			width = 100.percent()
			height = CoerceAtLeastConstraint(ChildBasedMaxSizeConstraint() + 4.pixels(), 20.pixels())

			x = 0.pixels()
			y = SiblingConstraint(padding = 5f)
		}
	}

	val label by UIText(translatable("librariantradefinder.gui.options.$optionKey")).constrain {
		x = 0.pixels()
		y = CenterConstraint()
	} childOf this

	val tooltip by TooltipComponent(label)
		.bindVisibility(this@BooleanOptionComponent)
		.bindText(BasicState(translatable("librariantradefinder.gui.options.$optionKey.tooltip")))

	val checkbox by CheckboxComponent(initialChecked).constrain {
		x = 1.pixels(alignOpposite = true)
		y = CenterConstraint()
	} childOf this

	/*fun setDisabled(disabled: Boolean) {
		this.disabled = disabled
		if (disabled) {
			checkbox.alpha = 0.5f
		} else {
			checkbox.alpha = 1.0f
		}
	}*/
}
