plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.9.20"
}

group = "me.kject"
version = "1.0-alpha-1"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    api(kotlin("reflect"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}