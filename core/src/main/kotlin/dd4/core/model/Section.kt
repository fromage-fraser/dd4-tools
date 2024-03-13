package dd4.core.model

import com.fasterxml.jackson.annotation.JsonValue

enum class Section(
        @JsonValue val tag: String,
) {
    AREA("AREA"),
    AREA_SPECIAL("AREA_SPECIAL"),
    MOBILES("MOBILES"),
    MOBPROGS("MOBPROGS"),
    OBJECTS("OBJECTS"),
    OBJECT_SETS("OBJECT_SETS"),
    ROOMS("ROOMS"),
    RESETS("RESETS"),
    SHOPS("SHOPS"),
    SPECIAL_FUNCTIONS("SPECIALS"),
    HELPS("HELPS"),
    RECALL("RECALL"),
    GAMES("GAMES"),
    END_OF_FILE("$");

    companion object {
        fun fromTag(value: String) = values().first { it.tag == value }
    }
}
