package de.greenman999.librariantradefinder.gui.components

import de.greenman999.librariantradefinder.LibrarianTradeFinder
import de.greenman999.librariantradefinder.config.Config
import de.greenman999.librariantradefinder.config.ConfigManager
import de.greenman999.librariantradefinder.gui.Palette
import de.greenman999.librariantradefinder.util.RegistryHelper
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.input.UITextInput
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.effect
import gg.essential.elementa.dsl.minus
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.provideDelegate
import gg.essential.elementa.dsl.toConstraint
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.toConstraint
import net.minecraft.resources.Identifier
import net.minecraft.world.item.enchantment.Enchantment
import java.awt.Color

class EnchantmentComponent(val entry: MutableMap.MutableEntry<Identifier, Config.EnchantmentEntry>) : UIRoundedRectangle(4f) {

	val colorState: BasicState<Color> = if (entry.value.isEnabled) {
		BasicState(Color(56, 171, 80, 150))
	} else {
		BasicState(Color(0, 0, 0, 100))
	}

	init {
		val config = LibrarianTradeFinder.getInstance().config
	    constrain {
			x = 0.pixels()
			y = SiblingConstraint(padding = 2f)

			color = colorState.toConstraint()

			width = 100.percent() - 4.pixels()
			height = 20.pixels()
		}

		val enchantment: Enchantment = RegistryHelper.getEnchantmentById(entry.key)

		val name by UIText(enchantment.description().string).constrain {
			x = 5.pixels()
			y = CenterConstraint()

			color = Color.WHITE.toConstraint()
		} childOf this

		val levelBox by UIRoundedRectangle(4f).constrain {
			x = 0.pixels(alignOpposite = true)
			y = CenterConstraint()

			width = 50.pixels()
			height = 14.pixels()

			color = Color(50, 50, 50, 0).toConstraint()
		} childOf this effect OutlineEffect(Color(50, 50, 50), 1f)

		val level by UITextInput("Max Level").constrain {
			x = 2.pixels()
			y = CenterConstraint()

			width = 100.percent() - 4.pixels()
		} childOf levelBox

		levelBox.onMouseClick {
			level.grabWindowFocus()
		}

		if (!entry.value.isEnabled) {
			levelBox.hide(true)
		}

		onMouseClick {
			if (levelBox.isPointInside(it.absoluteX, it.absoluteY)) {
				return@onMouseClick
			}
			config.enableEnchantment(entry.key, !config.isEnchantmentEnabled(entry.key))
			colorState.set {
				if (config.isEnchantmentEnabled(entry.key)) {
					val success = Palette.success.get()
					Color(success.red, success.green, success.blue, 100)
				} else {
					Color(0, 0, 0, 100)
				}
			}
			if (config.isEnchantmentEnabled(entry.key)) {
				levelBox.unhide(true)
			} else {
				levelBox.hide(true)
			}
			LibrarianTradeFinder.getInstance().configManager.save()
		}
	}

}
