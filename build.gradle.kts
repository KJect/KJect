plugins {
    kotlin("jvm") version "1.9.10"
}

group = "me.kject"
version = "1.0-alpha-1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}