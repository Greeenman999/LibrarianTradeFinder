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
import de.greenman999.librariantradefinder.gui.components.options.BooleanOptionComponent
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.minus
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.provideDelegate

class OptionsColumn : UIContainer() {

	val instance = LibrarianTradeFinder.getInstance()

	val preventToolBreakingOption by BooleanOptionComponent("prevent_tool_breaking", instance.config.shouldPreventToolBreaking()) childOf this
	val teleportToDroppedItemsOption by BooleanOptionComponent("teleport_to_dropped_items", instance.config.shouldTeleportToDroppedItems()) childOf this
	val smartLookModeOption by BooleanOptionComponent("smart_look_mode", instance.config.isSmartLookMode) childOf this
	val slowModeOption by BooleanOptionComponent("slow_mode", instance.config.isSlowMode, !instance.config.isSmartLookMode).constrain {
		x = 20.pixels()
		width = 100.percent() - 20.pixels()
	} childOf this
	val buyOnTradeFoundOption by BooleanOptionComponent("buy_on_trade_found", instance.config.shouldBuyOnTradeFound()) childOf this
	val notifyOnTradeFoundOption by BooleanOptionComponent("notify_on_trade_found", instance.config.shouldNotifyOnTradeFound()) childOf this
	val displayTradesOnVillagerOption by BooleanOptionComponent("display_trades_on_villager", instance.config.shouldDisplayTradesOnVillager()) childOf this

	init {
		preventToolBreakingOption.checkbox.onUpdate {
			instance.config.setPreventToolBreaking(it)
			instance.configManager.save()
		}

		teleportToDroppedItemsOption.checkbox.onUpdate {
			instance.config.setTeleportToDroppedItems(it)
			instance.configManager.save()
		}

		smartLookModeOption.checkbox.onUpdate {
			instance.config.isSmartLookMode = it
			instance.configManager.save()
			slowModeOption.disabled.set(!it)
		}

		slowModeOption.checkbox.onUpdate {
			instance.config.isSlowMode = it
			instance.configManager.save()
		}

		buyOnTradeFoundOption.checkbox.onUpdate {
			instance.config.setBuyOnTradeFound(it)
			instance.configManager.save()
		}

		notifyOnTradeFoundOption.checkbox.onUpdate {
			instance.config.setNotifyOnTradeFound(it)
			instance.configManager.save()
		}

		displayTradesOnVillagerOption.checkbox.onUpdate {
			instance.config.setDisplayTradesOnVillager(it)
			instance.configManager.save()
		}
	}
}
