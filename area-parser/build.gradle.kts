plugins {
    application
}

application {
    mainClass.set("dd4.areaparser.AreaParserMainKt")
}

dependencies {
    implementation(project(":core"))
}

tasks.shadowJar {
    archiveFileName.set("area-parser.jar")
    manifest {
        attributes["Main-Class"] = "dd4.areaparser.AreaParserMainKt"
    }
}
