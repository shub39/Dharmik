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
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.koin.compiler)
}

val appName = "Dharmik"
val appVersionCode = 4120
val appVersionName = "4.1.2"

val gitHash =
    providers
        .exec { commandLine("git", "rev-parse", "HEAD") }
        .standardOutput
        .asText
        .get()
        .trim()
        .take(7)

android {
    namespace = "com.shub39.dharmik"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.shub39.dharmik.online"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = appVersionCode
        versionName = appVersionName

        vectorDrawables { useSupportLibrary = true }

        buildConfigField("int", "VERSION_CODE", appVersionCode.toString())
        buildConfigField("String", "VERSION_NAME", "\"$appVersionName\"")
    }

    buildTypes {
        release {
            resValue("string", "app_name", appName)
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }

        create("beta") {
            resValue("string", "app_name", "$appName Beta")
            applicationIdSuffix = ".beta"
            versionNameSuffix = "-beta$gitHash"
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }

        debug {
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "$appName Debug")
        }
    }

    flavorDimensions += "version"

    productFlavors {
        create("play") {
            dimension = "version"

            val localProperties = Properties()
            val localFile = rootProject.file("local.properties")

            if (localFile.exists()) localProperties.load(localFile.inputStream())

            val postHogApiKey = localProperties.getProperty("POSTHOG_API_KEY") ?: ""
            val postHogHost = localProperties.getProperty("POSTHOG_HOST") ?: ""

            if (postHogHost.isBlank() || postHogApiKey.isBlank()) {
                println("WARNING: POSTHOG_API_KEY and POSTHOG_HOST must be set in local.properties")
            }

            buildConfigField("String", "POSTHOG_API_KEY", "\"$postHogApiKey\"")
            buildConfigField("String", "POSTHOG_HOST", "\"$postHogHost\"")
        }
        create("foss") { dimension = "version" }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }

    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    sourceSets {
        getByName("main") {
            res.directories.addAll(
                listOf(
                    "src/main/res",
                    "${project(":shared:ui").projectDir}/src/commonMain/composeResources",
                )
            )
        }
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-sensitive-resolution")
        optIn.add("androidx.compose.material3.ExperimentalMaterial3Api")
        optIn.add("androidx.compose.material3.ExperimentalMaterial3ExpressiveApi")
        optIn.add(
            "androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi"
        )
    }
}

dependencies {
    implementation(projects.shared.core)
    implementation(projects.shared.ui)
    implementation(projects.shared.logic)

    // Analytics is only wired into the Play flavor. The FOSS flavor ships without the SDK.
    "playImplementation"(libs.posthog.android)

    // Billing is only wired into the Play flavor. The FOSS flavor ships without the SDK.
    "playImplementation"(libs.purchases)
    "playImplementation"(libs.purchases.ui)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.koin.android)
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.compose.viewmodel.navigation)
    implementation(libs.koin.annotations)
}
