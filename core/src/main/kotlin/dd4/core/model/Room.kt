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
        val extraDescriptions: List<ExtraDescription>,
) {
    enum class Flag(
            @JsonValue val tag: String,
            val bit: ULong,
    ) {
        DARK("dark", 0x1u),
        NO_MOB("no_mob", 0x4u),
        INDOORS("indoors", 0x8u),
        VAULT("vault", 0x10u),
        CRAFT("craft", 0x80u),
        SPELLCRAFT("spellcraft", 0x100u),
        PRIVATE("private", 0x200u),
        SAFE("safe", 0x400u),
        SOLITARY("solitary", 0x800u),
        PET_SHOP("pet_shop", 0x1000u),
        NO_RECALL("no_recall", 0x2000u),
        CONE_OF_SILENCE("cone_of_silence", 0x4000u),
        PLAYER_KILLER("player_killer", 0x8000u),
        HEALING("healing", 0x10000u),
        FREEZING("freezing", 0x20000u),
        BURNING("burning", 0x40000u),
        NO_MOUNT("no_mount", 0x80000u),
        TOXIC("toxic", 0x100000u),
        NO_DROP("no_drop", 0x8000000000000000u);

        companion object {
            fun toSet(value: ULong) = values().filter { value.and(it.bit) != 0uL }.toSet()
        }
    }

    enum class SectorType(
            @JsonValue val tag: String,
            val id: Int,
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
        DESERT("desert", 10),
        SWAMP("swamp", 11),
        UNDERWATER_GROUND("underwater_ground", 12);

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
            val description: String,
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
        get() = this.name
                .replace(Regex("[{}]."), "")
                .replace(Regex("<[^>]*>"), "")

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
