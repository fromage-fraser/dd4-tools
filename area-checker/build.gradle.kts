plugins {
    application
}

application {
    mainClass.set("dd4.areachecker.AreaCheckerMainKt")
}

dependencies {
    implementation(project(":core"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(Libs.kotlinx_cli)
}

tasks.shadowJar {
    archiveFileName.set("area-checker.jar")
    manifest {
        attributes["Main-Class"] = "dd4.areachecker.AreaCheckerMainKt"
    }
}
