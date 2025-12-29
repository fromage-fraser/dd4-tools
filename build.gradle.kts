import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlint)
}

allprojects {
    group = "dd4-tools"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = rootProject.libs.plugins.kotlin.jvm.get().pluginId)
    apply(plugin = rootProject.libs.plugins.ktlint.get().pluginId)

    dependencies {
        implementation(rootProject.libs.bundles.kotlin)
        implementation(rootProject.libs.bundles.jackson)
    }

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

    plugins.withType<JavaPlugin> {
        tasks.named<Jar>("jar") {
            archiveFileName.set(project.name + ".jar")
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE

            dependsOn(configurations.runtimeClasspath)

            from(sourceSets.main.get().output)

            from({
                configurations.runtimeClasspath.get()
                    .filter { it.name.endsWith(".jar") }
                    .map { zipTree(it) }
            })
        }
    }
}
