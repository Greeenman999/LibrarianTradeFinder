package de.greenman999.librariantradefinder.gui.columns

import de.greenman999.librariantradefinder.LibrarianTradeFinder
import de.greenman999.librariantradefinder.gui.components.EnchantmentColumnInfoComponent
import de.greenman999.librariantradefinder.gui.components.EnchantmentComponent
import de.greenman999.librariantradefinder.util.translatable
import de.greenman999.librariantradefinder.util.withHandCursor
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.minus
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.plus
import gg.essential.elementa.dsl.provideDelegate
import gg.essential.elementa.dsl.times
import gg.essential.elementa.dsl.toConstraint
import java.awt.Color
import kotlin.collections.iterator

class EnchantmentColumn : UIContainer() {

	init {
	    val columnInfo by EnchantmentColumnInfoComponent().constrain {
			x = 0.pixels()
			y = 0.pixels()

			width = 100.percent()
			height = ChildBasedMaxSizeConstraint()
	    } childOf this

		val enchantmentsContainer by UIContainer().constrain {
			x = 0.pixels()
			y = columnInfo.getHeight().pixels() + 2.pixels()

			width = 100.percent()
			height = FillConstraint() - 2.pixels()
		} childOf this

		val enchantments by ScrollComponent(translatable("librariantradefinder.gui.empty-enchantments")).constrain {
			x = 0.pixels()
			y = 0.pixels()

			width = 100.percent() - 4.pixels() * 2
			height = 100.percent()
		} childOf enchantmentsContainer
		enchantments.emptyText.setWidth(enchantments.getWidth().pixels() - 10.pixels())
		enchantments.emptyText.setY(CenterConstraint())

		val scrollbar by UIRoundedRectangle(2f).constrain {
			x = 0.pixels(alignOpposite = true)

			width = 4.pixels()

			color = Color(0, 0, 0, 100).toConstraint()
		}.withHandCursor() childOf enchantmentsContainer
		enchantments.setScrollBarComponent(scrollbar, hideWhenUseless = true, isHorizontal = false)

		for (entry in LibrarianTradeFinder.getInstance().config.enchantments) {
			EnchantmentComponent(entry) childOf enchantments
		}
	}

}
