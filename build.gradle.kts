plugins {
    `java-library`
    kotlin("jvm") version "1.9.20"
}

group = "me.kject"
version = "1.0-alpha-1"

repositories {
    mavenCentral()
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