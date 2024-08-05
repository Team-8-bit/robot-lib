val kspVersion: String by project

plugins {
    kotlin("jvm")
    `java-gradle-plugin`
}

group = "org.team9432.lib"

dependencies {
    implementation("com.squareup:kotlinpoet:1.14.2")
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("choreo-plugin") {
            id = "org.team9432.lib.choreo-plugin"
            implementationClass = "org.team9432.lib.choreoplugin.ChoreoPlugin"
        }
    }
}