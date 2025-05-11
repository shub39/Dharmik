import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.reload.ComposeHotRun
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.hotreload)
}

val variant: String by project

val appName = "Dharmik"
val appBasePackageName = "com.shub39.dharmik"
val appPackageName = "$appBasePackageName.$variant"
val appVersionName = "2.2.1"
val appVersionCode = 2210

android {
    namespace = appPackageName
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = appPackageName
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = appVersionCode
        versionName = appVersionName
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            resValue("string", "app_name", appName)
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "src/commonMain/proguard-rules.pro"
            )
        }
        debug {
            resValue("string", "app_name", "$appName Debug")
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

kotlin {
    targets.all {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }

    jvm("desktop")

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.compose.adaptive)
            implementation(compose.material3)
            implementation(libs.mediaPlayer)
            implementation(libs.materialKolor)
            implementation(libs.androidx.datastore.core)
            implementation(libs.jetbrains.compose.navigation)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.datetime)
            implementation(libs.composeIcons.fontAwesome)
            api(libs.koin.core)
            implementation(libs.material.icons.core)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
        dependencies {
            ksp(libs.androidx.room.compiler)
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

buildkonfig {
    packageName = appBasePackageName
    objectName = "DharmikConfigs"
    exposeObjectWithName = "DharmikConfig"

    defaultConfigs {
        buildConfigField(STRING, "packageName", appPackageName)
        buildConfigField(STRING, "versionName", appVersionName)
        buildConfigField(STRING, "versionCode", appVersionCode.toString())
        buildConfigField(STRING, "variant", variant)
    }
}

compose.desktop {
    application {
        mainClass = "com.shub39.dharmik.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = appPackageName
            packageVersion = appVersionName
        }
    }
}

tasks.register<ComposeHotRun>("runHot") {
    mainClass.set("com.shub39.dharmik.MainKt")
}

// offline variant tasks
if (variant == "offline") {
    val sourceDir = file("$projectDir/gita_audio")
    val destination = file("$projectDir/src/commonMain/composeResources/files/gita_audio")

    tasks.register<Copy>("PackageOfflineResources") {
        into(destination)
        from(sourceDir)
    }

    tasks.matching { it.name == "copyNonXmlValueResourcesForCommonMain" }.configureEach {
        dependsOn("PackageOfflineResources")
        finalizedBy("CleanOfflineResources")
    }

    tasks.register<Delete>("CleanOfflineResources") {
        delete(destination)
    }
}