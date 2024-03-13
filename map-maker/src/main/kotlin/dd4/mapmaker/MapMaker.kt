package dd4.mapmaker

import dd4.core.file.AreaFileMapper
import dd4.core.model.AreaSpecial
import dd4.mapmaker.model.AreaMap
import dd4.mapmaker.model.RoomAndArea
import dd4.mapmaker.render.HtmlRenderer

class MapMaker(
        private val areaFileName: String,
        private val outputDirPath: String,
        private val areaFileMapper: AreaFileMapper,
        private val renderer: HtmlRenderer,
        private val verbose: Boolean = false,
        private val dieOnError: Boolean = false,
        private val progressiveRender: Boolean = false,
        private val mapDebug: Boolean = false,
        private val areaIds: List<String> = listOf(),
) {
    companion object {
        const val VERSION = "0.3"
    }

    fun generate() {
        info("Reading areas from $areaFileName...")

        val allAreaFiles = areaFileMapper.readFromFile(areaFileName)
                .filter { it.area != null }
                .filter { it.area?.isClanHeadquarters() != true }
                .filter { it.areaSpecial?.isFlagged(AreaSpecial.AreaFlag.HIDDEN) != true }
                .filter { it.rooms.isNotEmpty() }

        val areaFilesToMap = allAreaFiles
                .filter { areaIds.isEmpty() || areaIds.contains(it.id) }

        info("Found ${allAreaFiles.size} areas, mapping ${areaFilesToMap.size}")

        val roomVnumLookup = allAreaFiles
                .flatMap { areaFile ->
                    areaFile.rooms.map { room ->
                        Pair(room.vnum, RoomAndArea(room, areaFile))
                    }
                }
                .toMap()
        info("Registered ${roomVnumLookup.size} rooms")

        val areaMaps = mutableListOf<AreaMap>()

        for (areaFile in areaFilesToMap) {
            try {
                val generator = SingleAreaMapGenerator(
                        areaFile, roomVnumLookup, renderer, outputDirPath, verbose,
                        progressiveRender, mapDebug,
                )
                val areaMap = generator.generate()
                areaMaps.add(areaMap)
            }
            catch (e: MapGenerationError) {
                info("Failed to generate map for area file $areaFile: ${e.message}")
                if (dieOnError) throw e
            }
        }

        generateIndex(areaMaps)
    }

    private fun generateIndex(areaMaps: List<AreaMap>) {
        info("Generating index...")
        renderer.renderIndex(outputDirPath, areaMaps)
    }

    private fun info(message: String) {
        println(message)
    }

    private fun debug(message: String) {
        if (verbose) println(message)
    }
}
