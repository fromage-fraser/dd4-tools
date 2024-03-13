package dd4.mapmaker.model

import dd4.core.model.Exit
import dd4.core.model.Room

data class UnlinkedExit(
        val exit: Exit,
        val exitRoom: Room,
        val targetRoom: Room,
        val forceJump: Boolean = false,
)
