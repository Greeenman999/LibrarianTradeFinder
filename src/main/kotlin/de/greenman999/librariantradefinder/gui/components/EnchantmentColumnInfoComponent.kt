package de.greenman999.librariantradefinder.gui.components

import de.greenman999.librariantradefinder.util.translatable
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.minus
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

	val minLevelInfo by UIText(translatable("librariantradefinder.gui.enchantment-column-info.min-level")).constrain {
		x = 65.pixels(alignOpposite = true)
		y = 0.pixels()

		color = Color.WHITE.toConstraint()
	} childOf this

	val maxPriceInfo by UIText(translatable("librariantradefinder.gui.enchantment-column-info.max-price")).constrain {
		x = 8.pixels(alignOpposite = true) - 5.pixels()
		y = 0.pixels()

		color = Color.WHITE.toConstraint()
	} childOf this
}
