/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 rotgruengelb, and stonecutter-mod-template contributors
 * See the LICENSE file in the project root for license terms.
 */

package com.example.modtemplate.platform.neoforge;

//? neoforge {

/*import com.example.modtemplate.event.ExampleEventHandler; // sample_content
import net.minecraft.server.level.ServerPlayer; // sample_content
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber
public class NeoforgeEventSubscriber {

	@SubscribeEvent // sample_content
	public static void onPlayerDamage(LivingDamageEvent.Post event) { // sample_content
		if (event.getEntity() instanceof ServerPlayer player && event.getNewDamage() > 0) { // sample_content
			ExampleEventHandler.onPlayerHurt(player); // sample_content
		} // sample_content
	} // sample_content
}
*///?}
