package de.greenman999.librariantradefinder.gui

import de.greenman999.librariantradefinder.LibrarianTradeFinder
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

class ConfigScreen(val parent: Screen? = null) : WindowScreen(ElementaVersion.V10, true, true, true) {

	init {
		UIText(translatable("librariantradefinder.gui.title")).constrain {
			x = CenterConstraint()
			y = 10.pixels()
		} childOf window

		UIWrappedText(LibrarianTradeFinder.getInstance().config.enchantments.map {
			it.key
		}.joinToString(", ")).constrain {
			x = CenterConstraint()
			y = 30.pixels()
			width = 90.percent()
		} childOf window

		UIWrappedText(LibrarianTradeFinder.getInstance().config.enchantmentsRaw.map {
			it.key
		}.joinToString(", ")).constrain {
			x = CenterConstraint()
			y = 60.pixels()
			width = 90.percent()
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
