package dd4.areaquery

import dd4.core.model.Item
import dd4.core.model.MobProgFile
import dd4.core.model.Mobile
import dd4.core.model.Room
import dd4.core.model.Shop
import dd4.core.model.SourceFile

class EntityLookup(sourceFiles: List<SourceFile>) {

    private val itemVnumLookup: Map<Int, Item>
    private val mobileVnumLookup: Map<Int, Mobile>
    private val roomVnumLookup: Map<Int, Room>
    private val shopVnumLookup: Map<Int, Shop>
    private val mobProgFileLookup: Map<String, MobProgFile>

    init {
        itemVnumLookup = sourceFiles
                .flatMap { areaFile ->
                    areaFile.objects.map { item ->
                        Pair(item.vnum, item)
                    }
                }
                .toMap()

        mobileVnumLookup = sourceFiles
                .flatMap { areaFile ->
                    areaFile.mobiles.map { mobile ->
                        Pair(mobile.vnum, mobile)
                    }
                }
                .toMap()

        roomVnumLookup = sourceFiles
                .flatMap { areaFile ->
                    areaFile.rooms.map { room ->
                        Pair(room.vnum, room)
                    }
                }
                .toMap()

        shopVnumLookup = sourceFiles
                .flatMap { areaFile ->
                    areaFile.shops.map { shop ->
                        Pair(shop.keeperVnum, shop)
                    }
                }
                .toMap()

        mobProgFileLookup = sourceFiles
                .flatMap { areaFile ->
                    areaFile.mobProgFiles.map { mobProgFile ->
                        Pair(mobProgFile.fileName, mobProgFile)
                    }
                }
                .toMap()
    }

    fun item(vnum: Int) =
            itemVnumLookup[vnum] ?: throw IllegalArgumentException("Failed to load item vnum $vnum")

    fun mobile(vnum: Int) =
            mobileVnumLookup[vnum] ?: throw IllegalArgumentException("Failed to load mobile vnum $vnum")

    fun room(vnum: Int) =
            roomVnumLookup[vnum] ?: throw IllegalArgumentException("Failed to load room vnum $vnum")

    fun shop(mobileVnum: Int) =
            shopVnumLookup[mobileVnum]

    fun mobProgFile(fileName: String) =
            mobProgFileLookup[fileName] ?: throw IllegalArgumentException("Failed to load mob prog file $fileName")
}
