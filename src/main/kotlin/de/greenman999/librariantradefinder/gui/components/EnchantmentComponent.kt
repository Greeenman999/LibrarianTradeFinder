package de.greenman999.librariantradefinder.gui.components

import de.greenman999.librariantradefinder.LibrarianTradeFinder
import de.greenman999.librariantradefinder.config.Config
import de.greenman999.librariantradefinder.util.RegistryHelper
import de.greenman999.librariantradefinder.util.translatable
import de.greenman999.librariantradefinder.util.withHandCursor
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.minus
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
		fun maxEmeraldCost(level: Int? = entry.value.minLevel) = RegistryHelper.getMaxEmeraldCost(entry.key, level)
		fun minEmeraldCost(level: Int? = entry.value.minLevel) = RegistryHelper.getMinEmeraldCost(entry.key, level)
		fun denormalize(value: Float, min: Int, max: Int) = (value * (max - min)) + min
		fun normalize(value: Int, min: Int, max: Int) = if (min == max) {
			min.toFloat()
		} else {
			(value - min).toFloat() / (max - min).toFloat()
		}
		fun calculateEmeraldsFromValue(value: Float) = denormalize(value, minEmeraldCost(), maxEmeraldCost())
		fun normalizeEmeralds(emeralds: Int) = normalize(emeralds, minEmeraldCost(), maxEmeraldCost())
		fun calculateLevelFromValue(value: Float) = denormalize(value, enchantment.minLevel, enchantment.maxLevel).toInt()
		fun normalizeLevel(level: Int) = normalize(level, enchantment.minLevel, enchantment.maxLevel)

		val name by UIText(enchantment.description().string).constrain {
			x = 5.pixels()
			y = CenterConstraint()

			color = Color.WHITE.toConstraint()
		} childOf this

		val emeraldsSlider = SliderComponent(normalizeEmeralds(entry.value.maxPrice)).constrain {
			x = 5.pixels(alignOpposite = true)
			y = CenterConstraint()

			width = 50.pixels()
			height = 14.pixels()
		}.onValueSave {
			if (!config.isEnchantmentEnabled(entry.key)) return@onValueSave

			val newValue = calculateEmeraldsFromValue(it).toInt()
			entry.value.maxPrice = newValue

			config.setEnchantmentMaxPrice(entry.key, newValue)
			LibrarianTradeFinder.getInstance().configManager.save()
		}.formatText { value ->
			(calculateEmeraldsFromValue(value)).toInt().toString()
		} childOf this

		val emeraldTooltip by TooltipComponent(emeraldsSlider)
			.bindVisibility(emeraldsSlider)
			.bindText(BasicState(translatable("librariantradefinder.gui.tooltip.max-price")))

		val levelSlider = SliderComponent(normalizeLevel(entry.value.minLevel)).constrain {
			x = 5.pixels(alignOpposite = true) - 50.pixels() - 5.pixels()
			y = CenterConstraint()

			width = 40.pixels()
			height = 14.pixels()
		}.onValueChange {
			if (!config.isEnchantmentEnabled(entry.key)) return@onValueChange

			val newLevel = calculateLevelFromValue(it)
			entry.value.minLevel = newLevel
			// Update emeralds slider in case min level change affected costs
			if (entry.value.maxPrice < minEmeraldCost(newLevel)) {
				entry.value.maxPrice = minEmeraldCost(newLevel)
			} else if (entry.value.maxPrice > maxEmeraldCost(newLevel)) {
				entry.value.maxPrice = maxEmeraldCost(newLevel)
			}

			emeraldsSlider.updateSliderValue(normalizeEmeralds(entry.value.maxPrice))
		}.onValueSave {
			if (!config.isEnchantmentEnabled(entry.key)) return@onValueSave

			config.setEnchantmentMinLevel(entry.key, entry.value.minLevel)
			LibrarianTradeFinder.getInstance().configManager.save()
		}.formatText {
			when (calculateLevelFromValue(it)) {
				1 -> "I"
				2 -> "II"
				3 -> "III"
				4 -> "IV"
				5 -> "V"
				6 -> "VI"
				7 -> "VII"
				8 -> "VIII"
				9 -> "IX"
				10 -> "X"
				else -> calculateLevelFromValue(it).toString()
			}
 		} childOf this

		val levelTooltip by TooltipComponent(levelSlider)
			.bindVisibility(levelSlider)
			.bindText(BasicState(translatable("librariantradefinder.gui.tooltip.level")))

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
				levelSlider.unhide(true)
			} else {
				emeraldsSlider.hide(false)
				levelSlider.hide(false)
			}
		}

		onMouseClick {
			updateEntry(true)
		}

		updateEntry(false)
	}



}
