/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 rotgruengelb, and stonecutter-mod-template contributors
 * Copyright (c) 2025 murder_spagurder
 * See the LICENSE file in the project root for license terms.
 */

package com.example.modtemplate.platform.fabric.datagen;

//? fabric && != 1.19.2 {

import com.example.modtemplate.ModTemplate;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {

	private final CompletableFuture<HolderLookup.Provider> registriesFuture;

	public ModRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
		this.registriesFuture = registriesFuture;
	}

	//? if 1.21.1 {
	/*@Override
	public void buildRecipes(RecipeOutput recipeOutput) {
		IntRecipeProvider provider = new IntRecipeProvider(this.output, this.registriesFuture);
		provider.buildRecipes(recipeOutput);
	}
	*///? } else {
	@Override
	protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
		return new IntRecipeProvider(provider, recipeOutput);
	}
	//? }


	@Override
	public @NotNull String getName() {
		return ModTemplate.MOD_ID + ":recipe_provider";
	}

	static class IntRecipeProvider extends RecipeProvider {

		//? if 1.21.1 {
		/*public IntRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> completableFuture) {
			super(packOutput, completableFuture);
		}

		@Override
		public void buildRecipes(RecipeOutput recipeOutput) {
			buildLavaChickenRecipe(ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.COOKED_CHICKEN))
					.save(recipeOutput, "lava_chicken_recipe");
		}
		*///? } else {
		protected IntRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
			super(provider, recipeOutput);
		}

		@Override
		public void buildRecipes() {
			final var itemLookup = registries.lookupOrThrow(Registries.ITEM);
			buildLavaChickenRecipe(ShapelessRecipeBuilder.shapeless(itemLookup, RecipeCategory.FOOD, Items.COOKED_CHICKEN))
					.save(output, "lava_chicken_recipe");
		}
		//? }

		private ShapelessRecipeBuilder buildLavaChickenRecipe(ShapelessRecipeBuilder builder) {
			return builder.requires(Items.LAVA_BUCKET)
					.requires(Items.CHICKEN)
					.unlockedBy("has_lava_bucket", has(Items.LAVA_BUCKET))
					.unlockedBy("has_chicken", has(Items.CHICKEN));
		}
	}
}
//?}
