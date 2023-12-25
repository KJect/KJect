plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.9.22"
}

group = "me.kject"
version = "1.0-alpha-1"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    api(kotlin("reflect"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}