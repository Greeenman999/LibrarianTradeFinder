/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 rotgruengelb, and stonecutter-mod-template contributors
 * Copyright (c) 2025 murder_spagurder
 * Copyright (c) 2025 Greenman999
 * See the LICENSE file in the project root for license terms.
 */

plugins {
	id("mod-platform")
	id("net.neoforged.moddev")
}

platform {
	loader = "neoforge"
	dependencies {
		required("minecraft") {
			forgeVersionRange = "[${prop("deps.minecraft")}]"
		}
		required("neoforge") {
			forgeVersionRange = "[1,)"
		}
		required("kotlinforforge") {
			forgeVersionRange = "[${prop("deps.kotlinforforge")},)"
			slug("kotlin-for-forge")
		}
	}
}

neoForge {
	version = property("deps.neoforge") as String
	accessTransformers.from(rootProject.file("src/main/resources/aw/${stonecutter.current.version}.cfg"))
	validateAccessTransformers = true

	if (hasProperty("deps.parchment")) parchment {
		val (mc, ver) = (property("deps.parchment") as String).split(':')
		mappingsVersion = ver
		minecraftVersion = mc
	}

	runs {
		register("client") {
			client()
			gameDirectory = file("run/")
			ideName = "NeoForge Client (${stonecutter.current.version})"
			programArgument("--username=Dev")
		}
		register("server") {
			server()
			gameDirectory = file("run/")
			ideName = "NeoForge Server (${stonecutter.current.version})"
		}
	}

	mods {
		register(property("mod.id") as String) {
			sourceSet(sourceSets["main"])
		}
	}
	sourceSets["main"].resources.srcDir("${rootDir}/versions/datagen/${stonecutter.current.version.split("-")[0]}/src/main/generated")
}

repositories {
	maven("https://repo.essential.gg/repository/maven-public") { name = "Essential" }
	maven("https://thedarkcolour.github.io/KotlinForForge/") { name = "KotlinForForge" }
	maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
}

dependencies {
	implementation(libs.moulberry.mixinconstraints)
	jarJar(libs.moulberry.mixinconstraints)

	jarJar("gg.essential:elementa") {
		version {
			strictly("[${prop("deps.elementa")},)")
			prefer(prop("deps.elementa"))
		}
	}
	jarJar("gg.essential:universalcraft-${prop("deps.universalcraft-mc")}-neoforge") {
		version {
			strictly("[${prop("deps.universalcraft")},)")
			prefer(prop("deps.universalcraft"))
		}
	}

	implementation("thedarkcolour:kotlinforforge-neoforge:${property("deps.kotlinforforge")}")
	implementation("org.spongepowered:configurate-gson:${prop("deps.configurate")}")
}

tasks.named("createMinecraftArtifacts") {
	dependsOn(tasks.named("stonecutterGenerate"))
}
