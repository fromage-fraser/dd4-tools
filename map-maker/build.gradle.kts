plugins {
    application
}

application {
    mainClass.set("dd4.mapmaker.MapMakerMainKt")
}

dependencies {
    implementation(project(":core"))

    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.jackson)
    implementation(libs.freemarker)
}

tasks.shadowJar {
    archiveFileName.set("map-maker.jar")
    manifest {
        attributes["Main-Class"] = "dd4.mapmaker.MapMakerMainKt"
    }
}
