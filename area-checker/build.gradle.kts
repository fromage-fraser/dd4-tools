plugins {
    application
}

dependencies {
    implementation(project(":core"))
}

application {
    mainClass.set("dd4.areachecker.AreaCheckerMainKt")
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Main-Class" to application.mainClass.get(),
        )
    }
}
