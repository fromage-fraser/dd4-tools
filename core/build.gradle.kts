import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(Libs.jackson_databind)
    implementation(Libs.jackson_dataformat_yaml)
    implementation(Libs.jackson_module_kotlin)
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
