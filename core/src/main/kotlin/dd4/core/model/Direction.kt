package dd4.core.model

import com.fasterxml.jackson.annotation.JsonValue

enum class Direction(
    @JsonValue val tag: String,
    val id: Int,
    val description: String,
    val deltaX: Int,
    val deltaY: Int,
    val deltaZ: Int,
) {
    NORTH("n", 0, "north", 0, 1, 0),
    EAST("e", 1, "east", 1, 0, 0),
    SOUTH("s", 2, "south", 0, -1, 0),
    WEST("w", 3, "west", -1, 0, 0),
    UP("u", 4, "up", 0, 0, 1),
    DOWN("d", 5, "down", 0, 0, -1),
    ;

    companion object {
        fun fromId(value: Int) = try {
            entries.first { it.id == value }
        } catch (_: NoSuchElementException) {
            throw IllegalArgumentException("Invalid direction ID: $value")
        }

        fun fromTag(value: String) = try {
            entries.first { it.tag == value }
        } catch (_: NoSuchElementException) {
            throw IllegalArgumentException("Invalid direction tag: $value")
        }

        val HORIZONTAL_DIRECTIONS = setOf(NORTH, EAST, SOUTH, WEST)
    }

    fun reverse(): Direction = when (this) {
        NORTH -> SOUTH
        SOUTH -> NORTH
        EAST -> WEST
        WEST -> EAST
        UP -> DOWN
        DOWN -> UP
    }

    fun isVertical() = this !in HORIZONTAL_DIRECTIONS

    fun isHorizontal() = this in HORIZONTAL_DIRECTIONS
}
