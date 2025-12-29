plugins {
    application
}

dependencies {
    implementation(project(":core"))
    implementation(libs.freemarker)
}

application {
    mainClass.set("dd4.mapmaker.MapMakerMainKt")
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Main-Class" to application.mainClass.get(),
        )
    }
}
