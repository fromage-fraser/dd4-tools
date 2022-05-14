package dd4.mapmaker

import dd4.core.model.Direction
import dd4.core.model.Exit
import dd4.core.model.Room
import dd4.core.model.SourceFile
import dd4.core.model.SpecialFunction
import dd4.core.util.upperCaseFirst
import dd4.mapmaker.model.AreaExit
import dd4.mapmaker.model.AreaExitCell
import dd4.mapmaker.model.AreaMap
import dd4.mapmaker.model.Cell
import dd4.mapmaker.model.Fragment
import dd4.mapmaker.model.Position
import dd4.mapmaker.model.RoomAndArea
import dd4.mapmaker.model.RoomAndLabel
import dd4.mapmaker.model.RoomCell
import dd4.mapmaker.model.UnlinkedExit
import dd4.mapmaker.render.HtmlRenderer
import dd4.mapmaker.render.RenderOptions
import java.util.Deque
import java.util.LinkedList
import java.util.Queue

class SingleAreaMapGenerator(
        private val sourceFile: SourceFile,
        private val roomVnumLookup: Map<Int, RoomAndArea>,
        private val renderer: HtmlRenderer,
        private val outputDirPath: String,
        private val verbose: Boolean = false,
        private val progressiveRender: Boolean = false,
        private val mapDebug: Boolean = false
) {
    companion object {
        const val maxRoomLinkingCount = 10_000
    }

    private val area = sourceFile.area ?: throw IllegalArgumentException("Area file must provide area")
    private val areaMap = AreaMap(sourceFile.id, area)
    private val unlinkedRooms: Deque<RoomAndLabel> = buildUnlinkedRooms()
    private val unlinkedExits = mutableSetOf<UnlinkedExit>()
    private val fragmentAnchors = mutableSetOf<UnlinkedExit>()
    private val areaExits = mutableSetOf<AreaExit>()
    private val hinter = StaticHinter()
    private val labeller = Labeller()
    private val logPrefix = "${sourceFile.id}:"

    fun generate(): AreaMap {
        info("Generating map for area '${area.name}'")
        processUnlinkedRooms()
        processUnlinkedExits(createJumpsForUnlinkableExits = true, mergeFragments = false)
        processAreaExits()
        verify()
        doRender()
        return areaMap
    }

    private fun info(message: String) {
        println("$logPrefix $message")
    }

    private fun debug(message: String) {
        if (verbose) println("$logPrefix $message")
    }

    private fun doProgressiveRender() {
        if (progressiveRender) doRender()
    }

    private fun doRender() {
        val renderOptions = RenderOptions(
                renderVnums = mapDebug,
                renderLinkedExitSymbols = mapDebug,
                renderPositions = mapDebug
        )
        renderer.renderAreaMap(areaMap, outputDirPath, renderOptions)
    }

    private fun buildUnlinkedRooms(): Deque<RoomAndLabel> =
            LinkedList(sourceFile.rooms
                    // Reorder rooms so that indoor rooms are processed last: this generates better maps for cities
                    .sortedWith { a, b ->
                        val aIsInside = a.sectorType === Room.SectorType.INSIDE
                        val bIsInside = b.sectorType === Room.SectorType.INSIDE
                        when {
                            aIsInside && !bIsInside -> 1
                            !aIsInside && bIsInside -> -1
                            else -> 0
                        }
                    }
                    .map { RoomAndLabel(it) }
            )

    private fun processUnlinkedRooms() {
        var count = 0
        while (unlinkedRooms.isNotEmpty()) {
            if (++count > maxRoomLinkingCount)
                throw MapGenerationError("Giving up: failed to place rooms")

            val roomAndLabel = unlinkedRooms.removeFirst()
            val room = roomAndLabel.room
            val roomLabel = roomAndLabel.label
            val roomCellFlags = roomCellFlagsFor(room, sourceFile)

            debug("-".repeat(120))
            debug("Attempting to place $room (label ${roomLabel ?: "none"})")

            // Do we link to any room that is already placed?
            var roomLinked = processUnlinkedRoomUsingExits(room, roomLabel, roomCellFlags)

            // Does any placed room link to me?
            if (!roomLinked) roomLinked = processUnlinkedRoomUsingReverseExits(room, roomLabel, roomCellFlags)

            // Create a new fragment if we're still not placed
            if (!roomLinked) {
                debug("$room was not linked: creating new fragment")
                val fragment = areaMap.newFragment()
                val cell = RoomCell(Position.ORIGIN, room, roomCellFlags)
                fragment.addCell(cell)

                for (direction in Direction.values()) {
                    val exit = room.exit(direction)
                    if (exit == null) cell.setNoLink(direction) else cell.setUnlinked(direction)
                }

                roomLinked = true
            }

            // Note that our room  may become unlinked after exits are processed...
            if (roomLinked) unlinkedRooms.remove(roomAndLabel)

            processUnlinkedExits(createJumpsForUnlinkableExits = false, mergeFragments = true)
            doProgressiveRender()
        }
    }

    private fun processUnlinkedRoomUsingExits(
            room: Room,
            roomLabel: String?,
            roomCellFlags: Set<RoomCell.Flag>
    ): Boolean {
        var roomLinked = false

        exit@ for ((direction, exit) in room.exits) {
            if (exit.destinationVnum < 1) continue@exit
            val targetRoomAndArea = roomVnumLookup[exit.destinationVnum]

            if (targetRoomAndArea == null) {
                info("$room $exit destination vnum ${exit.destinationVnum} not mapped! (Ignoring)")
                continue@exit
            }

            if (targetRoomAndArea.area != area) {
                debug("Found exit from area: $room $exit")
                addAreaExit(exit, room, targetRoomAndArea)
                continue@exit
            }

            val hints = hinter.hintsForExit(sourceFile, room, direction)
            val forceJump = hints.any { it.type == ExitHint.Type.FORCE_JUMP }

            if (forceJump) {
                val unlinkedExit = addUnlinkedExit(exit, room, targetRoomAndArea.room, forceJump = true)
                debug("Found 'force jump' hint for exit: $unlinkedExit")
                continue@exit
            }

            if (roomLinked) {
                // Room is already linked to the map (through a previously processed exit).
                // Remember the current exit and we'll try to link things later
                val unlinkedExit = addUnlinkedExit(exit, room, targetRoomAndArea.room)
                debug("Unlinked exit for linked room: $unlinkedExit")
                continue@exit
            }

            val targetRoom = targetRoomAndArea.room
            val targetFragment = areaMap.findFragmentWithRoom(targetRoom)
            debug("$room leads $direction to $targetRoom in fragment $targetFragment")

            if (targetFragment != null) {
                // Target room exists in a fragment: attempt to link to it in that fragment
                val destinationCell = targetFragment.findCellForRoom(targetRoom)
                        ?: throw MapGenerationError("Expected cell for $room in $targetFragment")

                // Vertical exits: rely on the unlinked exit processor to create a jump
                if (direction.isVertical()) {
                    val unlinkedExit = addUnlinkedExit(exit, room, targetRoomAndArea.room)
                    debug("Adding unlinked vertical exit for linked room: $unlinkedExit")
                    continue@exit
                }

                roomLinked = addRoomToFragment(room, roomLabel, roomCellFlags, targetFragment, destinationCell,
                        direction) != null
            } else {
                // Target room does not yet exist in a fragment: keep track of an unlinked exit
                val unlinkedExit = addUnlinkedExit(exit, room, targetRoomAndArea.room)
                debug("Adding unlinked exit to unlinked room: $unlinkedExit")
                continue@exit
            }
        }

        return roomLinked
    }

    private fun processUnlinkedRoomUsingReverseExits(
            room: Room,
            roomLabel: String?,
            roomCellFlags: Set<RoomCell.Flag>
    ): Boolean {
        var roomLinked = false
        val candidateExits = unlinkedExits.union(fragmentAnchors).filter { it.targetRoom == room }

        candidate@ for (candidateExit in candidateExits) {
            if (candidateExit.exit.direction.isVertical()) continue@candidate
            if (candidateExit.forceJump) continue@candidate
            val directionToCandidate = candidateExit.exit.direction.reverse()
            val candidateRoom = candidateExit.exitRoom
            val candidateFragment = areaMap.findFragmentWithRoom(candidateRoom) ?: continue@candidate
            val candidateCell = candidateFragment.findCellForRoom(candidateRoom) ?: continue@candidate
            val addedCell = addRoomToFragment(room, roomLabel, roomCellFlags, candidateFragment, candidateCell,
                    directionToCandidate)

            if (addedCell != null) {
                roomLinked = true
                // Link to the cell, even though we may not have an exit in that direction: this keeps the
                // cells together if we later decide to move or merge things
                addedCell.setLinked(directionToCandidate)
                candidateCell.setLinked(candidateExit.exit.direction)
                break@candidate
            }
        }

        return roomLinked
    }

    private fun processUnlinkedExits(
            createJumpsForUnlinkableExits: Boolean = false,
            mergeFragments: Boolean = false
    ) {
        for (unlinkedExit in unlinkedExits.toList()) {
            val linked = processUnlinkedExit(unlinkedExit, createJumpsForUnlinkableExits, mergeFragments)
            if (linked) unlinkedExits.remove(unlinkedExit)
        }
    }

    private fun processUnlinkedExit(
            unlinkedExit: UnlinkedExit,
            createJumpsForUnlinkableExits: Boolean,
            mergeFragments: Boolean
    ): Boolean =
            if (unlinkedExit.exit.direction.isVertical())
                processUnlinkedVerticalExit(unlinkedExit)
            else
                processUnlinkedHorizontalExit(unlinkedExit, createJumpsForUnlinkableExits, mergeFragments)

    private fun processUnlinkedHorizontalExit(
            unlinkedExit: UnlinkedExit,
            createJumpsForUnlinkableExits: Boolean,
            mergeFragments: Boolean
    ): Boolean {
        if (unlinkedExit.forceJump && !createJumpsForUnlinkableExits) return false
        val exitDirection = unlinkedExit.exit.direction
        assert(exitDirection.isHorizontal())
        val reverseDirection = exitDirection.reverse()

        debug("Attempting to link unlinked exit $unlinkedExit ...")
        val sourceFragment = areaMap.findFragmentWithRoom(unlinkedExit.exitRoom) ?: run {
            debug("Source room is currently not in a fragment (skipping)")
            return false
        }

        val targetFragment = areaMap.findFragmentWithRoom(unlinkedExit.targetRoom) ?: run {
            debug("Target room is not yet in a fragment (skipping)")
            return false
        }

        if (targetFragment == sourceFragment) {
            val sourceCell = sourceFragment.findCellForRoom(unlinkedExit.exitRoom)
                    ?: throw MapGenerationError("Unable to find cell for room ${unlinkedExit.exitRoom}")

            // We might already be linked...
            if (sourceCell.isLinkedOrHasJump(exitDirection)) {
                debug("Cell is already linked in direction $exitDirection")
                return true
            }

            // Check to see whether we can link to a neighbouring cell
            val neighbouringCell = sourceFragment.cell(sourceCell.position.neighbour(exitDirection))

            if (neighbouringCell == null) {
                if (unlinkedExit.forceJump || createJumpsForUnlinkableExits) {
                    debug("Creating jump to cell in same fragment (forcing a jump)")
                    createJumpForUnlinkedExit(unlinkedExit)
                    return true
                }

                // Come back to this exit later
                return false
            }

            if (neighbouringCell !is RoomCell) {
                debug("Creating jump to cell in same fragment (neighbouring cell blocks exit)")
                createJumpForUnlinkedExit(unlinkedExit)
                return true
            }

            val neighbouringRoom = neighbouringCell.room

            if (unlinkedExit.targetRoom == neighbouringRoom) {
                debug("Linking to neighbour")
                sourceCell.setLinked(exitDirection)

                if (neighbouringRoom.hasExitTo(unlinkedExit.exitRoom, reverseDirection)) {
                    debug("Linking back from neighbour with return exit")
                    neighbouringCell.setLinked(reverseDirection)
                }

                return true
            }

            if (unlinkedExit.forceJump || createJumpsForUnlinkableExits) {
                debug("Creating jump to cell in same fragment (forcing a jump)")
                createJumpForUnlinkedExit(unlinkedExit)
                return true
            }

            // Come back to this exit later (cell might be shifted out of the way)
            return false
        }

        // Can we merge?
        if (!mergeFragments) {
            // If not, we may need to jump
            if (unlinkedExit.forceJump) {
                debug("Creating jump to cell in different fragment (forcing a jump)")
                createJumpForUnlinkedExit(unlinkedExit)
                return true
            }

            debug("Not merging fragments for this exit")
            return false
        }

        // We can't merge fragments if the exit and target rooms have incompatible exits (e.g. exit leads north to
        // target and target leads south to a third room)
        if (!roomFragmentsCanBeMerged(unlinkedExit.exitRoom, exitDirection, unlinkedExit.targetRoom)) {
            debug("Won't merge fragments with incompatible exits")
            return false
        }

        // We now have two fragments that can be merged.
        // To do this we can just take all of the rooms from one fragment and put them back in our queue for linking.
        // An ASSumption we're making: All rooms in a fragment are connected somehow, so we can move them all.
        // We add them back into the queue in an order that makes re-linking easier.
        val fragmentToMerge =
                if (targetFragment.cellCount >= sourceFragment.cellCount) sourceFragment else targetFragment
        debug("Returning rooms in $fragmentToMerge to linking queue")

        // This adds back the rooms in reverse order, which tends to improve how successfully we place them.
        // We could probably re-order them by distance from the linking point in the other fragment to make
        // linking more efficient.
        fragmentToMerge.cells()
                .filterIsInstance<RoomCell>()
                .forEach { roomCell ->
                    unlinkedRooms.addFirst(roomCell.toRoomAndLabel())
                    // Forgot about any unlinked exits
                    unlinkedExits.removeAll { unlinkedExit ->
                        unlinkedExit.exitRoom.vnum == roomCell.room.vnum
                    }
                }
        areaMap.fragments.remove(fragmentToMerge)

        // "Fragment anchors" allow certain rooms to be able to find their anchor points again (quite hacky...)
        addFragmentAnchor(unlinkedExit)
        return true
    }

    private fun processUnlinkedVerticalExit(unlinkedExit: UnlinkedExit): Boolean {
        assert(unlinkedExit.exit.direction.isVertical())
        return createJumpForUnlinkedExit(unlinkedExit)
    }

    private fun createJumpForUnlinkedExit(unlinkedExit: UnlinkedExit): Boolean {
        debug("Attempting to jump from unlinked exit $unlinkedExit ...")

        val exitRoomCell = areaMap.findCellForRoom(unlinkedExit.exitRoom) ?: run {
            debug("Exit room is currently not in a fragment (skipping)")
            return false
        }

        val targetRoomCell = areaMap.findCellForRoom(unlinkedExit.targetRoom) ?: run {
            debug("Exit room is currently not in a fragment (skipping)")
            return false
        }

        return createJumpToRoomCell(exitRoomCell, targetRoomCell, unlinkedExit.exit.direction)
    }

    private fun createJumpToRoomCell(exitCell: Cell, targetRoomCell: RoomCell, direction: Direction): Boolean {
        if (targetRoomCell.label == null) targetRoomCell.label = labeller.nextLabel()
        val label = targetRoomCell.label ?: throw MapGenerationError("Couldn't generate label for $targetRoomCell")
        val description = "${direction.description.upperCaseFirst()} to $label (${targetRoomCell.room.cleanName})"
        exitCell.setJump(direction, label, description)

        debug("Created jump from $exitCell to $targetRoomCell with label $label")
        return true
    }

    private fun roomCellFlagsFor(room: Room, sourceFile: SourceFile): Set<RoomCell.Flag> {
        val flags = mutableSetOf<RoomCell.Flag>()

        if (sourceFile.roomHasRandomizedExits(room))
            flags.add(RoomCell.Flag.RANDOMIZED_EXITS)

        if (room.isFlagged(Room.Flag.NO_MOB))
            flags.add(RoomCell.Flag.NO_MOBILES)

        if (room.isFlagged(Room.Flag.NO_RECALL))
            flags.add(RoomCell.Flag.NO_RECALL)

        if (sourceFile.roomHasMobileWithSpecialFunction(room, SpecialFunction.SPECIAL_FUNCTION_ADEPT))
            flags.add(RoomCell.Flag.HEALER)

        if (sourceFile.roomHasShop(room))
            flags.add(RoomCell.Flag.SHOP)

        if (sourceFile.roomHasTeacher(room))
            flags.add(RoomCell.Flag.TEACHER)

        return flags
    }

    private fun addAreaExit(exit: Exit, exitRoom: Room, targetRoomAndArea: RoomAndArea): AreaExit {
        val areaExit = AreaExit(exitRoom, exit, targetRoomAndArea.area, targetRoomAndArea.sourceFile)
        areaExits.add(areaExit)
        return areaExit
    }

    private fun addUnlinkedExit(exit: Exit, exitRoom: Room, targetRoom: Room, forceJump: Boolean = false): UnlinkedExit {
        val unlinkedExit = UnlinkedExit(exit, exitRoom, targetRoom, forceJump)
        unlinkedExits.add(unlinkedExit)
        return unlinkedExit
    }

    private fun addFragmentAnchor(unlinkedExit: UnlinkedExit) {
        fragmentAnchors.add(unlinkedExit)
    }

    private fun addRoomToFragment(
            room: Room,
            roomLabel: String?,
            roomCellFlags: Set<RoomCell.Flag>,
            fragment: Fragment,
            destinationCell: Cell,
            directionToDestinationCell: Direction
    ): RoomCell? {
        val destinationPosition = destinationCell.position
        val reverseDirection = directionToDestinationCell.reverse()
        val targetPosition = destinationPosition.neighbour(reverseDirection)
        debug("Add $room to $fragment: Target position is $targetPosition")
        val existingCell = fragment.cell(targetPosition)

        if (existingCell != null) {
            if (canShiftCellsFrom(fragment, targetPosition, reverseDirection)) {
                debug("Target position is occupied by $existingCell: attempting shift")

                if (shiftCells(fragment, targetPosition, reverseDirection)) {
                    debug("Shift successful")
                } else {
                    debug("Unable to shift cells")
                    return null
                }
            } else {
                debug("Target position is occupied by $existingCell and shift is not possible")
                return null
            }
        }

        val cell = RoomCell(targetPosition, room, roomCellFlags, roomLabel)
        // Note: the destination room cell may lead to us, but we don't necessarily lead back to it.
        val destinationCellRoom = if (destinationCell is RoomCell) destinationCell.room else null

        for (direction in Direction.values()) {
            val exit = room.exit(direction)

            if (exit == null) {
                cell.setNoLink(direction)
            }
            else if (direction == directionToDestinationCell) {
                val targetRoom = sourceFile.findRoomByVnum(exit.destinationVnum)

                if (exit.hasDestinationRoom() && targetRoom == null) {
                    //TODO: Need to support area exits?
                    throw MapGenerationError("Target room not found in area: $room $exit")
                }

                if (destinationCellRoom == null) {
                    cell.setUnlinked(direction)
                }
                else if (direction.isVertical()) {
                    cell.setUnlinked(direction)
                }
                else if (targetRoom == null) {
                    // Probably wrong...
                    cell.setNoLink(direction)
                }
                else if (destinationCellRoom == targetRoom) {
                    // We lead to the destination room
                    cell.setLinked(direction)
                    if (room.hasReturnExitTo(destinationCellRoom, direction)) {
                        // And the destination room links back to us
                        destinationCell.setLinked(direction.reverse())
                    }
                }
                else {
                    addUnlinkedExit(exit, room, targetRoom)
                    cell.setUnlinked(direction)
                }
            }
            else {
                cell.setUnlinked(direction)
            }
        }

        debug("Placing new cell $cell in free position")
        fragment.addCell(cell)
        return cell
    }

    private fun canShiftCellsFrom(fragment: Fragment, targetPosition: Position, shiftDirection: Direction): Boolean {
        val targetCell = fragment.cell(targetPosition) ?: return false
        if (targetCell.isLinked(shiftDirection.reverse())) return false
        val neighbourCell = fragment.cell(targetPosition.neighbour(shiftDirection.reverse())) ?: return true
        if (neighbourCell.isLinked(shiftDirection)) return false
        return true
    }

    private fun shiftCells(fragment: Fragment, targetPosition: Position, shiftDirection: Direction): Boolean {
        val cellsToShift = mutableSetOf<Cell>()

        // Find all the cells in front of us that can be shifted, plus those that are behind our boundary and need
        // to be separately checked to see if they can be shifted too (preventing them from being "left behind")
        val (shiftableCells, cellsBehindBoundary) =
                findShiftableCellsFromBoundaryForward(fragment, targetPosition, shiftDirection)
        cellsToShift.addAll(shiftableCells)

        // Attempt to move blocks behind the boundary
        for (cell in cellsBehindBoundary) {
            cellsToShift.addAll(findShiftableCellsBehindBoundary(fragment, cell, targetPosition, shiftDirection,
                    cellsToShift))
        }

        // Now we have all cells we know we can shift: sort them by distance from the boundary to make moving them
        // easier.
        val cellsToShiftByDistanceFromBoundary = cellsToShift.sortedWith { a, b ->
            when (shiftDirection) {
                Direction.NORTH -> b.position.y.compareTo(a.position.y)
                Direction.SOUTH -> a.position.y.compareTo(b.position.y)
                Direction.EAST -> b.position.x.compareTo(a.position.x)
                Direction.WEST -> a.position.x.compareTo(b.position.x)
                else -> 0
            }
        }

        // Finally, shift the cells as all target positions will be clear
        for (cell in cellsToShiftByDistanceFromBoundary) {
            val fromPosition = cell.position
            val toPosition = fromPosition.neighbour(shiftDirection)

            fragment.cell(toPosition)?.let {
                throw MapGenerationError("Expected position $toPosition to be empty when shifting $cell " +
                        "$shiftDirection (found $it)")
            }

            // Do we need to add a connector?
            var connectCells = false

            // We're not at a N/S boundary, so no new connectors required
            if ((shiftDirection == Direction.NORTH || shiftDirection == Direction.SOUTH)
                    && fromPosition.y != targetPosition.y) {
                connectCells = false
            }
            // We're not at an E/W boundary, so no new connectors required
            else if ((shiftDirection == Direction.EAST || shiftDirection == Direction.WEST)
                    && fromPosition.x != targetPosition.x) {
                connectCells = false
            }
            // There is a cell behind the boundary that will move up to occupy this space
            else if (cellsToShift.any { it.position == fromPosition.neighbour(shiftDirection.reverse()) }) {
                connectCells = false
            }
            // Our cell is linked to the neighbour it will be shifted away from
            else if (cell.isLinked(shiftDirection.reverse())) {
                connectCells = true
            } else {
                val neighbourCell = fragment.cell(fromPosition.neighbour(shiftDirection.reverse()))
                // The neighbour we are shifting away from is linked to us
                if (neighbourCell != null && neighbourCell.isLinked(shiftDirection)) {
                    connectCells = true
                }
            }

            fragment.moveCell(cell, toPosition)
            if (connectCells) fragment.addConnectorCell(fromPosition, shiftDirection)
        }

        return true
    }

    private fun findShiftableCellsFromBoundaryForward(
            fragment: Fragment,
            targetPosition: Position,
            shiftDirection: Direction
    ): Pair<Set<Cell>, Set<Cell>> {
        val boundaryX = targetPosition.x
        val boundaryY = targetPosition.y
        val cellsSeen = mutableSetOf<Cell>()
        val cellsToShift = mutableSetOf<Cell>()
        val cellsBehindBoundary = mutableSetOf<Cell>()
        val searchQueue: Queue<Cell> = LinkedList()

        fragment.cell(targetPosition).let { searchQueue.add(it) }

        queue@ while (searchQueue.isNotEmpty()) {
            val cell = searchQueue.remove()
            cellsToShift.add(cell)
            if (cellsSeen.contains(cell)) continue@queue
            cellsSeen.add(cell)

            direction@ for (direction in Direction.HORIZONTAL_DIRECTIONS) {
                val neighbourPosition = cell.position.neighbour(direction)
                val neighbourCell = fragment.cell(neighbourPosition) ?: continue@direction

                // When the neighbour is "behind" our boundary we cannot necessarily shift it forward: it may be
                // connected to other rooms that are blocked from shifting.
                val behindBoundary = when (shiftDirection) {
                    Direction.NORTH -> neighbourPosition.y < boundaryY
                    Direction.SOUTH -> neighbourPosition.y > boundaryY
                    Direction.WEST -> neighbourPosition.x > boundaryX
                    Direction.EAST -> neighbourPosition.x < boundaryX
                    else -> false
                }

                if (behindBoundary) {
                    // Remember neighbours behind the boundary and check later whether its "block" can be  shifted.
                    // If it can this makes the map much neater. If it can't we will leave it behind with a connector.
                    // However, don't do this if we are behind our target cell...
                    if (cell.position != targetPosition) cellsBehindBoundary.add(neighbourCell)
                    continue@direction
                }

                if (direction == shiftDirection
                        || cell.isLinked(direction)
                        || neighbourCell.isLinked(direction.reverse())) {
                    searchQueue.add(neighbourCell)
                }
            }
        }

        return Pair(cellsToShift, cellsBehindBoundary)
    }

    private fun findShiftableCellsBehindBoundary(
            fragment: Fragment,
            rootCell: Cell,
            targetPosition: Position,
            shiftDirection: Direction,
            cellsBeyondBoundaryBeingShifted: Set<Cell>
    ): Set<Cell> {
        val cellsSeen = mutableSetOf<Cell>()
        val cellsToShift = mutableSetOf<Cell>()
        val searchQueue: Queue<Cell> = LinkedList()

        searchQueue.add(rootCell)

        // First, find all the rooms that form a connected block behind the boundary
        queue@ while (searchQueue.isNotEmpty()) {
            val cell = searchQueue.remove()
            cellsToShift.add(cell)
            if (cellsSeen.contains(cell)) continue@queue
            cellsSeen.add(cell)

            direction@ for (direction in Direction.HORIZONTAL_DIRECTIONS) {
                // If we're the root cell, don't want to include anything already being shifted
                if (cell == rootCell && direction == shiftDirection) continue@direction
                val neighbourCell = fragment.findCellNeighbour(cell, direction) ?: continue@direction
                if (cell.isLinked(direction) || neighbourCell.isLinked(direction.reverse()))
                    searchQueue.add(neighbourCell)
            }
        }

        // Next, check whether they all can be shifted up
        check@ for (cell in cellsToShift) {
            val positionToOccupy = cell.position.neighbour(shiftDirection)

            // Can't shift into the position we are trying to occupy...
            if (positionToOccupy == targetPosition) return emptySet()

            // Empty cells can be occupied
            val cellInOccupiedPosition = fragment.cell(positionToOccupy) ?: continue@check

            // OK to shift into a position our block already occupies
            if (cellsToShift.contains(cellInOccupiedPosition)) continue@check

            // OK to shift into a position occupied by a cell we intend to shift
            if (cellsBeyondBoundaryBeingShifted.contains(cellInOccupiedPosition)) continue@check

            // Else we have bumped into cell not in our block: we cannot shift the block
            return emptySet()
        }

        return cellsToShift
    }

    private fun roomFragmentsCanBeMerged(fromRoom: Room, direction: Direction, toRoom: Room): Boolean {
        val fromRoomHasExit = fromRoom.hasExit(direction)
        val toRoomHasExit = toRoom.hasExit(direction.reverse())
        val fromRoomIsLinked = fromRoom.exit(direction)?.destinationVnum == toRoom.vnum
        val toRoomIsLinked = toRoom.exit(direction.reverse())?.destinationVnum == fromRoom.vnum

        if (fromRoomIsLinked || !toRoomHasExit) return true
        if (toRoomIsLinked || !fromRoomHasExit) return true
        if (fromRoomIsLinked && toRoomIsLinked) return true

        return false
    }

    private fun processAreaExits() {
        for (areaExit in areaExits) {
            processAreaExit(areaExit)
            doProgressiveRender()
        }
    }

    private fun processAreaExit(areaExit: AreaExit) {
        debug("Attempting to link area exit $areaExit...")
        val targetFragment = areaMap.findFragmentWithRoom(areaExit.room)
                ?: throw MapGenerationError("Unable to find fragment for $areaExit")
        val exitRoomCell = targetFragment.findCellForRoom(areaExit.room)
                ?: throw MapGenerationError("Unable to find exit room $areaExit")
        val exitDirection = areaExit.exit.direction

        // Can we add alongside the exit room without shifting cells around?
        if (exitDirection.isHorizontal()) {
            val exitRoomPosition = exitRoomCell.position
            val targetPosition = exitRoomPosition.neighbour(exitDirection)
            debug("Area exit target position is $targetPosition")

            if (!targetFragment.hasCellAt(targetPosition)) {
                val areaExitCell = AreaExitCell(targetPosition, areaExit.targetArea, exitDirection.reverse(),
                        renderer.fileNameFor(areaExit.targetSourceFile.id))
                debug("Placing area exit cell $areaExitCell in free position")
                targetFragment.addCell(areaExitCell)
                exitRoomCell.setLinked(exitDirection)
                return
            }
        }

        // Create a new fragment to hold the area exit
        val areaExitCell = AreaExitCell(Position.ORIGIN, areaExit.targetArea, exitDirection.reverse(),
                renderer.fileNameFor(areaExit.targetSourceFile.id))
        val exitLabel = labeller.nextLabel()
        areaExitCell.label = exitLabel

        val fragment = areaMap.newFragment()
        fragment.addCell(areaExitCell)

        val description = "${exitDirection.description.upperCaseFirst()} to $exitLabel (${areaExit.targetArea.name})"
        exitRoomCell.setJump(exitDirection, exitLabel, description)
        createJumpToRoomCell(areaExitCell, exitRoomCell, exitDirection.reverse())
    }

    private fun verify() {
        if (unlinkedRooms.isNotEmpty())
            throw MapGenerationError("${unlinkedRooms.size} rooms were not linked!")

        if (unlinkedExits.isNotEmpty())
            throw MapGenerationError("${unlinkedExits.size} exits were not linked!")
    }
}
