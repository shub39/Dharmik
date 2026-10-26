/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.room)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.ksp)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-sensitive-resolution")
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    jvm()

    android {
        namespace = "com.shub39.dharmik.shared.logic"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        androidResources { enable = true }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.shared.core)

            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)

            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines)

            implementation(libs.ktor.client.core)
            implementation(libs.okio)

            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.androidx.datastore.preferences.core)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.koin.annotations)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.session)
        }
        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.cio)
        }
    }
}

dependencies {
    add("kspJvm", libs.androidx.room.compiler)
    add("kspAndroid", libs.androidx.room.compiler)
}

room3 { schemaDirectory("$projectDir/schemas") }

// ---- Gita text pipeline ----------------------------------------------------
// The chapter JSONs live in the repo-root `gita/text` folder. They are generated into Compose
// resources for the build and deleted again afterwards, so the source tree stays clean.
val gitaTextDir = layout.projectDirectory.dir("src/commonMain/composeResources/files/bhagvad_gita")

val copyGitaTexts =
    tasks.register<Copy>("CopyGitaTexts") {
        description = "Copy Gita chapter JSONs from repo-root gita/text into Compose resources"
        group = "build"
        from(rootProject.file("gita/text"))
        into(gitaTextDir)
        include("*.json")
    }

val deleteGitaTexts =
    tasks.register<Delete>("DeleteGitaTexts") {
        description = "Remove the Gita chapter JSONs generated into Compose resources"
        group = "build"
        delete(gitaTextDir)
    }

// Make sure the JSONs exist before anything reads/processes Compose resources.
tasks
    .matching {
        it.name.contains("NonXmlValueResources") ||
            it.name.contains("ComposeResources") ||
            it.name.startsWith("generateComposeResClass") ||
            it.name.startsWith("prepareComposeResources")
    }
    .configureEach { dependsOn(copyGitaTexts) }

// Clean up the generated copies once the build is done (and on `clean`).
tasks.named("build") { finalizedBy(deleteGitaTexts) }

tasks.named("clean") { dependsOn(deleteGitaTexts) }

tasks.register("generateChangelog") {
    description = "Extract changelogs from CHANGELOG.md"
    group = "build"
    val inputFile = rootProject.file("CHANGELOG.md")
    val outputDir = file("$projectDir/src/commonMain/composeResources/files/")
    val outputFile = File(outputDir, "changelog.json")

    inputs.file(inputFile)
    outputs.file(outputFile)

    doLast {
        if (!outputDir.exists()) outputDir.mkdirs()

        val lines = inputFile.readLines()

        val map = mutableMapOf<String, MutableList<String>>()
        var currentVersion: String? = null

        for (line in lines) {
            when {
                line.startsWith("## ") -> {
                    currentVersion = line.removePrefix("## ").trim()
                    map[currentVersion] = mutableListOf()
                }

                line.startsWith("- ") && currentVersion != null -> {
                    map[currentVersion]?.add(line.removePrefix("- ").trim())
                }
            }
        }

        val json = buildString {
            append("[\n")

            val limitedEntries = map.entries.take(10)
            limitedEntries.forEachIndexed { index, entry ->
                append("  {\n")
                append("    \"version\": \"${entry.key}\",\n")
                append("    \"changes\": [\n")

                entry.value.forEachIndexed { i, item ->
                    append("      \"${item.replace("\"", "\\\"")}\"")
                    if (i != entry.value.lastIndex) append(",")
                    append("\n")
                }

                append("    ]\n")
                append("  }")

                if (index != limitedEntries.lastIndex) append(",")
                append("\n")
            }

            append("]")
        }

        outputFile.writeText(json)
    }
}

tasks.named("copyNonXmlValueResourcesForCommonMain") { dependsOn("generateChangelog") }
