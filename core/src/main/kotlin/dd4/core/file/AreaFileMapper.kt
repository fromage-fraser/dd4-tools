package dd4.core.file

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.KotlinModule
import dd4.core.model.SourceFile
import java.io.File

class AreaFileMapper {

    private val yamlFactory = YAMLFactory()
        .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
        .enable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE)
        .enable(YAMLGenerator.Feature.SPLIT_LINES)

    private val objectMapper = ObjectMapper(yamlFactory).registerModules(KotlinModule())

    fun writeToFile(sourceFiles: List<SourceFile>, outputFilePath: String) {
        if (sourceFiles.isEmpty()) throw IllegalArgumentException("source files required")

        objectMapper.writer()
            .withDefaultPrettyPrinter()
            .writeValues(File(outputFilePath))
            .use { sequenceWriter ->
                sequenceWriter.writeAll(sourceFiles)
            }
    }

    fun readFromFile(inputFilePath: String): List<SourceFile> {
        val yamlParser = yamlFactory.createParser(File(inputFilePath))
        return objectMapper.readValues(yamlParser, object : TypeReference<SourceFile>() {})
            .readAll()
    }
}
