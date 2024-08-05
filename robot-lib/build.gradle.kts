val kotlinVersion: String by project
val kspVersion: String by project
val wpiLibVersion: String by project
val frcYear: String by project

plugins {
    kotlin("jvm")
}

group = "org.team9432.lib"

dependencies {
    // Wpilib
    implementation("edu.wpi.first.hal:hal-java:$wpiLibVersion")
    implementation("edu.wpi.first.wpilibj:wpilibj-java:$wpiLibVersion")
    implementation("edu.wpi.first.wpiutil:wpiutil-java:$wpiLibVersion")
    implementation("edu.wpi.first.wpimath:wpimath-java:$wpiLibVersion")
    implementation("edu.wpi.first.wpiunits:wpiunits-java:$wpiLibVersion")
    implementation("edu.wpi.first.ntcore:ntcore-jni:$wpiLibVersion")
    implementation("edu.wpi.first.ntcore:ntcore-java:$wpiLibVersion")

    // Vendors
    implementation("edu.wpi.first.wpilibNewCommands:wpilibNewCommands-java:$wpiLibVersion")
    implementation("com.ctre.phoenix6:wpiapi-java:24.2.0")
    implementation("org.photonvision:photonlib-java:v2024.3.1")
    implementation("org.photonvision:photontargeting-java:v2024.3.1")
    implementation("com.revrobotics.frc:REVLib-java:2024.2.1")
    implementation("com.github.jonahsnider:doglog:2024.5.8")

    // Misc.
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1-Beta")

    testImplementation(kotlin("test", kotlinVersion))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
}

tasks.test {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
    maven { setUrl("https://frcmaven.wpi.edu/artifactory/release/") }
    maven { setUrl("https://plugins.gradle.org/m2/") }
    maven { setUrl("https://maven.ctr-electronics.com/release/") }
    maven { setUrl("https://maven.revrobotics.com/") }
    maven { setUrl("https://maven.photonvision.org/repository/internal") }
    maven { setUrl("https://maven.photonvision.org/repository/snapshots") }
    maven { setUrl("https://jitpack.io") }
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

