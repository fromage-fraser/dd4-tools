package dd4.core.model

import com.fasterxml.jackson.annotation.JsonValue

enum class Section(
        @JsonValue val tag: String
) {
    AREA("AREA"),
    AREA_SPECIAL("AREA_SPECIAL"),
    MOBILES("MOBILES"),
    MOBPROGS("MOBPROGS"),
    OBJECTS("OBJECTS"),
    ROOMS("ROOMS"),
    RESETS("RESETS"),
    SHOPS("SHOPS"),
    SPECIAL_FUNCTIONS("SPECIALS"),
    HELPS("HELPS"),
    RECALL("RECALL"),
    END_OF_FILE("$");

    companion object {
        fun fromTag(value: String) = values().first { it.tag == value }
    }
}
