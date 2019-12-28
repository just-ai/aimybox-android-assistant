import com.android.build.gradle.BaseExtension
import com.jfrog.bintray.gradle.BintrayExtension


buildscript {
    val kotlinVersion = "1.3.50"
    val aimyboxVersion = "0.7.0"
    val componentsVersion = "0.1.8"

    extra.set("kotlinVersion", kotlinVersion)
    extra.set("aimyboxVersion", aimyboxVersion)
    extra.set("componentsVersion", componentsVersion)

    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.android.tools.build:gradle:3.5.0")
        classpath("com.getkeepsafe.dexcount:dexcount-gradle-plugin:0.8.6")
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4")
        classpath("org.jfrog.buildinfo:build-info-extractor-gradle:4.7.5")
    }

}

allprojects {

    repositories {
        mavenLocal()
        google()
        jcenter()
        mavenCentral()
        maven("https://kotlin.bintray.com/kotlinx")
        maven("https://dl.bintray.com/aimybox/aimybox-android-sdk/")
    }
}

subprojects {
    afterEvaluate {
        val componentsVersion: String by rootProject.extra
        if (name == "components") configureBintrayPublishing(componentsVersion)
    }
}

fun Project.configureBintrayPublishing(version: String) {
    apply(plugin = "com.jfrog.bintray")

    val publicationName = project.name
        .split('-')
        .joinToString("", transform = String::capitalize)

    configurePublication(publicationName, version)

    configure<BintrayExtension> {
        val properties = java.util.Properties()
        val propsFile = project.rootProject.file("local.properties")

        if (propsFile.canRead()) {
            properties.load(propsFile.inputStream())
        }

        val bintrayUsername = properties["bintrayUser"] as? String
            ?: System.getProperty("BINTRAY_USER") ?: System.getenv("BINTRAY_USER")
        val bintrayKey = properties["bintrayKey"] as? String
            ?: System.getProperty("BINTRAY_KEY") ?: System.getenv("BINTRAY_KEY")

        user = bintrayUsername
        key = bintrayKey

        pkg(closureOf<BintrayExtension.PackageConfig> {
            repo = "aimybox-android-assistant"
            name = project.name
            userOrg = "aimybox"
            setLicenses("Apache-2.0")
            websiteUrl = "https://aimybox.com"
            publish = true
            vcsUrl = "https://github.com/just-ai/aimybox-android-assistant.git"
            version(closureOf<BintrayExtension.VersionConfig> {
                name = version
            })
        })

        setPublications(publicationName)
    }

    afterEvaluate {
        val generatePomFile = "generatePomFileFor${publicationName}Publication"

        tasks.register("prepareArtifacts") {
            dependsOn("assembleRelease", generatePomFile, "sourcesJar")
        }

        tasks.named("bintrayUpload").configure { dependsOn("prepareArtifacts") }
        tasks.named("publish${publicationName}PublicationToMavenLocal").configure {
            dependsOn("prepareArtifacts")
        }
    }
}

fun Project.configurePublication(publicationName: String, publicationVersion: String) {
    apply(plugin = "maven-publish")

    val sourcesJar = tasks.register<Jar>("sourcesJar") {
        classifier = "sources"
        from(project.the<BaseExtension>().sourceSets["main"].java.srcDirs)
    }

    artifacts.add("archives", sourcesJar)

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>(publicationName) {
                groupId = "com.justai.aimybox"
                artifactId = project.name
                version = publicationVersion

                val releaseAar = "$buildDir/outputs/aar/${project.name}-release.aar"

                artifact(releaseAar)
                artifact(sourcesJar.get())

                pom {
                    name.set("Aimybox ${project.name.replace('-', ' ').capitalize()}")
                    description.set("Aimybox Android SDK")
                    url.set("https://github.com/aimybox/aimybox-android-sdk")
                    organization {
                        name.set("Aimybox")
                        url.set("https://aimybox.com/")
                    }
                    scm {
                        val scmUrl = "scm:git:git@github.com/aimybox/aimybox-android-sdk.git"
                        connection.set(scmUrl)
                        developerConnection.set(scmUrl)
                        url.set(this@pom.url)
                        tag.set("HEAD")
                    }
                    developers {
                        developer {
                            id.set("morfeusys")
                            name.set("Dmitriy Chechyotkin")
                            email.set("morfeusys@gmail.com")
                            organization.set("Aimybox")
                            organizationUrl.set("https://aimybox.com")
                            roles.set(listOf("Project-Administrator", "Developer"))
                        }
                        developer {
                            id.set("lambdatamer")
                            name.set("Alexander Sirota")
                            email.set("lambdatamer@gmail.com")
                            organization.set("Aimybox")
                            organizationUrl.set("https://aimybox.com")
                            roles.set(listOf("Developer"))
                        }
                        developer {
                            id.set("nikkas29")
                            name.set("Nikita Kasenkov")
                            organization.set("Aimybox")
                            organizationUrl.set("https://aimybox.com")
                            roles.set(listOf("Developer"))
                        }
                    }
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    withXml {
                        asNode().appendNode("dependencies").apply {
                            fun Dependency.write(scope: String) = appendNode("dependency").apply {
                                appendNode("groupId", group)
                                appendNode("artifactId", name)
                                appendNode("version", version)
                                appendNode("scope", scope)
                            }
                            for (dependency in configurations["api"].dependencies) {
                                dependency.write("compile")
                            }
                            for (dependency in configurations["implementation"].dependencies) {
                                dependency.write("runtime")
                            }
                        }
                    }
                }
            }
        }
    }
}


tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

val Project.isSubmodule get() = name != rootProject.name
