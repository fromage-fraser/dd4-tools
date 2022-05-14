object Versions {
    const val kotlin_jvm_target = "11"
    const val kotlin_api = "1.6"
    const val jackson = "2.11.1"
    const val kotlinx_cli = "0.3.4"
    const val freemarker = "2.3.30"
}

object Libs {
    const val jackson_databind = "com.fasterxml.jackson.core:jackson-databind:${Versions.jackson}"
    const val jackson_dataformat_yaml = "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${Versions.jackson}"
    const val jackson_module_kotlin = "com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.jackson}"
    const val kotlinx_cli = "org.jetbrains.kotlinx:kotlinx-cli:${Versions.kotlinx_cli}"
    const val freemarker = "org.freemarker:freemarker:${Versions.freemarker}"
}
