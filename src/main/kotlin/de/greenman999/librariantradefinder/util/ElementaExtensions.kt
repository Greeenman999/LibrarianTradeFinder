package de.greenman999.librariantradefinder.util

import com.mojang.blaze3d.platform.cursor.CursorType
import com.mojang.blaze3d.platform.cursor.CursorTypes
import gg.essential.elementa.UIComponent
import net.minecraft.client.Minecraft

fun UIComponent.withHandCursor(): UIComponent {
	onMouseEnter {
		CursorTypes.POINTING_HAND.select(Minecraft.getInstance().window)
	}

	onMouseLeave {
		CursorType.DEFAULT.select(Minecraft.getInstance().window)
	}

	return this
}
