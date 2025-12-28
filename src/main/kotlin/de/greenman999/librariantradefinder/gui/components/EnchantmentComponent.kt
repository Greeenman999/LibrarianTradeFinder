package de.greenman999.librariantradefinder.gui.components

import de.greenman999.librariantradefinder.LibrarianTradeFinder
import de.greenman999.librariantradefinder.config.Config
import de.greenman999.librariantradefinder.util.RegistryHelper
import de.greenman999.librariantradefinder.util.withHandCursor
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.provideDelegate
import gg.essential.elementa.dsl.toConstraint
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.toConstraint
import net.minecraft.resources.Identifier
import net.minecraft.world.item.enchantment.Enchantment
import java.awt.Color

class EnchantmentComponent(val entry: MutableMap.MutableEntry<Identifier, Config.EnchantmentEntry>) : UIRoundedRectangle(4f) {

	val colorState: BasicState<Color> = BasicState(Color(0, 0, 0, 100))

	init {
		val config: Config = LibrarianTradeFinder.getInstance().config

		constrain {
			x = 0.pixels()
			y = SiblingConstraint(padding = 2f)

			color = colorState.toConstraint()

			width = 100.percent()
			height = 20.pixels()
		}

		val enchantment: Enchantment = RegistryHelper.getEnchantmentById(entry.key)

		val name by UIText(enchantment.description().string).constrain {
			x = 5.pixels()
			y = CenterConstraint()

			color = Color.WHITE.toConstraint()
		} childOf this

		val emeraldsSlider = SliderComponent().constrain {
			x = 5.pixels(alignOpposite = true)
			y = CenterConstraint()

			width = 40.pixels()
			height = 14.pixels()
		} childOf this

		withHandCursor()

		fun updateEntry(toggle: Boolean) {
			if (toggle) {
				config.enableEnchantment(entry.key, !config.isEnchantmentEnabled(entry.key))
				LibrarianTradeFinder.getInstance().configManager.save()
			}

			colorState.set {
				if (config.isEnchantmentEnabled(entry.key)) {
					Color(56, 171, 80, 150)
				} else {
					Color(0, 0, 0, 100)
				}
			}
			if (config.isEnchantmentEnabled(entry.key)) {
				emeraldsSlider.unhide(true)
			} else {
				emeraldsSlider.hide(false)
			}
		}

		onMouseClick {
			updateEntry(true)
		}

		updateEntry(false)
	}



}
