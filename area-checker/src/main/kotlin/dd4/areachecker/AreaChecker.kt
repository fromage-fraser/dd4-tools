package dd4.areachecker

import dd4.core.file.AreaFileMapper
import dd4.core.model.SourceFile

class AreaChecker(
        private val areaFileName: String,
        private val areaFileMapper: AreaFileMapper,
) {
    fun check() {
        val areaFiles = areaFileMapper.readFromFile(areaFileName)

        println("Checking areas...")
        val result = CheckResult()

        for (areaFile in areaFiles) {
            result.merge(checkObjects(areaFile))
        }

        println("Complete (${result.errors} errors, ${result.warnings} warnings)")
    }

    private fun checkObjects(sourceFile: SourceFile): CheckResult {
        val result = CheckResult()

        for (item in sourceFile.objects) {
            if (item.fullDescription.contains(Regex("[\r\n\t]"))) {
                error(
                        "object #${item.vnum} \"${item.shortDescription}\": " +
                                "full description has bad characters => \"${escape(item.fullDescription)}\"",
                        sourceFile, result,
                )
            }

            if (item.shortDescription.matches(Regex("^.*[\\s.,;:!?]\\s*$"))) {
                error(
                        "object #${item.vnum}: " +
                                "short description ends with bad characters => \"${escape(item.shortDescription)}\"",
                        sourceFile, result,
                )
            }

            if (item.shortDescription.matches(Regex("^(A|An|The|Some)\\b.*$"))) {
                warning(
                        "object #${item.vnum}: " +
                                "short description might be wrongly capitalised => \"${escape(item.shortDescription)}\"",
                        sourceFile, result,
                )
            }

            if (item.fullDescription.isBlank()) {
                warning(
                        "object #${item.vnum} \"${item.shortDescription}\": " +
                                "blank full description (hidden)",
                        sourceFile, result,
                )
            }
        }

        return result
    }

    private fun error(details: String, sourceFile: SourceFile, checkResult: CheckResult) {
        println("[error] ${sourceFile.id}: $details")
        checkResult.errors++
    }

    private fun warning(details: String, sourceFile: SourceFile, checkResult: CheckResult) {
        println("[warn ] ${sourceFile.id}: $details")
        checkResult.warnings++
    }

    private fun escape(string: String): String =
            string.replace("\r", "\\r")
                    .replace("\n", "\\n")
                    .replace("\t", "\\t")
}
