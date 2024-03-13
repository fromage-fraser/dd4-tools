package dd4.areaquery

import dd4.core.file.AreaFileMapper
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required

fun main(args: Array<String>) {

    val argParser = ArgParser("area-query")

    val areaFilePath = argParser.option(
            ArgType.String,
            fullName = "input-file",
            shortName = "i",
            description = "Input area file",
    ).required()

    val outputFilePath = argParser.option(
            ArgType.String,
            fullName = "output-file",
            shortName = "o",
            description = "Output file",
    ).required()

    val outputFormat = argParser.option(
            ArgType.Choice<FileFormat>(),
            fullName = "output-format",
            shortName = "f",
            description = "Output format",
    ).default(FileFormat.JSON)

    argParser.parse(args)

    println("Creating query DB...")
    val queryDbGenerator = QueryDbGenerator(areaFilePath.value, AreaFileMapper())
    val queryDb = queryDbGenerator.generate()

    println("Writing to ${outputFilePath.value}...")
    val queryDbMapper = QueryDbMapper()
    queryDbMapper.writeToFile(queryDb, outputFilePath.value, outputFormat.value)
}
