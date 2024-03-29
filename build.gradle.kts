buildscript {
    val kotlinVersion = "1.8.21"
    val aimyboxVersion = "0.17.6-alpha.2"

    extra.set("kotlinVersion", kotlinVersion)
    extra.set("aimyboxVersion", aimyboxVersion)

    repositories {
        mavenCentral()
        google()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.android.tools.build:gradle:7.1.3")
    }

}

allprojects {

    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

val Project.isSubmodule get() = name != rootProject.name
