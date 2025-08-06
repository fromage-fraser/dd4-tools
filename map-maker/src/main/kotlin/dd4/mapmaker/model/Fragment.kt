package dd4.mapmaker.model

import dd4.core.model.Direction
import dd4.core.model.Room
import dd4.mapmaker.MapGenerationError
import java.lang.Integer.max
import java.lang.Integer.min

class Fragment {
    var minX = 0
    var maxX = 0
    var minY = 0
    var maxY = 0
    private val cells: MutableMap<Position, Cell> = mutableMapOf()

    fun cell(position: Position) = cells[position]

    fun hasCellAt(position: Position) = cell(position) != null

    fun cells() = cells.values

    val cellCount: Int
        get() = cells.size

    fun addCell(cell: Cell) {
        if (cells.containsKey(cell.position)) {
            throw MapGenerationError("Attempt to add cell to occupied position: $cell")
        }

        minX = min(minX, cell.position.x)
        maxX = max(maxX, cell.position.x)
        minY = min(minY, cell.position.y)
        maxY = max(maxY, cell.position.y)
        cells[cell.position] = cell
        cell.fragment = this
    }

    fun removeCell(cell: Cell) {
        if (!cells.containsKey(cell.position)) {
            throw MapGenerationError("Attempt to remove cell that is not in fragment: $cell")
        }
        cells.remove(cell.position)
        cell.fragment = null
        // TODO: Recalculate min/max
    }

    fun columns() = maxX - minX + 1

    fun rows() = maxY - minY + 1

    @Suppress("unused")
    fun grid(): List<List<Cell>> {
        val table = mutableListOf<List<Cell>>()

        for (y in maxY downTo minY) {
            val row = mutableListOf<Cell>()

            for (x in minX..maxX) {
                val position = Position(x, y)
                if (cells.containsKey(position)) {
                    row.add(cells[position]!!)
                } else {
                    row.add(EmptyCell(position))
                }
            }

            table.add(row)
        }

        return table
    }

    fun findCellForRoom(room: Room): RoomCell? =
        cells.values.firstOrNull { it is RoomCell && it.room == room } as RoomCell?

    fun findCellNeighbour(cell: Cell, direction: Direction): Cell? {
        checkHasCell(cell)
        return cells[cell.position.neighbour(direction)]
    }

    fun hasRoom(room: Room): Boolean = findCellForRoom(room) != null

    @Suppress("unused")
    fun labels(): Set<String> = cells.values.mapNotNull { it.label }.toSet()

    fun addConnectorCell(position: Position, direction: Direction) {
        val connectorCell = when (direction) {
            Direction.NORTH, Direction.SOUTH -> NorthSouthConnectorCell(position)
            Direction.EAST, Direction.WEST -> EastWestConnectorCell(position)
            else -> throw IllegalArgumentException("Unexpected direction: $direction")
        }
        when (connectorCell) {
            is NorthSouthConnectorCell -> {
                connectorCell.setLinked(Direction.NORTH)
                connectorCell.setLinked(Direction.SOUTH)
            }

            is EastWestConnectorCell -> {
                connectorCell.setLinked(Direction.EAST)
                connectorCell.setLinked(Direction.WEST)
            }
        }
        addCell(connectorCell)
    }

    fun moveCell(cell: Cell, newPosition: Position) {
        checkHasCell(cell)
        if (cells.containsKey(
                newPosition,
            )
        ) {
            throw IllegalArgumentException("New position not empty: $newPosition")
        }
        removeCell(cell)
        cell.position = newPosition
        addCell(cell)
    }

    private fun checkHasCell(cell: Cell) {
        require(cells.values.contains(cell)) { "Cell not in fragment: $cell" }
    }

    override fun toString(): String =
        "Fragment(minX=$minX, maxX=$maxX, minY=$minY, maxY=$maxY, cells=${cells.size})"
}
