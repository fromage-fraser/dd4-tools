plugins {
    application
}

application {
    mainClass.set("dd4.areaparser.AreaParserMainKt")
}

dependencies {
    implementation(project(":core"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(Libs.kotlinx_cli)
}

tasks.shadowJar {
    archiveFileName.set("area-parser.jar")
    manifest {
        attributes["Main-Class"] = "dd4.areaparser.AreaParserMainKt"
    }
}
