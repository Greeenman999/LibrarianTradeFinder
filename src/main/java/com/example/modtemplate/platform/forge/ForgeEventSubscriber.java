/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 rotgruengelb, and stonecutter-mod-template contributors
 * See the LICENSE file in the project root for license terms.
 */

package com.example.modtemplate.platform.forge;

//? forge {

/*import com.example.modtemplate.event.ExampleEventHandler; // sample_content
import net.minecraft.server.level.ServerPlayer; // sample_content
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ForgeEventSubscriber {

	@SubscribeEvent // sample_content
	public static void onPlayerDamage(LivingDamageEvent event) { // sample_content
		if (event.getEntity() instanceof ServerPlayer player && event.getAmount() > 0) { // sample_content
			ExampleEventHandler.onPlayerHurt(player); // sample_content
		} // sample_content
	} // sample_content
}
*///?}
