# Stonecutter Mod Template

A multi-platform Minecraft mod template for **Fabric** and **NeoForge**,
using [Stonecutter](https://stonecutter.kikugie.dev/) for
multiversion and multiloader code.
This is the Java-only version adapted from KikuGie's Elytra Trims
rewrite following major Stonecutter feature updates.

This template is as "batteries included" as possible.
If you don't like this, it's not the right template for
you ([Alternative Templates](https://stonecutter.kikugie.dev/wiki/tips/multiloader)).

## Features

* Single codebase for both Fabric and NeoForge
* Single codebase for multiple Minecraft versions
* CI/CD with GitHub Actions for automated builds and releases
* Separate build scripts for each platform

## Getting Started

### Prerequisites

* Knowledge of Fabric and NeoForge
* Suitable IDE
* Java 21 or higher
* Git

### Initial Setup

#### 1. **Clone or use this template**

```bash
git clone https://github.com/Greeenman999/stonecutter-mod-template.git
cd stonecutter-mod-template
```

#### 2. **Open in your IDE**

Import the project as a Gradle project
in your preferred IDE (e.g., IntelliJ IDEA, Eclipse).

#### 3. **Stonecutter IntelliJ plugin**

The IntelliJ plugin adds comment syntax highlighting and completion,
a button to switch the active version, alongside other utilities.

#### 4. **Configure your mod**

Edit `gradle.properties` to set your mod's metadata:

| Property           | Description                                  | Example                                                           |
|--------------------|----------------------------------------------|-------------------------------------------------------------------|
| `mod.id`           | Your mod’s identifier (lowercase, no spaces) | `modtemplate`                                                     |
| `mod.name`         | Display name of your mod                     | `Mod Template`                                                    |
| `mod.group`        | Java package group                           | `com.example`                                                     |
| `mod.version`      | Mod version number                           | `0.1.0`                                                           |
| `mod.channel_tag`  | Optional release channel tag                 | `-alpha.0`                                                        |
| `mod.authors`      | Name of the author(s), comma-separated       | `AuthorName`                                                      |
| `mod.contributors` | Contributor names, comma-separated           | `ContributorName, AnotherContributorName`                         |
| `mod.license`      | License type                                 | `MIT`                                                             |
| `mod.description`  | Short mod description                        | `Example Description`                                             |
| `mod.sources_url`  | Link to your source code repository          | `https://github.com/Greeenman999/stonecutter-mod-template`        |
| `mod.homepage_url` | Mod homepage or info page                    | `https://github.com/Greeenman999/stonecutter-mod-template`        |
| `mod.issues_url`   | Link to issue tracker                        | `https://github.com/Greeenman999/stonecutter-mod-template/issues` |
| `mod.discord_url`  | Link to a Discord invite                     | `https://discord.gg/aunYJB4wz9`                                   |

Dependencies/Properties that are specific to a version/loader 
are defined in `gradle.properties` as `[VERSIONED]` then set in `versions/{version}-{loader}/gradle.properties`.

#### 5. **Rename package structure**

Rename the `com.example.modtemplate` package in
`src/main/java/` to match your `mod.group` and `mod.id`.

#### 6. **Update resource files**

Rename these files to match your `mod.id`:

* `src/main/resources/modtemplate.accesswidener`
* `src/main/resources/modtemplate.mixins.json`

Replace and `src/main/resources/assets/icon.png` and `.idea/icon.png` with the mods icon.

#### 7. Configure git-cliff

Run `sed -i "s|%%%repo_url%%%|YOUR_REPO_URL|g" cliff.toml` to set your repository URL for changelog generation.

## Development

### Stonecutter

[Stonecutter](https://stonecutter.kikugie.dev/) allows multiple Minecraft versions and loaders in a single codebase.
Configure Stonecutter in `stonecutter.gradle.kts` and `settings.gradle.kts`.

Example of platform-specific code using Stonecutter comments:

```java
//? if fabric {
/*fabricOnlyCode();*/
//?} else {
neoforgeOnlyCode();
//?}
```
Verson-specific code works similarly:

```java
//? if 1.21.7 {
LOGGER.info("hello 1.21.7!");
//?} else {
/*LOGGER.info("hello from any other version!");
 *///?}
```

For more details, read the [Stonecutter documentation](https://stonecutter.kikugie.dev/wiki/).

### Running in Development

The Gradle plugins of the respective platform should provide run configurations.
If not, you can run the server and client with the respective Gradle tasks.
Be careful to run the correct task for the selected Stonecutter platform and Minecraft version.

### Platform Abstraction

The template uses a platform abstraction pattern to keep shared code loader-agnostic:

* **Shared code** goes in `com.example.modtemplate` (no platform dependencies)
* **Platform-specific code** goes in `com.example.modtemplate.platform.{fabric|neoforge}`
* The `Platform` interface provides loader-specific functionality to shared code

### Adding Dependencies

To add dependencies for a specific platform, modify the `platform` block in the respective `build.gradle.kts` file.
The declared dependencies are automatically added to the metadata file for the loader and when publishing the mod to
mod hosting platforms.
**Important:** This does not replace the `dependencies` block!

```kotlin
platform {
  loader = "fabric" // or "neoforge"
  dependencies {
    required("my-lib") {
      slug("my-lib") // Mod hosting platform slug (here the slug is the same on both Modrinth and CurseForge)
      versionRange = ">=${prop("deps.my-lib")}" // version range (for fabric.mod.json)
      forgeVersionRange =
        "[${prop("deps.my-lib")},)" // version range (for neoforge mods.toml), uses Maven version range syntax
    }
  }
}
```

### Data Generation

Run Fabric data generation to create recipes, tags, and other data:

```bash
./gradlew :1.21.7-fabric:runDatagen
```

Generated files appear in `src/main/generated/`.
The current setup uses Fabric data generation for both platforms.

## Resouerces and Links
- [Stonecutter Documentation](https://stonecutter.kikugie.dev/wiki/)
- [NeoForge Documentation](https://docs.neoforged.net/docs/gettingstarted/)
- [Fabric Documentation](https://docs.fabricmc.net/develop/)
- [Pre-commit](https://pre-commit.com/)
- [Git Source Control](https://git-scm.com/doc)
- [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)
- [Semantic Versioning](https://semver.org/)
  - [How to denote a pre-release version](https://semver.org/#spec-item-9)
- [Your Modrinth PAT](https://modrinth.com/settings/pats)
- [Your CurseForge API Tokens](https://legacy.curseforge.com/account/api-tokens)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Gradle Documentation](https://docs.gradle.org/current/userguide/userguide.html)

### Help and Support
For help and support, consider the following places:
- ["Kiku's Realm" Discord Server](https://discord.kikugie.dev/) for Stonecutter-related questions.
- ["The NeoForge Project" Discord Server](https://github.com/neoforged) for NeoForge-related questions.
- ["The Fabric Project" Discord Server](https://discord.gg/v6v4pMv) for Fabric-related questions.

## License/Credits

This template is provided under the MIT License.
Check `LICENSE` for details.

* Based on [rotgruengelb/stonecutter-mod-template](https://github.com/rotgruengelb/stonecutter-mod-template)
  * Based on [murderspagurder/mod-template-java](https://github.com/murderspagurder/mod-template-java)
    * Adapted from [KikuGie's Elytra Trims](https://github.com/kikugie/elytra-trims) setup
* Uses [Stonecutter](https://stonecutter.kikugie.dev/) by KikuGie
