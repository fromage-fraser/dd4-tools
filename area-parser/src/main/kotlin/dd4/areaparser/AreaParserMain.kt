package dd4.areaparser

import dd4.core.file.AreaFileMapper
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required

fun main(args: Array<String>) {
    val argParser = ArgParser("area-parser")

    val inputDirName = argParser.option(
            ArgType.String,
            fullName = "input-dir",
            shortName = "i",
            description = "Input file directory"
    ).required()

    val areaListFileName = argParser.option(
            ArgType.String,
            fullName = "area-list",
            shortName = "a",
            description = "Area list file"
    ).default("area.lst")

    val outputFilePath = argParser.option(
            ArgType.String,
            fullName = "output-file",
            shortName = "o",
            description = "Output file"
    ).required()

    val verbose = argParser.option(
            ArgType.Boolean,
            fullName = "verbose",
            shortName = "v",
            description = "Verbose logging"
    ).default(false)

    argParser.parse(args)

    println("Reading area files ... ")
    val parser = AreaParser(inputDirName.value, areaListFileName.value, verbose.value)
    val areaFiles = parser.parse()

    print("Writing YAML to file ${outputFilePath.value} ...")
    val renderer = AreaFileMapper()
    renderer.writeToFile(areaFiles, outputFilePath.value)
    println("complete")
}
