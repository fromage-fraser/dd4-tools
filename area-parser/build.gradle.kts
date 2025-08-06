plugins {
    kotlin("jvm")
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

kotlin {
    jvmToolchain(21)
}

val jar by tasks.getting(Jar::class) {
    archiveFileName.set("area-parser.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = "dd4.areaparser.AreaParserMainKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
        exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
    }
}
