buildscript {
    val hiltVersion: String by project
    val kotlinVersion: String by project

    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("org.slf4j:slf4j-simple:2.0.6")
    }
    repositories {
        mavenCentral()
    }

}
plugins {
    kotlin("jvm")   version "1.8.10"
    id("io.ktor.plugin") version "2.2.3"
    id("com.android.application") version "7.4.0-beta02" apply false
    id("com.android.library") version "7.4.0-beta02" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
}


group = "abdulrahman.codes"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}