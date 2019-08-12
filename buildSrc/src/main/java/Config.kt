val Submodules = listOf(
    Application("app", Versions.components, false),
    Library("components", Versions.components, true)
)

object Versions {
    object Sdk {
        const val min = 21
        const val target = 28
        const val compile = target
    }

    object Plugins {
        const val androidGradle = "3.4.1"
        const val dexCount = "0.8.6"
        const val bintray = "1.8.4"
        const val buildInfo = "4.7.5"
    }

    const val aimybox = "0.1.3"
    const val components = "0.0.3"

    const val kotlin = "1.3.41"
    const val coroutines = "1.3.0-M1"

    const val appCompat = "1.0.2"
    const val constraintLayout = "1.1.3"
    const val material = "1.1.0-alpha06"
    const val ktx = "1.2.0-alpha01"
    const val lifecycle = "2.0.0"

    const val glide = "4.9.0"

    const val kotson = "2.5.0"

    const val kodein = "6.3.2"

    const val mockk = "1.9"
    const val androidxTest = "1.1.1"
}

object Plugins {
    val kotlin = kotlin("gradle-plugin")
    val androidGradle = "com.android.tools.build:gradle" version Versions.Plugins.androidGradle
    val dexcount = "com.getkeepsafe.dexcount:dexcount-gradle-plugin" version Versions.Plugins.dexCount
    val bintray = "com.jfrog.bintray.gradle:gradle-bintray-plugin" version Versions.Plugins.bintray
    val buildInfo = "org.jfrog.buildinfo:build-info-extractor-gradle" version Versions.Plugins.buildInfo
}

object Libraries {
    object Kotlin {
        val stdLib = kotlin("stdlib")

        val coroutinesCore = kotlinx("coroutines-core", Versions.coroutines)
        val coroutinesAndroid = kotlinx("coroutines-android", Versions.coroutines)

        val coroutines = listOf(coroutinesCore, coroutinesAndroid)


    }

    object Android {
        val appCompat = "androidx.appcompat:appcompat" version Versions.appCompat
        val constraintLayout = "androidx.constraintlayout:constraintlayout" version Versions.constraintLayout
        val material = "com.google.android.material:material" version Versions.material
        val ktx = "androidx.core:core-ktx" version Versions.ktx

        val lifecycle = "androidx.lifecycle:lifecycle-extensions" version Versions.lifecycle

        val glide = "com.github.bumptech.glide:glide" version Versions.glide
        val glideCompiler = "com.github.bumptech.glide:compiler" version Versions.glide

        val kotson = "com.github.salomonbrys.kotson:kotson" version Versions.kotson

        val kodeinDi = "org.kodein.di:kodein-di-generic-jvm" version Versions.kodein
        val kodeinFramework = "org.kodein.di:kodein-di-framework-android-x" version Versions.kodein

        val kodein = listOf(kodeinDi, kodeinFramework)
    }


    object Test {
        val kotlin = kotlin("test")
        val kotlinJUnit = kotlin("test-junit")

        val mockk = "io.mockk:mockk" version Versions.mockk

        val androidXRunner = "androidx.test:runner" version Versions.androidxTest
        val androidXRules = "androidx.test:rules" version Versions.androidxTest

        val unitTest = listOf(kotlin, kotlinJUnit, mockk)
        val instrumentedTest = listOf(kotlin, androidXRunner, androidXRules)

    }
}

internal fun kotlin(module: String, version: String = Versions.kotlin) =
    "org.jetbrains.kotlin:kotlin-$module:$version"

internal fun kotlinx(module: String, version: String = Versions.kotlin) =
    "org.jetbrains.kotlinx:kotlinx-$module:$version"

internal infix fun String.version(version: String) = plus(":$version")