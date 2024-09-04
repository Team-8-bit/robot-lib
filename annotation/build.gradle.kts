val kspVersion: String by project

plugins {
    kotlin("jvm")
}

group = "org.team9432.lib"

dependencies {
    implementation("com.squareup:kotlinpoet:1.14.2")
    implementation("com.squareup:kotlinpoet-ksp:1.14.2")
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")

    implementation("org.littletonrobotics.akit.junction:junction-core:3.2.1")
}

repositories {
    mavenCentral()
    maven { setUrl("https://frcmaven.wpi.edu/artifactory/release/") }
    maven {
        url = uri("https://maven.pkg.github.com/Mechanical-Advantage/AdvantageKit")
        credentials {
            username = "Mechanical-Advantage-Bot"
            password = "\u0067\u0068\u0070\u005f\u006e\u0056\u0051\u006a\u0055\u004f\u004c\u0061\u0079\u0066\u006e\u0078\u006e\u0037\u0051\u0049\u0054\u0042\u0032\u004c\u004a\u006d\u0055\u0070\u0073\u0031\u006d\u0037\u004c\u005a\u0030\u0076\u0062\u0070\u0063\u0051"
        }
    }
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

