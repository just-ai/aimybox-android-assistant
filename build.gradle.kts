buildscript {
    val kotlinVersion = "1.6.10"
    val aimyboxVersion = "0.17.5.1"

    extra.set("kotlinVersion", kotlinVersion)
    extra.set("aimyboxVersion", aimyboxVersion)

    repositories {
        mavenCentral()
        google()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.android.tools.build:gradle:7.1.2")
    }

}

allprojects {

    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        jcenter()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

val Project.isSubmodule get() = name != rootProject.name
