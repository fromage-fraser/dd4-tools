package dd4.areaquery

import dd4.core.file.AreaFileMapper
import dd4.core.model.Area
import dd4.core.model.AreaSpecial
import dd4.core.model.Item
import dd4.core.model.Level
import dd4.core.model.Mobile
import dd4.core.model.Reset
import dd4.core.model.Room
import java.lang.Integer.max
import java.lang.Integer.min

class QueryDbGenerator(
    private val areaFilePath: String,
    private val areaFileMapper: AreaFileMapper,
) {
    private companion object {
        const val ITEM_LEVEL_VERSUS_MOBILE_MODIFIER = -2
        const val ITEM_LEVEL_FUZZ = 1
        const val SCHOOL_LEVEL_MAX = 5

        val MPOLOAD_PATTERN =
            Regex(""".*\bmpoload\s+(\d+)(?:\s+(\d+))?\b.*""", RegexOption.IGNORE_CASE)
    }

    private data class MobProgToProcess(val mobile: Mobile, val mobProg: String)

    private val currentRoomForItemLookup: MutableMap<Int, Room> = mutableMapOf()
    private val currentRoomForMobileLookup: MutableMap<Int, Room> = mutableMapOf()
    private val currentLevelForItemLookup: MutableMap<Int, Int> = mutableMapOf()
    private val mobProgsToProcess: MutableList<MobProgToProcess> = mutableListOf()
    private var currentMobile: Mobile? = null
    private var currentRoom: Room? = null
    private var currentItemLevel = 0

    fun generate(): QueryDb {
        currentRoomForItemLookup.clear()
        currentRoomForMobileLookup.clear()
        currentLevelForItemLookup.clear()
        mobProgsToProcess.clear()
        currentMobile = null
        currentRoom = null
        currentItemLevel = 0

        val areaFiles = areaFileMapper.readFromFile(areaFilePath)
        val entityLookup = EntityLookup(areaFiles)
        val queryDb = QueryDb()

        for (areaFile in areaFiles) {
            val area = areaFile.area ?: continue
            val areaSpecial = areaFile.areaSpecial
            queryDb.registerArea(area)

            for (reset in areaFile.resets) {
                when (reset.type) {
                    Reset.Type.MOBILE_TO_ROOM -> processMobileToRoomReset(
                        area,
                        reset,
                        queryDb,
                        entityLookup,
                    )

                    Reset.Type.OBJECT_TO_ROOM -> processObjectToRoomReset(
                        area,
                        reset,
                        queryDb,
                        entityLookup,
                    )

                    Reset.Type.OBJECT_TO_ROOM_EXTENDED ->
                        processObjectToRoomExtendedReset(area, reset, queryDb, entityLookup)

                    Reset.Type.OBJECT_TO_MOBILE_EQUIPMENT,
                    Reset.Type.OBJECT_TO_MOBILE_INVENTORY,
                    ->
                        processObjectToMobileReset(areaSpecial, reset, queryDb, entityLookup)

                    Reset.Type.OBJECT_TO_OBJECT -> processObjectToObjectReset(
                        reset,
                        queryDb,
                        entityLookup,
                    )

                    Reset.Type.RANDOMIZE_EXITS,
                    Reset.Type.DOOR,
                    -> {
                    }

                    else -> TODO("Unsupported reset type: ${reset.type}")
                }
            }

            for (mobProgAssignment in areaFile.mobProgAssignments) {
                val mobile = entityLookup.mobile(mobProgAssignment.mobileVnum)
                entityLookup.mobProgFile(mobProgAssignment.fileName).mobProgs.map { it.commands }
                    .map { MobProgToProcess(mobile, it) }
                    .forEach { mobProgsToProcess.add(it) }
            }

            for (mobile in areaFile.mobiles) {
                mobile.mobProgs.map { it.commands }
                    .map { MobProgToProcess(mobile, it) }
                    .forEach { mobProgsToProcess.add(it) }
            }
        }

        mobProgsToProcess.forEach { mobProg ->
            val mobile = mobProg.mobile

            mobProg.mobProg.lines().forEach { line ->
                MPOLOAD_PATTERN.matchEntire(line)?.let { match ->
                    val itemVnum = match.groupValues[1].toInt()
                    val level = when (val levelRaw = match.groupValues[2]) {
                        "" -> mobile.level
                        else -> levelRaw.toInt()
                    }

                    val item = entityLookup.item(itemVnum)
                    val queryDbItem = queryDb.registerItem(item)
                    queryDbItem.addMobProg(level, mobile, currentRoomForMobileLookup[mobile.vnum])
                    queryDb.registerMobile(mobile)
                }
            }
        }

        return queryDb
    }

    private fun processMobileToRoomReset(
        area: Area,
        reset: Reset,
        queryDb: QueryDb,
        entityLookup: EntityLookup,
    ) {
        // M: - <mobile vnum> <max count> <room vnum>
        val mobileVnum = reset.arg1
        val roomVnum = reset.arg3

        val mobile = entityLookup.mobile(mobileVnum)
        val room = entityLookup.room(roomVnum)

        currentMobile = mobile
        currentRoom = room
        currentRoomForMobileLookup[mobileVnum] = room
        currentItemLevel = validLevel(mobile.level + ITEM_LEVEL_VERSUS_MOBILE_MODIFIER)

        queryDb.registerMobile(mobile, entityLookup.shop(mobile.vnum))
        queryDb.registerRoom(room, area)
    }

    private fun processObjectToRoomReset(
        area: Area,
        reset: Reset,
        queryDb: QueryDb,
        entityLookup: EntityLookup,
    ) {
        // O: - <object vnum> - <room vnum>
        val itemVnum = reset.arg1
        val roomVnum = reset.arg3

        val item = entityLookup.item(itemVnum)
        val room = entityLookup.room(roomVnum)

        currentLevelForItemLookup[itemVnum] = currentItemLevel
        currentRoomForItemLookup[itemVnum] = room

        queryDb.registerRoom(room, area)
        val queryDbItem = queryDb.registerItem(item)

        queryDbItem.addReset(
            type = reset.type,
            levelMin = validLevel(currentItemLevel - ITEM_LEVEL_FUZZ),
            levelMax = validLevel(currentItemLevel + ITEM_LEVEL_FUZZ),
            inRoom = room,
        )
    }

    private fun processObjectToRoomExtendedReset(
        area: Area,
        reset: Reset,
        queryDb: QueryDb,
        entityLookup: EntityLookup,
    ) {
        // E: <object vnum> <object level> <room vnum> <max count in room>
        val itemVnum = reset.arg0
        val resetItemLevel = reset.arg1
        val roomVnum = reset.arg2

        val item = entityLookup.item(itemVnum)
        val room = entityLookup.room(roomVnum)

        currentLevelForItemLookup[itemVnum] = currentItemLevel
        currentRoomForItemLookup[itemVnum] = room

        queryDb.registerRoom(room, area)
        val queryDbItem = queryDb.registerItem(item)

        queryDbItem.addReset(
            type = reset.type,
            levelMin = validLevel(resetItemLevel - ITEM_LEVEL_FUZZ),
            levelMax = validLevel(resetItemLevel + ITEM_LEVEL_FUZZ),
            inRoom = room,
        )
    }

    private fun processObjectToMobileReset(
        areaSpecial: AreaSpecial?,
        reset: Reset,
        queryDb: QueryDb,
        entityLookup: EntityLookup,
    ) {
        // E: Values: - <object vnum> - <wear location>
        // G: Values: - <object vnum> - -
        val itemVnum = reset.arg1

        val item = entityLookup.item(itemVnum)
        var levelMin = validLevel(currentItemLevel - ITEM_LEVEL_FUZZ)
        var levelMax = validLevel(currentItemLevel + ITEM_LEVEL_FUZZ)

        val mobile = currentMobile
        val room = currentRoom

        if (mobile != null && entityLookup.shop(mobile.vnum) != null) {
            if (item.level == 0) {
                when (item.type) {
                    Item.Type.PILL,
                    Item.Type.PAINT,
                    Item.Type.POTION,
                    -> {
                        levelMin = 0
                        levelMax = 10
                    }

                    Item.Type.SCROLL,
                    Item.Type.ARMOUR,
                    -> {
                        levelMin = 5
                        levelMax = 15
                    }

                    Item.Type.WAND -> {
                        levelMin = 10
                        levelMax = 20
                    }

                    Item.Type.STAFF -> {
                        levelMin = 15
                        levelMax = 25
                    }

                    Item.Type.WEAPON -> {
                        when (reset.type) {
                            Reset.Type.OBJECT_TO_MOBILE_INVENTORY -> {
                                levelMin = 5
                                levelMax = 15
                            }

                            else -> {}
                        }
                    }

                    else -> {
                        levelMin = 0
                        levelMax = 0
                    }
                }
            } else {
                levelMin = validLevel(item.level, Level.HERO - 1)
                levelMax = levelMin
            }
            currentLevelForItemLookup[itemVnum] = levelMin

            if (reset.type == Reset.Type.OBJECT_TO_MOBILE_INVENTORY) {
                item.extraFlags.add(Item.ExtraFlag.INVENTORY)
            }
        } else {
            currentLevelForItemLookup[itemVnum] = currentItemLevel
        }

        if (areaSpecial?.isFlagged(AreaSpecial.AreaFlag.SCHOOL) == true &&
            currentItemLevel <= SCHOOL_LEVEL_MAX
        ) {
            levelMin = 1
            levelMax = 1
        }

        room?.let { currentRoomForItemLookup[itemVnum] = it }
        val queryDbItem = queryDb.registerItem(item)

        queryDbItem.addReset(
            type = reset.type,
            levelMin = levelMin,
            levelMax = levelMax,
            inRoom = room,
            carriedBy = mobile,
        )
    }

    private fun processObjectToObjectReset(
        reset: Reset,
        queryDb: QueryDb,
        entityLookup: EntityLookup,
    ) {
        // P: - <object to place vnum> - <container object vnum>
        val itemVnum = reset.arg1
        val containerVnum = reset.arg3

        val item = entityLookup.item(itemVnum)
        val container = entityLookup.item(containerVnum)
        val containerRoom = currentRoomForItemLookup[containerVnum]
        containerRoom?.let { currentRoomForItemLookup[itemVnum] = it }
        val containerLevel = currentLevelForItemLookup[container.vnum] ?: 0
        currentLevelForItemLookup[itemVnum] = containerLevel

        val queryDbItem = queryDb.registerItem(item)
        queryDb.registerItem(container)

        queryDbItem.addReset(
            type = reset.type,
            levelMin = validLevel(containerLevel - ITEM_LEVEL_FUZZ),
            levelMax = validLevel(containerLevel + ITEM_LEVEL_FUZZ),
            inContainer = container,
            inRoom = containerRoom,
        )
    }

    private fun validLevel(level: Int, max: Int = Level.HERO): Int = min(max(level, 0), max)
}
