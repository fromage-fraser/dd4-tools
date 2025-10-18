plugins {
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
