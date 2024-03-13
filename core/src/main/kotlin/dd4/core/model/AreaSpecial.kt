package dd4.core.model

import com.fasterxml.jackson.annotation.JsonValue

data class AreaSpecial(
        val flags: Set<AreaFlag>,
        val experienceModifier: Int?,
) {
    enum class AreaFlag(
            @JsonValue val tag: String,
    ) {
        AREA_FLAG_SCHOOL("school"),
        AREA_FLAG_NO_QUEST("no_quest"),
        AREA_FLAG_HIDDEN("hidden"),
        AREA_FLAG_SAFE("safe"),
        AREA_FLAG_NO_TELEPORT("no_teleport"),
        AREA_FLAG_NO_MAGIC("no_magic");

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
}
