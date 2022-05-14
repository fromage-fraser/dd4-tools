package dd4.mapmaker.model

import dd4.core.model.Area
import dd4.core.model.Direction
import dd4.core.model.Exit
import dd4.core.model.Room

abstract class Cell(
        var position: Position,
        var label: String? = null
) {
    enum class Edge(
            val style: String
    ) {
        NONE("none"),
        OPEN("open"),
        DOOR_NORTH("door-north"),
        DOOR_SOUTH("door-south"),
        DOOR_EAST("door-east"),
        DOOR_WEST("door-west")
    }

    data class Link(
            val state: State,
            val targetLabel: String? = null,
            val description: String? = null
    ) {
        enum class State {
            NONE,
            UNLINKED,
            LINKED,
            JUMP
        }
    }

    abstract val style: String

    abstract fun edge(direction: Direction): Edge

    var fragment: Fragment? = null

    val type = this.javaClass.simpleName

    val northEdge; get() = edge(Direction.NORTH)

    val southEdge; get() = edge(Direction.SOUTH)

    val eastEdge; get() = edge(Direction.EAST)

    val westEdge; get() = edge(Direction.WEST)

    val hasNorthEdge; get() = edge(Direction.NORTH) != Edge.NONE

    val hasSouthEdge; get() = edge(Direction.SOUTH) != Edge.NONE

    val hasEastEdge; get() = edge(Direction.EAST) != Edge.NONE

    val hasWestEdge; get() = edge(Direction.WEST) != Edge.NONE

    val links = mutableMapOf(
            Direction.NORTH to Link(Link.State.NONE),
            Direction.SOUTH to Link(Link.State.NONE),
            Direction.EAST to Link(Link.State.NONE),
            Direction.WEST to Link(Link.State.NONE),
            Direction.UP to Link(Link.State.NONE),
            Direction.DOWN to Link(Link.State.NONE)
    )

    fun setNoLink(direction: Direction) {
        links[direction] = Link(Link.State.NONE)
    }

    fun setUnlinked(direction: Direction) {
        links[direction] = Link(Link.State.UNLINKED)
    }

    fun setLinked(direction: Direction) {
        links[direction] = Link(Link.State.LINKED)
    }

    fun setJump(direction: Direction, label: String, description: String) {
        links[direction] = Link(Link.State.JUMP, label, description)
    }

    fun isLinked(direction: Direction) =
            links[direction]?.state == Link.State.LINKED

    fun isLinkedOrHasJump(direction: Direction) =
            links[direction]?.state == Link.State.LINKED || links[direction]?.state == Link.State.JUMP
}


class EmptyCell(position: Position) : Cell(position) {

    override val style = "empty"

    override fun edge(direction: Direction) = Edge.NONE

    override fun toString(): String {
        return "EmptyCell(position=$position)"
    }
}


class RoomCell(
        position: Position,
        val room: Room,
        val flags: Set<Flag> = setOf(),
        label: String? = null
) : Cell(position, label) {

    enum class Flag(
            val style: String,
            val description: String
    ) {
        RANDOMIZED_EXITS("randomized-exits", "Randomized exits"),
        NO_MOBILES("no-mobiles", "No mobiles"),
        NO_RECALL("no-recall", "No recall"),
        HEALER("healer", "Healer"),
        SHOP("shop", "Shop"),
        TEACHER("teacher", "Teacher")
    }

    override val style = "room"

    private val linkMap: MutableMap<Direction, Link> = mutableMapOf()

    override fun edge(direction: Direction): Edge {
        val exit = room.exits[direction] ?: return Edge.NONE
        if (!exit.flags.contains(Exit.Flag.DOOR)) return Edge.OPEN

        return when (exit.direction) {
            Direction.NORTH -> Edge.DOOR_NORTH
            Direction.SOUTH -> Edge.DOOR_SOUTH
            Direction.EAST -> Edge.DOOR_EAST
            Direction.WEST -> Edge.DOOR_WEST
            else -> Edge.OPEN
        }
    }

    override fun toString(): String {
        return "RoomCell(position=$position, room=$room, flags=$flags)"
    }

    fun toRoomAndLabel(): RoomAndLabel = RoomAndLabel(room, label)
}


class AreaExitCell(
        position: Position,
        val area: Area,
        val directionToExitRoom: Direction,
        val mapReference: String
) : Cell(position) {

    override val style = "area-exit"

    override fun edge(direction: Direction): Edge = if (direction == directionToExitRoom) Edge.OPEN else Edge.NONE

    override fun toString(): String {
        return "AreaExitCell(position=$position, area=$area, directionToExitRoom=$directionToExitRoom, " +
                "mapReference='$mapReference')"
    }
}


class NorthSouthConnectorCell(
        position: Position
) : Cell(position) {

    override val style = "north-south-connector"

    override fun edge(direction: Direction): Edge =
            when (direction) {
                Direction.NORTH, Direction.SOUTH -> Edge.OPEN
                else -> Edge.NONE
            }

    override fun toString(): String {
        return "NorthSouthConnectorCell(position=$position)"
    }
}


class EastWestConnectorCell(
        position: Position
) : Cell(position) {

    override val style = "east-west-connector"

    override fun edge(direction: Direction): Edge =
            when (direction) {
                Direction.EAST, Direction.WEST -> Edge.OPEN
                else -> Edge.NONE
            }

    override fun toString(): String {
        return "EastWestConnectorCell(position=$position)"
    }
}
