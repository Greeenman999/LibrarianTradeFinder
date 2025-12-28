package de.greenman999.librariantradefinder.gui.components

import de.greenman999.librariantradefinder.util.translatable
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.minus
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.provideDelegate
import gg.essential.elementa.dsl.toConstraint
import java.awt.Color

class EnchantmentColumnInfoComponent : UIContainer() {

	val enchantmentInfo by UIText(translatable("librariantradefinder.gui.enchantment-column-info.enchantment")).constrain {
		x = 5.pixels()
		y = 0.pixels()

		color = Color.WHITE.toConstraint()
	} childOf this

	// 8 for scrollbar and padding, 5 for padding inside EnchantmentComponent, 51 for width of emeralds slider (include outline)
	val maxPriceInfoX = 100.percent() - 8.pixels() - 5.pixels() - 51.pixels()
	val maxPriceInfo by UIText(translatable("librariantradefinder.gui.enchantment-column-info.max-price")).constrain {
		x = maxPriceInfoX
		y = 0.pixels()

		color = Color.WHITE.toConstraint()
	} childOf this

	// 40 for width of min level info, 5 for padding
	val minLevelInfoX = maxPriceInfoX - 40.pixels() - 5.pixels()
	val minLevelInfo by UIText(translatable("librariantradefinder.gui.enchantment-column-info.min-level")).constrain {
		x = minLevelInfoX
		y = 0.pixels()

		color = Color.WHITE.toConstraint()
	} childOf this
}
