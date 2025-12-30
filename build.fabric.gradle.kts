/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 rotgruengelb, and stonecutter-mod-template contributors
 * Copyright (c) 2025 murder_spagurder
 * Copyright (c) 2025 Greenman999
 * See the LICENSE file in the project root for license terms.
 */

plugins {
	id("mod-platform")
	id("fabric-loom")
}

platform {
	loader = "fabric"
	dependencies {
		required("minecraft") {
			versionRange = prop("deps.minecraft")
		}
		required("fabric-api") {
			slug("fabric-api")
			versionRange = ">=${prop("deps.fabric-api")}"
		}
		required("fabricloader") {
			versionRange = ">=${libs.fabric.loader.get().version}"
		}
		required("fabric-language-kotlin") {
			slug("fabric-kotlin-language")
			versionRange = ">=${prop("deps.fabric-kotlin")}"
		}
		optional("modmenu") {}
	}
}

loom {
	accessWidenerPath = rootProject.file("src/main/resources/aw/${stonecutter.current.version}.accesswidener")
	runs.named("client") {
		client()
		ideConfigGenerated(true)
		runDir = "run/"
		environment = "client"
		programArgs("--username=Dev")
		configName = "Fabric Client"
	}
	runs.named("server") {
		server()
		ideConfigGenerated(true)
		runDir = "run/"
		environment = "server"
		configName = "Fabric Server"
	}
}

fabricApi {
	configureDataGeneration() {
		outputDirectory = file("${rootDir}/versions/datagen/${stonecutter.current.version.split("-")[0]}/src/main/generated")
		client = true
	}
}

repositories {
	maven("https://repo.essential.gg/repository/maven-public") { name = "Essential" }
	maven("https://maven.terraformersmc.com/") { name = "TerraformersMC" }
	maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
}

dependencies {
	minecraft("com.mojang:minecraft:${prop("deps.minecraft")}")
	mappings(
		loom.layered {
			officialMojangMappings()
			if (hasProperty("deps.parchment")) parchment("org.parchmentmc.data:parchment-${prop("deps.parchment")}@zip")
		})
	modImplementation(libs.fabric.loader)
	implementation(libs.moulberry.mixinconstraints)
	include(libs.moulberry.mixinconstraints)
	modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric-api")}")
	modImplementation("net.fabricmc:fabric-language-kotlin:${prop("deps.fabric-kotlin")}")

	implementation(include("gg.essential:elementa:${prop("deps.elementa")}")!!)
	modImplementation(include("gg.essential:universalcraft-${prop("deps.universalcraft-mc")}-fabric:${prop("deps.universalcraft")}")!!)
	modImplementation("com.terraformersmc:modmenu:${prop("deps.modmenu")}")

	implementation("org.spongepowered:configurate-gson:${prop("deps.configurate")}")

	implementation("org.apache.commons:commons-text:${prop("deps.commons-text")}")
}
