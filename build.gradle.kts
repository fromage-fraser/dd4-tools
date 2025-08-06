plugins {
    kotlin("jvm") version "2.1.21"
    id("org.jlleitschuh.gradle.ktlint") version "12.3.0"
}

allprojects {
    group = "dd4-tools"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}
