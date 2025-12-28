package de.greenman999.librariantradefinder.gui

import de.greenman999.librariantradefinder.gui.columns.EnchantmentColumn
import de.greenman999.librariantradefinder.gui.columns.OptionsColumn
import de.greenman999.librariantradefinder.util.translatable
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.CoerceAtMostConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.div
import gg.essential.elementa.dsl.minus
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.plus
import gg.essential.elementa.dsl.provideDelegate
import gg.essential.elementa.dsl.times
import gg.essential.elementa.dsl.toConstraint
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
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

		val enchantmentColumn by EnchantmentColumn().constrain {
			x = 0.pixels()
			y = 0.pixels()

			width = 50.percent() - paddingXL / 2
			height = 100.percent()
		} childOf container //effect OutlineEffect(Color.GREEN, 1f)

		val optionsColumn by OptionsColumn().constrain {
			x = 0.pixels(alignOpposite = true)
			y = 0.pixels()

			width = 50.percent() - paddingXL / 2
			height = 100.percent()
		} childOf container //effect OutlineEffect(Color.BLUE, 1f)

		Inspector(window).constrain {
			x = 10.pixels(true)
			y = 10.pixels(true)
		} childOf window
	}

	override fun onClose() {
		super.onClose()
		parent?.let { Minecraft.getInstance().setScreen(it) }
	}
}
