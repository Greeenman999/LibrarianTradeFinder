/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2025 rotgruengelb, and stonecutter-mod-template contributors
 * See the LICENSE file in the project root for license terms.
 */

@file:Suppress("unused")

import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.compile.JavaCompile
import javax.inject.Inject

fun NamedDomainObjectContainer<Dependency>.add(modid: String, configure: Action<Dependency>) {
	create(modid, configure)
}

fun NamedDomainObjectContainer<Dependency>.add(
	modid: String, modrinthSlug: String?, curseforgeSlug: String?
) {
	create(modid) {
		if (modrinthSlug != null) modrinth.set(modrinthSlug)
		if (curseforgeSlug != null) curseforge.set(curseforgeSlug)
	}
}


interface ModPlatformExtension {
	val requiredJava: Property<JavaVersion>
	val loader: Property<String>
	val jarTask: Property<String>
	val sourcesJarTask: Property<String>
	val dependencies: DependenciesConfig

	fun dependencies(action: Action<DependenciesConfig>)
}

interface DependenciesConfig {
	val required: NamedDomainObjectContainer<Dependency>
	val optional: NamedDomainObjectContainer<Dependency>
	val incompatible: NamedDomainObjectContainer<Dependency>
	val embeds: NamedDomainObjectContainer<Dependency>

	fun required(modid: String, action: Action<Dependency>)
	fun optional(modid: String, action: Action<Dependency>)
	fun incompatible(modid: String, action: Action<Dependency>)
	fun embeds(modid: String, action: Action<Dependency>)
}

interface Dependency {
	val modid: Property<String>
	val modrinth: Property<String>
	val curseforge: Property<String>
	val versionRange: Property<String>
	val forgeVersionRange: Property<String>
	val environment: Property<String>

	fun slug(modrinthSlug: String?, curseforgeSlug: String? = modrinthSlug)
	fun slug(modrinthAndCurseforgeSlug: String)
	fun slugModrinth(value: String)
	fun slugCurseforge(value: String)
}

abstract class DependencyImpl @Inject constructor(
	val name: String
) : Dependency {

	@get:Inject
	abstract val objects: ObjectFactory

	override val modid: Property<String> = objects.property(String::class.java).convention(name)
	override val modrinth: Property<String> = objects.property(String::class.java)
	override val curseforge: Property<String> = objects.property(String::class.java)
	override val versionRange: Property<String> = objects.property(String::class.java).convention("*")
	override val forgeVersionRange: Property<String> = objects.property(String::class.java).convention("(,]")
	override val environment: Property<String> = objects.property(String::class.java).convention("both")

	override fun slug(modrinthSlug: String?, curseforgeSlug: String?) {
		if (modrinthSlug != null) modrinth.set(modrinthSlug)
		if (curseforgeSlug != null) curseforge.set(curseforgeSlug)
	}

	override fun slug(modrinthAndCurseforgeSlug: String) {
		modrinth.set(modrinthAndCurseforgeSlug)
		curseforge.set(modrinthAndCurseforgeSlug)
	}

	override fun slugModrinth(value: String) {
		modrinth.set(value)
	}

	override fun slugCurseforge(value: String) {
		curseforge.set(value)
	}
}

abstract class ModPlatformExtensionImpl @Inject constructor(project: Project) : ModPlatformExtension {
	private val objects = project.objects
	override val requiredJava: Property<JavaVersion> = objects.property(JavaVersion::class.java).convention(JavaVersion.VERSION_21)
	override val loader: Property<String> = objects.property(String::class.java)
	override val jarTask: Property<String> = objects.property(String::class.java)
	override val sourcesJarTask: Property<String> = objects.property(String::class.java)
	override val dependencies: DependenciesConfig = objects.newInstance(DependenciesConfigImpl::class.java, project)
	override fun dependencies(action: Action<DependenciesConfig>) = action.execute(dependencies)
}

@Suppress("UNCHECKED_CAST")
abstract class DependenciesConfigImpl @Inject constructor(project: Project) : DependenciesConfig {
	private val objects = project.objects

	override val required: NamedDomainObjectContainer<Dependency> =
		project.container(DependencyImpl::class.java) as NamedDomainObjectContainer<Dependency>
	override val optional: NamedDomainObjectContainer<Dependency> =
		project.container(DependencyImpl::class.java) as NamedDomainObjectContainer<Dependency>
	override val incompatible: NamedDomainObjectContainer<Dependency> =
		project.container(DependencyImpl::class.java) as NamedDomainObjectContainer<Dependency>
	override val embeds: NamedDomainObjectContainer<Dependency> =
		project.container(DependencyImpl::class.java) as NamedDomainObjectContainer<Dependency>

	override fun required(modid: String, action: Action<Dependency>) = required.add(modid, action)
	override fun optional(modid: String, action: Action<Dependency>) = optional.add(modid, action)
	override fun incompatible(modid: String, action: Action<Dependency>) = incompatible.add(modid, action)
	override fun embeds(modid: String, action: Action<Dependency>) = embeds.add(modid, action)
}
