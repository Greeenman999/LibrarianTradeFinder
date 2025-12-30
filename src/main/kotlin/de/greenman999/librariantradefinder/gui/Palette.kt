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

import gg.essential.elementa.state.BasicState
import java.awt.Color

object Palette {

	fun getPalette0(): BasicState<Color> = palette0
	fun getPalette1(): BasicState<Color> = palette1
	fun getPalette2(): BasicState<Color> = palette2
	fun getPalette3(): BasicState<Color> = palette3
	fun getPalette4(): BasicState<Color> = palette4
	fun getPalette5(): BasicState<Color> = palette5
	fun getPalette6(): BasicState<Color> = palette6
	fun getPalette7(): BasicState<Color> = palette7
	fun getPalette8(): BasicState<Color> = palette8
	fun getPalette9(): BasicState<Color> = palette9
	fun getPalette10(): BasicState<Color> = palette10
	fun getPalette11(): BasicState<Color> = palette11
	fun getPalette12(): BasicState<Color> = palette12
	fun getPalette13(): BasicState<Color> = palette13
	fun getPalette14(): BasicState<Color> = palette14
	fun getPalette15(): BasicState<Color> = palette15


	internal val palette0 = BasicState(Color(27, 29, 43))
	internal val palette1 = BasicState(Color(255, 117, 127))
	internal val palette2 = BasicState(Color(195, 232, 141))
	internal val palette3 = BasicState(Color(255, 199, 119))
	internal val palette4 = BasicState(Color(130, 170, 255))
	internal val palette5 = BasicState(Color(192, 153, 255))
	internal val palette6 = BasicState(Color(134, 225, 252))
	internal val palette7 = BasicState(Color(130, 139, 184))
	internal val palette8 = BasicState(Color(68, 74, 115))
	internal val palette9 = BasicState(Color(255, 141, 148))
	internal val palette10 = BasicState(Color(199, 251, 109))
	internal val palette11 = BasicState(Color(255, 216, 171))
	internal val palette12 = BasicState(Color(154, 184, 255))
	internal val palette13 = BasicState(Color(202, 171, 255))
	internal val palette14 = BasicState(Color(178, 235, 255))
	internal val palette15 = BasicState(Color(200, 211, 245))

	// Core brand / accents (use existing palette entries)
	val primary: BasicState<Color> = getPalette4()
	val onPrimary: BasicState<Color> = getPalette8()

	val secondary: BasicState<Color> = getPalette1()
	val onSecondary: BasicState<Color> = getPalette0()

	// Background & surfaces
	val background: BasicState<Color> = BasicState(Color(34, 36, 54))
	val surface: BasicState<Color> = getPalette0()
	val onSurface: BasicState<Color> = BasicState(Color(200, 211, 245))

	// Selection / highlights
	val selection: BasicState<Color> = BasicState(Color(45, 63, 118))
	val selectionText: BasicState<Color> = BasicState(Color(200, 211, 245))

	// Interaction states
	val hover: BasicState<Color> = BasicState(Color(255, 255, 255, 20))
	val focus: BasicState<Color> = BasicState(Color(130, 170, 255, 200))
	val active: BasicState<Color> = BasicState(Color(255, 255, 255, 40))

	// Semantic colors
	val success: BasicState<Color> = getPalette2()
	val error: BasicState<Color> = getPalette1()
	val warning: BasicState<Color> = getPalette10()
	val info: BasicState<Color> = getPalette12()

	// Utility / UX
	val disabled: BasicState<Color> = BasicState(Color(130, 130, 130, 140))
	val border: BasicState<Color> = BasicState(Color(80, 88, 120, 160))
	val shadow: BasicState<Color> = BasicState(Color(0, 0, 0, 120))

	// tokyo night
	/*internal val background = BasicState(Color(34, 36, 54, backgroundAlpha))
	internal val foreground = BasicState(Color(200, 211, 245))
	internal val selectionBackground = BasicState(Color(45, 63, 118, backgroundAlpha))
	internal val selectionForeground = BasicState(Color(200, 211, 245))*/
}
