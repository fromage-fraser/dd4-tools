package dd4.areaquery

import dd4.core.model.Area
import dd4.core.model.Item
import dd4.core.model.Mobile
import dd4.core.model.Reset
import dd4.core.model.Room
import dd4.core.model.Shop

class QueryDb {

    private val areaRecords: MutableMap<String, QueryDbArea> = mutableMapOf()
    private val roomRecords: MutableMap<Int, QueryDbRoom> = mutableMapOf()
    private val itemRecords: MutableMap<Int, QueryDbItem> = mutableMapOf()
    private val mobileRecords: MutableMap<Int, QueryDbMobile> = mutableMapOf()

    fun areas(): List<QueryDbArea> = areaRecords.values.sortedBy { it.area.id.lowercase() }.toList()

    fun registerArea(area: Area): QueryDbArea = areaRecords.computeIfAbsent(area.id) {
        QueryDbArea(area)
    }

    fun rooms(): List<QueryDbRoom> = roomRecords.values.sortedBy { it.room.vnum }.toList()

    fun registerRoom(room: Room, area: Area): QueryDbRoom = roomRecords.computeIfAbsent(room.vnum) {
        QueryDbRoom(room, area)
    }

    fun items(): List<QueryDbItem> = itemRecords.values.sortedBy { it.item.vnum }.toList()

    fun registerItem(item: Item): QueryDbItem = itemRecords.computeIfAbsent(item.vnum) {
        QueryDbItem(item)
    }

    fun mobiles(): List<QueryDbMobile> = mobileRecords.values.sortedBy { it.mobile.vnum }.toList()

    fun registerMobile(mobile: Mobile, shop: Shop? = null): QueryDbMobile =
        mobileRecords.computeIfAbsent(mobile.vnum) {
            QueryDbMobile(mobile, shop)
        }
}

data class QueryDbArea(val area: Area)

data class QueryDbRoom(val room: Room, val area: Area)

data class QueryDbItem(
    val item: Item,
    val resets: MutableSet<QueryDbItemReset> = mutableSetOf(),
    val mobProgs: MutableSet<QueryDbItemMobProg> = mutableSetOf(),
) {
    fun addReset(
        type: Reset.Type,
        levelMin: Int,
        levelMax: Int,
        inRoom: Room? = null,
        inContainer: Item? = null,
        carriedBy: Mobile? = null,
    ) {
        resets.add(
            QueryDbItemReset(
                type = type,
                levelMin = levelMin,
                levelMax = levelMax,
                inRoom = inRoom,
                inContainer = inContainer,
                carriedBy = carriedBy,
            ),
        )
    }

    fun addMobProg(level: Int, mobile: Mobile, inRoom: Room? = null) {
        mobProgs.add(
            QueryDbItemMobProg(
                level = level,
                mobile = mobile,
                inRoom = inRoom,
            ),
        )
    }
}

data class QueryDbItemReset(
    val type: Reset.Type,
    val levelMin: Int,
    val levelMax: Int,
    val inRoom: Room?,
    val inContainer: Item?,
    val carriedBy: Mobile?,
)

data class QueryDbMobile(val mobile: Mobile, val shop: Shop?)

data class QueryDbItemMobProg(val level: Int, val mobile: Mobile, val inRoom: Room?)
