import com.android.build.gradle.BaseExtension

val demoAppModuleName = "demo"

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(Plugins.androidGradle)
        classpath(Plugins.kotlin)
        classpath(Plugins.dexcount)
    }
}

allprojects {
    repositories {
        mavenLocal()
        google()
        jcenter()
        mavenCentral()
        maven("https://kotlin.bintray.com/kotlinx")
    }

    if ((group as String).isNotEmpty()) configureAndroid()
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

fun Project.configureAndroid() {
    logger.warn("Project name ${project.name}")
    if (name == demoAppModuleName) {
        apply(plugin = "com.android.application")
    } else {
        apply(plugin = "com.android.library")
    }
    apply(plugin = "kotlin-android")
    apply(plugin = "kotlin-android-extensions")
    apply(plugin = "com.getkeepsafe.dexcount")

    configure<BaseExtension> {
        compileSdkVersion(Versions.Sdk.compile)

        defaultConfig {
            minSdkVersion(Versions.Sdk.min)
            targetSdkVersion(Versions.Sdk.target)

            versionName = Versions.aimybox
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
}
