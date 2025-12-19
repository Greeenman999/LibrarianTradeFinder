/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 rotgruengelb, and stonecutter-mod-template contributors
 * Copyright (c) 2025 murder_spagurder
 * See the LICENSE file in the project root for license terms.
 */

package com.example.modtemplate.platform.fabric.datagen;

//? fabric {
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
//? != 1.19.2 {
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
//?}

public class FabricDataGeneratorEntrypoint implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		//? != 1.19.2 {
		final FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider((FabricDataOutput output) -> new ModRecipeProvider(output, generator.getRegistries()));
		//?}
	}

}
//?}
