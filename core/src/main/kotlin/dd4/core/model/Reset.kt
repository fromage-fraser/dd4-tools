package dd4.core.model

import com.fasterxml.jackson.annotation.JsonValue

data class Reset(
    val type: Type,
    val arg0: Int,
    val arg1: Int,
    val arg2: Int,
    val arg3: Int,
    val comment: String,
) {
    enum class Type(@JsonValue val tag: String, val id: Char) {
        // Values: - <mobile vnum> <max count> <room vnum>
        MOBILE_TO_ROOM("mobile_to_room", 'M'),

        // Values: - <object vnum> - <room vnum>
        OBJECT_TO_ROOM("object_to_room", 'O'),

        // Values: <object vnum> <object level> <room vnum> <max count in room>
        OBJECT_TO_ROOM_EXTENDED("object_to_room_extended", 'I'),

        // Values: - <object to place vnum> - <container object vnum>
        OBJECT_TO_OBJECT("object_to_object", 'P'),

        // Values: - <object vnum> - -
        OBJECT_TO_MOBILE_INVENTORY("object_to_mobile_inventory", 'G'),

        // Values: - <object vnum> - <wear location>
        OBJECT_TO_MOBILE_EQUIPMENT("object_to_mobile_equipment", 'E'),

        // Values: - <room vnum> <direction number> <0 -> open, 1 -> close, 2 -> close and lock>
        DOOR("door", 'D'),

        // Values: - <room vnum> <max direction number: 4 -> NSEW, 6 -> NSEWUD> -
        RANDOMIZE_EXITS("randomize_exits", 'R'),

        UNKNOWN_F("unknown_f", 'F'),
        ;

        companion object {
            fun fromId(value: Char) = try {
                entries.first { it.id == value }
            } catch (_: NoSuchElementException) {
                throw IllegalArgumentException("Invalid reset type ID: $value")
            }
        }
    }

    override fun toString(): String = "Reset(${type.tag} $arg0 $arg1 $arg2 $arg3" +
        (if (comment.isNotBlank()) " '$comment'" else "") +
        ")"
}
