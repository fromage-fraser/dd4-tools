package dd4.areachecker

data class CheckResult(var errors: Int = 0, var warnings: Int = 0) {
    fun merge(result: CheckResult) {
        errors += result.errors
        warnings += result.warnings
    }
}
