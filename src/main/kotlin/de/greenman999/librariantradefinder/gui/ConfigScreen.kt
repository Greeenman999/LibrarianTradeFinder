package de.greenman999.librariantradefinder.gui

import de.greenman999.librariantradefinder.LibrarianTradeFinder
import de.greenman999.librariantradefinder.gui.components.EnchantmentComponent
import de.greenman999.librariantradefinder.util.RegistryHelper
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.CoerceAtMostConstraint
import gg.essential.elementa.constraints.CramSiblingConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.constraints.MaxConstraint
import gg.essential.elementa.constraints.RelativeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.div
import gg.essential.elementa.dsl.effect
import gg.essential.elementa.dsl.minus
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.plus
import gg.essential.elementa.dsl.provideDelegate
import gg.essential.elementa.dsl.times
import gg.essential.elementa.dsl.toConstraint
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.state.toConstraint
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.Holder
import net.minecraft.network.chat.Component
import net.minecraft.world.item.enchantment.Enchantment
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

		val leftSide by UIContainer().constrain {
			x = 0.pixels()
			y = 0.pixels()

			width = 50.percent() - paddingXL / 2
			height = 100.percent()
		} childOf container //effect OutlineEffect(Color.RED, 1f)

		val rightSide by UIContainer().constrain {
			x = 0.pixels(alignOpposite = true)
			y = 0.pixels()

			width = 50.percent() - paddingXL / 2
			height = 100.percent()
		} childOf container //effect OutlineEffect(Color.BLUE, 1f)

		val enchantments by ScrollComponent(translatable("librariantradefinder.gui.empty-enchantments")).constrain {
			x = 0.pixels()
			y = 0.pixels()

			width = FillConstraint()
			height = RelativeConstraint(1f)
		} childOf leftSide

		val scrollbar by UIRoundedRectangle(2f).constrain {
			x = 0.pixels(alignOpposite = true)

			width = 4.pixels()

			color = Color(0, 0, 0, 100).toConstraint()
		} childOf leftSide
		enchantments.setScrollBarComponent(scrollbar, hideWhenUseless = true, isHorizontal = false)

		enchantments.emptyText.setWidth(enchantments.getWidth().pixels() - 10.pixels())
		enchantments.emptyText.setY(CenterConstraint())

		for (entry in LibrarianTradeFinder.getInstance().config.enchantments) {
			EnchantmentComponent(entry) childOf enchantments
		}

		Inspector(window).constrain {
			x = 10.pixels(true)
			y = 10.pixels(true)
		} childOf window
	}

	fun translatable(key: String, argument: Component? = null): String {
		return argument?.let { Component.translatable(key, it) }?.string ?: Component.translatable(key).string
	}

	override fun onClose() {
		super.onClose()
		parent?.let { Minecraft.getInstance().setScreen(it) }
	}
}
