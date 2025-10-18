plugins {
    application
}

application {
    mainClass.set("dd4.areaquery.AreaQueryMainKt")
}

dependencies {
    implementation(project(":core"))
}

tasks.shadowJar {
    archiveFileName.set("area-query.jar")
    manifest {
        attributes["Main-Class"] = "dd4.areaquery.AreaQueryMainKt"
    }
}
