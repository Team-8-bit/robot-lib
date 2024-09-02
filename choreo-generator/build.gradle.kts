plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "org.team9432.lib"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.team9432.lib:robot-lib")

    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
}

tasks.test {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
}