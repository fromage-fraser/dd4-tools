plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.shadow)
    application
}

application {
    mainClass.set("dd4.areachecker.AreaCheckerMainKt")
}

dependencies {
    implementation(project(":core"))
    implementation(libs.bundles.kotlin)
}

tasks.shadowJar {
    archiveFileName.set("area-checker.jar")
    manifest {
        attributes["Main-Class"] = "dd4.areachecker.AreaCheckerMainKt"
    }
}
