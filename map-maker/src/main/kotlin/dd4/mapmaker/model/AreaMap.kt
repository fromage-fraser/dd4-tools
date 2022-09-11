package dd4.mapmaker.model

import dd4.core.model.Area
import dd4.core.model.Room

class AreaMap(
        val id: String,
        val area: Area
) {
    val fragments: MutableList<Fragment> = mutableListOf()

    private var fragmentCount = 0

    fun newFragment(addToFront: Boolean = false): Fragment {
        val fragment = Fragment()
        fragmentCount++
        if (addToFront) fragments.add(0, fragment) else fragments.add(fragment)
        return fragment
    }

    fun findFragmentWithRoom(room: Room): Fragment? = fragments.firstOrNull { it.hasRoom(room) }

    fun findCellForRoom(room: Room): RoomCell? =
            fragments.map { it.findCellForRoom(room) }
                    .filterNotNull()
                    .firstOrNull()

    fun name() = area.name

    fun author() = area.author

    fun levelDescription() =
            when {
                area.lowLevel == Area.LEVEL_ALL -> "All levels"
                area.isClanHeadquarters() -> "Clan headquarters"
                area.lowLevel > 0 && area.highLevel == area.lowLevel -> "Level ${area.lowLevel}"
                area.lowLevel > 0 && area.highLevel > 0 -> "Levels ${area.lowLevel}-${area.highLevel}"
                else -> null
            }
}
