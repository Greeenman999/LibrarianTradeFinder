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

package de.greenman999.librariantradefinder.gui

import de.greenman999.librariantradefinder.gui.columns.EnchantmentColumn
import de.greenman999.librariantradefinder.gui.columns.OptionsColumn
import de.greenman999.librariantradefinder.util.translatable
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.CoerceAtMostConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.div
import gg.essential.elementa.dsl.minus
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.plus
import gg.essential.elementa.dsl.provideDelegate
import gg.essential.elementa.dsl.times
import gg.essential.elementa.dsl.toConstraint
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import java.awt.Color

class ConfigScreen(val parent: Screen? = null) : WindowScreen(ElementaVersion.V10, true, true, true) {

	init {
		val paddingXL = 10.pixels()
		val title by UIText(translatable("librariantradefinder.gui.title")).constrain {
			x = CenterConstraint()
			y = paddingXL

			color = Color.WHITE.toConstraint()
		} childOf window

		val container by UIContainer().constrain {
			x = CenterConstraint()
			y = paddingXL * 2 + title.getHeight().pixels()

			width = CoerceAtMostConstraint(100.percent() - paddingXL * 2, 800.pixels())
			height = 100.percent() - (paddingXL * 2 + title.getHeight().pixels()) - paddingXL
		} childOf window //effect OutlineEffect(Color.BLACK, 1f)

		val enchantmentColumn by EnchantmentColumn().constrain {
			x = 0.pixels()
			y = 0.pixels()

			width = 50.percent() - paddingXL / 2
			height = 100.percent()
		} childOf container //effect OutlineEffect(Color.GREEN, 1f)

		val optionsColumn by OptionsColumn().constrain {
			x = 0.pixels(alignOpposite = true)
			y = 0.pixels()

			width = 50.percent() - paddingXL / 2
			height = 100.percent()
		} childOf container //effect OutlineEffect(Color.BLUE, 1f)

		Inspector(window).constrain {
			x = 10.pixels(true)
			y = 10.pixels(true)
		} childOf window
	}

	override fun onClose() {
		super.onClose()
		parent?.let { Minecraft.getInstance().setScreen(it) }
	}
}
