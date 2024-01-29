import java.util.*
import java.io.File
import java.io.FileInputStream

val kotlin_version: String by extra
val aimyboxVersion: String by rootProject.extra

plugins {
    id("com.android.application")
    kotlin("android")
}
apply {
    plugin("kotlin-android")
}

android {

    namespace = "com.justai.aimybox.assistant"

    compileSdk = 33

    defaultConfig {
        applicationId = "com.justai.aimybox.assistant"

        minSdk = 21
        targetSdk = 33

        versionName = aimyboxVersion
        versionCode = 1

        val properties = Properties()
        properties.load(FileInputStream(File(rootProject.rootDir, "local.properties")))
        buildConfigField("String", "token", properties.getProperty("token"))
        buildConfigField("String", "folderId", properties.getProperty("folderId"))
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            //TODO configure pro guard
        }
    }
    lint {
        abortOnError = true
        checkAllWarnings = true
        warningsAsErrors = false
    }
    namespace = "com.justai.aimybox.assistant"
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    implementation("com.just-ai.aimybox:components:$aimyboxVersion")
    implementation("com.just-ai.aimybox:core:$aimyboxVersion")
    implementation("com.just-ai.aimybox:yandex-speechkit:$aimyboxVersion")

}