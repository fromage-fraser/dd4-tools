package dd4.core.model

data class SourceFile(
    val id: String,
    val fileName: String,
    val filePath: String,
    var area: Area? = null,
    var areaSpecial: AreaSpecial? = null,
    var recall: Recall? = null,
    val mobiles: List<Mobile> = mutableListOf(),
    val mobProgAssignments: List<MobProgAssignment> = mutableListOf(),
    val mobProgFiles: List<MobProgFile> = mutableListOf(),
    val objects: List<Item> = mutableListOf(),
    val objectSets: List<ItemSet> = mutableListOf(),
    val rooms: List<Room> = mutableListOf(),
    val roomAmbientSounds: List<RoomAmbientSound> = mutableListOf(),
    val resets: List<Reset> = mutableListOf(),
    val shops: List<Shop> = mutableListOf(),
    val specialFunctions: List<SpecialFunction> = mutableListOf(),
    val helps: List<Help> = mutableListOf(),
    val games: List<Game> = mutableListOf(),
    val exitSounds: List<ExitSound> = mutableListOf(),
) {
    override fun toString(): String = "SourceFile(id='$id', fileName='$fileName')"

    fun addMobiles(toAdd: Collection<Mobile>) = (mobiles as MutableList).addAll(toAdd)

    fun addMobProgAssignments(toAdd: Collection<MobProgAssignment>) =
        (mobProgAssignments as MutableList).addAll(toAdd)

    fun addObjects(toAdd: Collection<Item>) = (objects as MutableList).addAll(toAdd)

    fun addObjectSets(toAdd: Collection<ItemSet>) = (objectSets as MutableList).addAll(toAdd)

    fun addRooms(toAdd: Collection<Room>) = (rooms as MutableList).addAll(toAdd)

    fun addRoomAmbientSounds(toAdd: Collection<RoomAmbientSound>) =
        (roomAmbientSounds as MutableList).addAll(toAdd)

    fun addResets(toAdd: Collection<Reset>) = (resets as MutableList).addAll(toAdd)

    fun addShops(toAdd: Collection<Shop>) = (shops as MutableList).addAll(toAdd)

    fun addSpecialFunctions(toAdd: Collection<SpecialFunction>) =
        (specialFunctions as MutableList).addAll(toAdd)

    fun addHelps(toAdd: Collection<Help>) = (helps as MutableList).addAll(toAdd)

    fun addGames(toAdd: Collection<Game>) = (games as MutableList).addAll(toAdd)

    fun addMobProgFiles(toAdd: Collection<MobProgFile>) {
        toAdd.forEach { mobProgFile ->
            if (mobProgFiles.none { it.fileName == mobProgFile.fileName }) {
                mobProgFiles as MutableList
                mobProgFiles.add(mobProgFile)
            }
        }
    }

    fun addExitSounds(toAdd: Collection<ExitSound>) = (exitSounds as MutableList).addAll(toAdd)

    fun findRoomByVnum(vnum: Int): Room? = rooms.firstOrNull { it.vnum == vnum }

    fun findMobileByVnum(vnum: Int): Mobile? = mobiles.firstOrNull { it.vnum == vnum }

    fun roomHasRandomizedExits(room: Room): Boolean {
        checkRoomInArea(room)
        return resets.any { it.type == Reset.Type.RANDOMIZE_EXITS && it.arg1 == room.vnum }
    }

    fun roomHasMobileWithSpecialFunction(room: Room, specialFunctionName: String): Boolean {
        checkRoomInArea(room)

        return mobilesFor(room).any { mobile ->
            specialFunctionsFor(mobile).any { specialFunction ->
                specialFunction.function == specialFunctionName
            }
        }
    }

    fun roomHasShop(room: Room): Boolean {
        checkRoomInArea(room)

        return mobilesFor(room).any { mobile ->
            shops.any { it.keeperVnum == mobile.vnum }
        }
    }

    fun roomHasTeacher(room: Room): Boolean {
        checkRoomInArea(room)
        return mobilesFor(room).any { it.isTeacher() }
    }

    fun specialFunctionsFor(mobile: Mobile): List<SpecialFunction> {
        checkMobileInArea(mobile)
        return specialFunctions.filter { it.mobileVnum == mobile.vnum }
    }

    fun mobilesFor(room: Room): List<Mobile> =
        resets.filter { it.type == Reset.Type.MOBILE_TO_ROOM }
            .filter { it.arg3 == room.vnum }
            .mapNotNull { findMobileByVnum(it.arg1) }

    private fun checkRoomInArea(room: Room) {
        require(rooms.contains(room)) { "Room not found in area: $room" }
    }

    private fun checkMobileInArea(mobile: Mobile) {
        require(mobiles.contains(mobile)) { "Mobile not found in area: $mobile" }
    }
}
