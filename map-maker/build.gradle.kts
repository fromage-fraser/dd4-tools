plugins {
    application
}

application {
    mainClass.set("dd4.mapmaker.MapMakerMainKt")
}

dependencies {
    implementation(project(":core"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(Libs.kotlinx_cli)
    implementation(Libs.jackson_databind)
    implementation(Libs.jackson_dataformat_yaml)
    implementation(Libs.jackson_module_kotlin)
    implementation(Libs.freemarker)
}

tasks.shadowJar {
    archiveFileName.set("map-maker.jar")
    manifest {
        attributes["Main-Class"] = "dd4.mapmaker.MapMakerMainKt"
    }
}
