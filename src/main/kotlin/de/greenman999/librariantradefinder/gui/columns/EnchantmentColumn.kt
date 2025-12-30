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

package de.greenman999.librariantradefinder.gui.columns

import de.greenman999.librariantradefinder.LibrarianTradeFinder
import de.greenman999.librariantradefinder.gui.components.EnchantmentColumnInfoComponent
import de.greenman999.librariantradefinder.gui.components.EnchantmentComponent
import de.greenman999.librariantradefinder.gui.components.SearchbarComponent
import de.greenman999.librariantradefinder.gui.components.TooltipComponent
import de.greenman999.librariantradefinder.util.RegistryHelper
import de.greenman999.librariantradefinder.util.translatable
import de.greenman999.librariantradefinder.util.withHandCursor
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.minus
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.plus
import gg.essential.elementa.dsl.provideDelegate
import gg.essential.elementa.dsl.times
import gg.essential.elementa.dsl.toConstraint
import gg.essential.elementa.state.BasicState
import gg.essential.universal.UMinecraft
import gg.essential.universal.USound
import org.apache.commons.text.similarity.FuzzyScore
import java.awt.Color
import java.util.Locale
import kotlin.collections.iterator

class EnchantmentColumn : UIContainer() {

	init {
		val searchbar by SearchbarComponent().constrain {
			x = 0.pixels()
			y = 0.pixels()

			width = 100.percent() - 24.pixels()
			height = 20.pixels()
		} childOf this

	    val columnInfo by EnchantmentColumnInfoComponent().constrain {
			x = 0.pixels()
			y = SiblingConstraint(padding = 4f)

			width = 100.percent()
			height = ChildBasedMaxSizeConstraint()
	    } childOf this

		val enchantmentsContainer by UIContainer().constrain {
			x = 0.pixels()
			y = SiblingConstraint(padding = 2f)

			width = 100.percent()
			height = FillConstraint() - 2.pixels() - 4.pixels() + 20.pixels()
		} childOf this

		val enchantments by ScrollComponent(translatable("librariantradefinder.gui.empty-enchantments")).constrain {
			x = 0.pixels()
			y = 0.pixels()

			width = 100.percent() - 4.pixels() * 2
			height = 100.percent()
		} childOf enchantmentsContainer
		enchantments.emptyText.setWidth(100.percent() - 10.pixels())
		enchantments.emptyText.setY(CenterConstraint())

		val resetButton by UIRoundedRectangle(4f).constrain {
			x = 0.pixels(alignOpposite = true)
			y = 0.pixels()
			width = 20.pixels()
			height = 20.pixels()
			color = Color(200, 50, 50, 150).toConstraint()
		}.onMouseClick {
			USound.playButtonPress()
			LibrarianTradeFinder.getInstance().config.setEnchantments(HashMap())
			LibrarianTradeFinder.getInstance().configManager.save()
			for (child in enchantments.allChildren.toList()) {
				(child as? EnchantmentComponent)?.reset()
			}
		}.withHandCursor() childOf this

		val resetIcon by UIText("\u21BA").constrain {
			x = CenterConstraint()
			y = CenterConstraint() - 4.pixels()
			color = Color.WHITE.toConstraint()
			textScale = 2.pixels
			height = 6.pixels()
		} childOf resetButton

		val resetTootip by TooltipComponent(resetButton)
			.bindVisibility(resetButton)
			.bindText(BasicState(translatable("librariantradefinder.gui.tooltip.reset")))

		val scrollbar by UIRoundedRectangle(2f).constrain {
			x = 0.pixels(alignOpposite = true)

			width = 4.pixels()

			color = Color(0, 0, 0, 100).toConstraint()
		}.withHandCursor() childOf enchantmentsContainer
		enchantments.setScrollBarComponent(scrollbar, hideWhenUseless = true, isHorizontal = false)

		val fuzzyScore = FuzzyScore(Locale.of(UMinecraft.getSettings().languageCode))

		for (entry in LibrarianTradeFinder.getInstance().config.enchantments) {
			EnchantmentComponent(entry, searchbar.searchTextState, fuzzyScore) childOf enchantments
		}

		searchbar.searchTextState.onSetValue { text ->
			enchantments.sortChildren { first, second ->
				val firstEnchantmentComponent = first as EnchantmentComponent
				val secondEnchantmentComponent = second as EnchantmentComponent

				val firstEnchantmentName: String = RegistryHelper.getEnchantmentById(firstEnchantmentComponent.entry.key).description().string
				val secondEnchantmentName: String = RegistryHelper.getEnchantmentById(secondEnchantmentComponent.entry.key).description().string

				if (text.isEmpty()) {
					return@sortChildren firstEnchantmentName.compareTo(secondEnchantmentName)
				}

				val firstScore = fuzzyScore.fuzzyScore(firstEnchantmentName, text)
				val secondScore = fuzzyScore.fuzzyScore(secondEnchantmentName, text)

				secondScore - firstScore
			}
		}
	}

}
