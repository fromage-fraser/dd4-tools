package dd4.areachecker

import dd4.core.file.AreaFileMapper
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required

fun main(args: Array<String>) {
    val argParser = ArgParser("area-analyser")

    val areaFileName = argParser.option(
        ArgType.String,
        fullName = "input-file",
        shortName = "i",
        description = "Input area file",
    ).required()

    argParser.parse(args)

    val areaChecker = AreaChecker(areaFileName.value, AreaFileMapper())
    areaChecker.check()
}
