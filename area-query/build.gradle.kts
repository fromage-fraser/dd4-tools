plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.shadow)
    application
}

application {
    mainClass.set("dd4.areaquery.AreaQueryMainKt")
}

dependencies {
    implementation(project(":core"))
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.jackson)
}

tasks.shadowJar {
    archiveFileName.set("area-query.jar")
    manifest {
        attributes["Main-Class"] = "dd4.areaquery.AreaQueryMainKt"
    }
}
