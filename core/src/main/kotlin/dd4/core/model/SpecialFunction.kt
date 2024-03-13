package dd4.core.model

data class SpecialFunction(
        val mobileVnum: Int,
        val function: String,
        val comment: String,
) {
    companion object {
        // Healer
        const val SPECIAL_FUNCTION_ADEPT = "spec_cast_adept"
    }

    override fun toString(): String {
        return "SpecialFunction(#$mobileVnum $function" +
                (if (comment.isNotBlank()) " '$comment'" else "") +
                ")"
    }
}
