package dd4.mapmaker.model

import dd4.core.model.Room

data class RoomAndLabel(
        val room: Room,
        val label: String? = null,
)
