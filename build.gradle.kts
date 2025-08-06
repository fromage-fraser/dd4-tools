import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    kotlin("jvm") version "2.1.21"
    id("org.jlleitschuh.gradle.ktlint") version "12.3.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

allprojects {
    group = "dd4-tools"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "com.github.johnrengelman.shadow")

    kotlin {
        jvmToolchain(21)
    }

    ktlint {
        version.set("1.6.0")
        verbose.set(true)
        outputToConsole.set(true)
        coloredOutput.set(true)

        reporters {
            reporter(ReporterType.CHECKSTYLE)
            reporter(ReporterType.PLAIN)
            reporter(ReporterType.HTML)
        }
    }
}
