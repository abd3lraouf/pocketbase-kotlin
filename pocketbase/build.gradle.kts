import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.serialization)
}

group = "dev.abd3lraouf"
version = "1.0.0"

kotlin {
    explicitApi()
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.ktor.client.core)
                api(libs.ktor.client.content.negociation)
                api(libs.ktor.serialization.json)

                api(libs.kotlin.coroutines)
                api(libs.kotlin.datetime)
                api(libs.kotlin.serlization.json)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negociation)
                implementation(libs.ktor.serialization.json)

                implementation(libs.kotlin.coroutines)
                implementation(libs.kotlin.datetime)
                implementation(libs.kotlin.serlization.json)
            }
        }

        val cioMain by creating {
            dependsOn(commonMain)
            dependencies {
                api(libs.ktor.client.cio)
            }
        }
        val cioTest by creating {
            dependsOn(commonTest)
            dependencies {
                implementation(libs.ktor.client.cio)
            }
        }

        fun KotlinSourceSet.configureDependencies(test: Boolean = false) {
            if (test) {
                this.dependsOn(cioTest)
            } else {
                this.dependsOn(cioMain)
            }
        }

        getByName("iosArm64Main").configureDependencies()
        getByName("iosArm64Test").configureDependencies(test = true)

        getByName("iosSimulatorArm64Main").configureDependencies()
        getByName("iosSimulatorArm64Test").configureDependencies(test = true)

        getByName("iosX64Main").configureDependencies()
        getByName("iosX64Test").configureDependencies(test = true)

        getByName("androidMain").configureDependencies()
        getByName("androidUnitTest").configureDependencies(test = true)
    }
}

android {
    namespace = "dev.abd3lraouf.libs.pocketbase.kotlin"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()
    defaultConfig {
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "pocketbase-kotlin", version.toString())

    pom {
        name = "PocketbaseKotlin"
        description = "Pocketbase kotlin implementation ported to KMM"
        inceptionYear = "2025"
        url = "https://github.com/abd3lraouf/pocketbase-kotlin/"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "abd3lraouf"
                name = "Abdelraouf Sabri"
                url = "https://abd3lraouf.dev"
            }
        }
        scm {
            url = "https://github.com/abd3lraouf/pocketbase-kotlin/"
            connection = "scm:git:git://github.com/abd3lraouf/pocketbase-kotlin.git"
            developerConnection = "scm:git:ssh://git@github.com/abd3lraouf/pocketbase-kotlin.git"
        }
    }
}
