package de.greenman999.librariantradefinder.util

import net.minecraft.network.chat.Component

fun translatable(key: String, argument: Component? = null): String {
	return argument?.let { Component.translatable(key, it) }?.string ?: Component.translatable(key).string
}
