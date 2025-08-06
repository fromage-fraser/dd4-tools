package dd4.core.model

import com.fasterxml.jackson.annotation.JsonValue

data class AreaSpecial(
        val flags: Set<AreaFlag>,
        val experienceModifier: Int?,
        val resetMessage: String?,
) {
    enum class AreaFlag(
            @JsonValue val tag: String,
    ) {
        SCHOOL("school"),
        NO_QUEST("no_quest"),
        HIDDEN("hidden"),
        SAFE("safe"),
        NO_TELEPORT("no_teleport"),
        NO_MAGIC("no_magic");

        companion object {
            fun fromTag(value: String) =
                    try {
                        values().first { it.tag == value }
                    }
                    catch (e: NoSuchElementException) {
                        throw IllegalArgumentException("Invalid area flag: $value")
                    }

            fun findByTag(value: String) = values().find { it.tag == value }
        }
    }

    fun isFlagged(flag: AreaFlag): Boolean = flags.contains(flag)
}
