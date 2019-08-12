plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-android-extensions")
}

android {

    compileSdkVersion(28)

    defaultConfig {

        minSdkVersion(21)
        targetSdkVersion(28)

        versionName = "0.0.4"
        versionCode = 1
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

    lintOptions {
        isCheckAllWarnings = true
        isWarningsAsErrors = false
        isAbortOnError = true
    }
}

repositories {
    mavenLocal()
    google()
    jcenter()
    mavenCentral()
    maven("https://kotlin.bintray.com/kotlinx")
    maven("https://dl.bintray.com/aimybox/aimybox-android-sdk/")
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("androidx.recyclerview:recyclerview:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.core:core-ktx:1.0.2")
    implementation("androidx.lifecycle:lifecycle-extensions:2.0.0")
    implementation("com.google.android.material:material:1.0.0")

    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0-M1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.0-M1")
//
//    implementation("com.github.bumptech.glide:glide:4.9.0")
//    annotationProcessor("com.github.bumptech.glide:compiler:4.9.0")

    val aimyboxVersion: String by rootProject.extra

    implementation("com.justai.aimybox:core:$aimyboxVersion")
    implementation("com.justai.aimybox:google-platform-speechkit:$aimyboxVersion")

}
