package dd4.mapmaker.model

import dd4.core.model.Area
import dd4.core.model.Room
import dd4.core.model.SourceFile

class RoomAndArea(val room: Room, val sourceFile: SourceFile) {
    init {
        requireNotNull(sourceFile.area)
    }

    val area: Area
        get() = sourceFile.area!!
}
