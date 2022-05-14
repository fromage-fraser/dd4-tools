package dd4.core.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue

data class Room(
        val vnum: Int,
        val name: String,
        val description: String,
        val flags: Set<Flag>,
        val sectorType: SectorType,
        val exits: Map<Direction, Exit>,
        val extraDescriptions: List<ExtraDescription>
) {
    enum class Flag(
            @JsonValue val tag: String,
            val bit: Int
    ) {
        DARK("dark", 0x1),
        NO_MOB("no_mob", 0x4),
        INDOORS("indoors", 0x8),
        PRIVATE("private", 0x200),
        SAFE("safe", 0x400),
        SOLITARY("solitary", 0x800),
        PET_SHOP("pet_shop", 0x1000),
        NO_RECALL("no_recall", 0x2000),
        CONE_OF_SILENCE("cone_of_silence", 0x4000),
        PLAYER_KILLER("player_killer", 0x8000),
        HEALING("healing", 0x10000),
        FREEZING("freezing", 0x20000),
        BURNING("burning", 0x40000),
        NO_MOUNT("no_mount", 0x80000);

        companion object {
            fun fromInt(value: Int) = values().filter { value.and(it.bit) != 0 }.toSet()
        }
    }

    enum class SectorType(
            @JsonValue val tag: String,
            val id: Int
    ) {
        UNKNOWN("unknown", -1),
        INSIDE("inside", 0),
        CITY("city", 1),
        FIELD("field", 2),
        FOREST("forest", 3),
        HILLS("hills", 4),
        MOUNTAIN("mountain", 5),
        WATER_SWIM("water_swim", 6),
        WATER_NO_SWIM("water_no_swim", 7),
        UNDERWATER("underwater", 8),
        AIR("air", 9),
        DESERT("desert", 10);

        companion object {
            fun fromId(value: Int) =
                    try {
                        values().first { it.id == value }
                    }
                    catch (e: NoSuchElementException) {
                        throw IllegalArgumentException("Invalid sector type ID: $value")
                    }

            fun findById(value: Int) = values().find { it.id == value }
        }
    }

    data class ExtraDescription(
            val keywords: String,
            val description: String
    )

    override fun toString(): String {
        return "Room(#$vnum '$name' sectorType=${sectorType.tag}" +
                " flags=" + flags.joinToString(",") { it.tag }.ifEmpty { "none" } +
                ")"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Room

        if (vnum != other.vnum) return false

        return true
    }

    override fun hashCode(): Int {
        return vnum
    }

    @get:JsonIgnore
    val cleanName: String
        get() = this.name.replace(Regex("[{}]."), "")

    fun exit(direction: Direction) = exits[direction]

    fun hasExit(direction: Direction): Boolean = exits.containsKey(direction)

    fun hasExitTo(destinationRoom: Room, directionToDestination: Direction): Boolean {
        if (this == destinationRoom) return false
        return exits[directionToDestination]?.destinationVnum == destinationRoom.vnum
    }

    fun hasReturnExitTo(destinationRoom: Room, directionToDestination: Direction): Boolean {
        if (!hasExitTo(destinationRoom, directionToDestination)) return false
        return destinationRoom.exits[directionToDestination.reverse()]?.destinationVnum == vnum
    }

    fun isFlagged(flag: Flag): Boolean = flags.contains(flag)
}
