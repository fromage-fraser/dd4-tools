package dd4.mapmaker.model

import dd4.core.model.Area
import dd4.core.model.Exit
import dd4.core.model.Room
import dd4.core.model.SourceFile

data class AreaExit(
        val room: Room,
        val exit: Exit,
        val targetArea: Area,
        val targetSourceFile: SourceFile,
)
