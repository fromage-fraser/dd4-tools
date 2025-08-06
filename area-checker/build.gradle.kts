import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
    application
}

application {
    mainClass.set("dd4.areachecker.AreaCheckerMainKt")
}

dependencies {
    implementation(project(":core"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(Libs.kotlinx_cli)
}

kotlin {
    jvmToolchain(21)
}

val jar by tasks.getting(Jar::class) {
    archiveFileName.set("area-checker.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = "dd4.areachecker.AreaCheckerMainKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
        exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
    }
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

    filter {
        include("src/main/kotlin/**")
        include("src/test/kotlin/**")
    }
}
