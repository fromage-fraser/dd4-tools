plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(Libs.jackson_databind)
    implementation(Libs.jackson_dataformat_yaml)
    implementation(Libs.jackson_module_kotlin)
}
