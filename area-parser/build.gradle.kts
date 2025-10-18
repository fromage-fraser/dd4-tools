plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.shadow)
    application
}

application {
    mainClass.set("dd4.areaparser.AreaParserMainKt")
}

dependencies {
    implementation(project(":core"))
    implementation(libs.bundles.kotlin)
}

tasks.shadowJar {
    archiveFileName.set("area-parser.jar")
    manifest {
        attributes["Main-Class"] = "dd4.areaparser.AreaParserMainKt"
    }
}
