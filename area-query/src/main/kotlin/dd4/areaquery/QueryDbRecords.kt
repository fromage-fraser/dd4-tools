package dd4.areaquery

data class QueryDbRecord(
        val info: InfoRecord,
        val areas: List<AreaRecord>,
        val rooms: List<RoomRecord>,
        val mobiles: List<MobileRecord>,
        val items: List<ItemRecord>,
)

data class InfoRecord(
        val creationDate: String,
)

data class AreaRecord(
        val id: String,
        val name: String,
)

data class RoomRecord(
        val vnum: Int,
        val desc: String,
        val area: String,
)

data class MobileRecord(
        val vnum: Int,
        val desc: String,
        val level: Int,
        val alignment: Int,
        val shop: Boolean,
)

data class ItemRecord(
        val vnum: Int,
        val desc: String,
        val keywords: List<String>,
        val type: String,
        val wear: List<String>,
        val flags: List<String>,
        val effects: List<EffectRecord>,
        val resets: List<ItemResetRecord>,
        val mobProgs: List<ItemMobProgRecord>?,
        val weaponAttackType: String?,
        val spellLevel: Int?,
        val spells: List<String>?,
        val currentCharges: Int?,
        val maxCharges: Int?,
        val containerCapacity: Int?,
)

data class EffectRecord(
        val attr: String,
        val mod: Int,
)

data class ItemResetRecord(
        val type: String,
        val levelMin: Int,
        val levelMax: Int,
        val inRoom: Int?,
        val inContainer: Int?,
        val carriedBy: Int?,
)

data class ItemMobProgRecord(
        val level: Int,
        val mobile: Int,
        val inRoom: Int?,
)
