plugins {
    kotlin("jvm")
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

kotlin {
    jvmToolchain(21)
}

val jar by tasks.getting(Jar::class) {
    archiveFileName.set("map-maker.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = "dd4.mapmaker.MapMakerMainKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
        exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
    }
}
