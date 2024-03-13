package dd4.mapmaker.model

import dd4.core.model.Direction

data class Position(
        val x: Int,
        val y: Int,
) {
    companion object {
        val ORIGIN = Position(0, 0)
    }

    override fun toString(): String {
        return "($x, $y)"
    }

    fun neighbour(direction: Direction): Position =
            when (direction) {
                Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST ->
                    Position(x + direction.deltaX, y + direction.deltaY)

                else -> throw IllegalArgumentException("Unsupported direction: $direction")
            }
}
