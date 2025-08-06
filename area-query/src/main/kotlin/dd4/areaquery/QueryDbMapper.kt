package dd4.areaquery

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class QueryDbMapper {

    private val yamlFactory = YAMLFactory()
        .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
        .enable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE)
        .enable(YAMLGenerator.Feature.SPLIT_LINES)

    private val yamlObjectMapper = ObjectMapper(yamlFactory)
        .registerModules(KotlinModule())
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)

    private val jsonObjectMapper = ObjectMapper()
        .registerModules(KotlinModule())
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)

    fun writeToFile(queryDb: QueryDb, outputFilePath: String, fileFormat: FileFormat) {
        val mapper = when (fileFormat) {
            FileFormat.YAML -> yamlObjectMapper
            FileFormat.JSON -> jsonObjectMapper
        }
        val queryDbRecord = mapQueryDb(queryDb)
        mapper.writer()
            .withDefaultPrettyPrinter()
            .writeValue(File(outputFilePath), queryDbRecord)
    }

    private fun mapQueryDb(queryDb: QueryDb): QueryDbRecord {
        val infoRecord = InfoRecord(
            creationDate = ZonedDateTime.now(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
        )

        val mobileRecords = queryDb.mobiles()
            .map { queryDbMobile ->
                val mobile = queryDbMobile.mobile

                MobileRecord(
                    vnum = mobile.vnum,
                    desc = cleanDescription(mobile.shortDescription),
                    level = mobile.level,
                    alignment = mobile.alignment,
                    shop = queryDbMobile.shop != null,
                )
            }

        val areaRecords = queryDb.areas()
            .map { queryDbArea ->
                val area = queryDbArea.area

                AreaRecord(
                    id = area.id,
                    name = area.name,
                )
            }

        val roomRecords = queryDb.rooms()
            .map { queryDbRoom ->
                val room = queryDbRoom.room
                val area = queryDbRoom.area

                RoomRecord(
                    vnum = room.vnum,
                    desc = cleanDescription(room.name),
                    area = area.id,
                )
            }

        val itemRecords = queryDb.items()
            .map { queryDbItem ->
                val item = queryDbItem.item
                val effects = item.effects.map { EffectRecord(it.attribute.tag, it.modifier) }

                val resetRecords = queryDbItem.resets
                    .map {
                        ItemResetRecord(
                            type = it.type.id.toString(),
                            levelMin = it.levelMin,
                            levelMax = it.levelMax,
                            inRoom = it.inRoom?.vnum,
                            inContainer = it.inContainer?.vnum,
                            carriedBy = it.carriedBy?.vnum,
                        )
                    }

                val mobProgRecords = queryDbItem.mobProgs
                    .map {
                        ItemMobProgRecord(
                            level = it.level,
                            mobile = it.mobile.vnum,
                            inRoom = it.inRoom?.vnum,
                        )
                    }

                ItemRecord(
                    vnum = item.vnum,
                    desc = cleanDescription(item.shortDescription),
                    keywords = item.name.lowercase()
                        .split(Regex("\\s+"))
                        .filter { it.isNotBlank() },
                    type = item.type.tag,
                    wear = item.wearFlags.map { it.tag }.sorted(),
                    flags = item.extraFlags.map { it.tag }.sorted(),
                    effects = effects,
                    resets = resetRecords,
                    mobProgs = mobProgRecords,
                    weaponAttackType = item.typeProperties.weaponAttackType?.tag,
                    spellLevel = item.typeProperties.spellLevel,
                    spells = item.typeProperties.spells,
                    currentCharges = item.typeProperties.currentCharges,
                    maxCharges = item.typeProperties.maxCharges,
                    containerCapacity = item.typeProperties.containerCapacity,
                    maxInstances = item.maxInstances,
                )
            }

        return QueryDbRecord(
            info = infoRecord,
            areas = areaRecords,
            rooms = roomRecords,
            mobiles = mobileRecords,
            items = itemRecords,
        )
    }

    private fun cleanDescription(text: String): String = text.replace(Regex("\\{.?"), "")
}
