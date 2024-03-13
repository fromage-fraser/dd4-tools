package dd4.mapmaker

import dd4.core.file.AreaFileMapper
import dd4.mapmaker.render.HtmlRenderer
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required

fun main(args: Array<String>) {
    val argParser = ArgParser("map-maker")

    val areaFileName = argParser.option(
            ArgType.String,
            fullName = "input-file",
            shortName = "i",
            description = "Input area file",
    ).required()

    val outputDirPath = argParser.option(
            ArgType.String,
            fullName = "output-dir",
            shortName = "o",
            description = "Output directory",
    ).required()

    val verbose = argParser.option(
            ArgType.Boolean,
            fullName = "verbose",
            shortName = "v",
            description = "Verbose logging",
    ).default(false)

    argParser.parse(args)

    val mapMaker = MapMaker(
            areaFileName.value,
            outputDirPath.value,
            AreaFileMapper(),
            HtmlRenderer(),
            verbose.value,
    )

    mapMaker.generate()
}
