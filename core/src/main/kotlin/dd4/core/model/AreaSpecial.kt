package dd4.core.model

import com.fasterxml.jackson.annotation.JsonValue

data class AreaSpecial(
    val flags: Set<AreaFlag>,
    val experienceModifier: Int?,
    val resetMessage: String?,
) {
    enum class AreaFlag(@JsonValue val tag: String) {
        SCHOOL("school"),
        NO_QUEST("no_quest"),
        HIDDEN("hidden"),
        SAFE("safe"),
        NO_TELEPORT("no_teleport"),
        NO_MAGIC("no_magic"),
        ;

        companion object {
            fun fromTag(value: String) = try {
                entries.first { it.tag == value }
            } catch (_: NoSuchElementException) {
                throw IllegalArgumentException("Invalid area flag: $value")
            }
        }
    }

    fun isFlagged(flag: AreaFlag): Boolean = flags.contains(flag)
}
