plugins {
    application
}

application {
    mainClass.set("dd4.areaquery.AreaQueryMainKt")
}

dependencies {
    implementation(project(":core"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(Libs.kotlinx_cli)
    implementation(Libs.jackson_databind)
    implementation(Libs.jackson_dataformat_yaml)
    implementation(Libs.jackson_module_kotlin)
}

tasks.shadowJar {
    archiveFileName.set("area-query.jar")
    manifest {
        attributes["Main-Class"] = "dd4.areaquery.AreaQueryMainKt"
    }
}
