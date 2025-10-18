plugins {
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
