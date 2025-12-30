package dd4.areaparser

import dd4.core.file.Markup
import dd4.core.model.Area
import dd4.core.model.AreaSpecial
import dd4.core.model.Direction
import dd4.core.model.Exit
import dd4.core.model.ExitSound
import dd4.core.model.Game
import dd4.core.model.Help
import dd4.core.model.Item
import dd4.core.model.ItemSet
import dd4.core.model.MobProg
import dd4.core.model.MobProgAssignment
import dd4.core.model.MobProgFile
import dd4.core.model.MobSpec
import dd4.core.model.Mobile
import dd4.core.model.Recall
import dd4.core.model.Reset
import dd4.core.model.Room
import dd4.core.model.RoomAmbientSound
import dd4.core.model.Section
import dd4.core.model.Shop
import dd4.core.model.SourceFile
import dd4.core.model.SpecialFunction
import dd4.core.model.Vnum
import dd4.core.util.upperCaseFirst
import java.io.File
import java.nio.file.Paths

class AreaParser(
    private val inputDirName: String,
    private val areaListFileName: String,
    private val verbose: Boolean = false,
) {
    private companion object {
        const val MOB_PROG_FILE_DIR = "MOBProgs"
        val OBJECT_MATERIAL_ELEMENT_SEPARATOR_PATTERN =
            Regex("""\s*${Regex.escape(Markup.OBJECT_MATERIAL_ELEMENT_SEPARATOR)}+\s*""")
    }

    fun parse(): List<SourceFile> {
        val areaFileNames = parseAreaList()
        val areaFiles = areaFileNames.map { parseAreaFile(it) }
        info(
            "Read ${areaFiles.size} area files, " +
                areaFiles.sumOf { it.mobiles.size } + " mobiles, " +
                areaFiles.sumOf { it.objects.size } + " objects, " +
                areaFiles.sumOf { it.rooms.size } + " rooms, " +
                areaFiles.sumOf { it.resets.size } + " resets, " +
                areaFiles.sumOf { it.helps.size } + " helps",
        )
        return areaFiles
    }

    private fun info(message: String) {
        println(message)
    }

    private fun debug(message: String) {
        if (verbose) println(message)
    }

    private fun parseAreaList(): List<String> {
        val areaListPath = Paths.get(inputDirName, areaListFileName)
        info("Using area list $areaListPath")

        return File(areaListPath.toString()).readLines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .filter { it.endsWith(".are", ignoreCase = true) }
    }

    private fun parseAreaFile(areaFileName: String): SourceFile {
        val areaFilePath = Paths.get(inputDirName, areaFileName)
        val id = areaFileName.lowercase().removeSuffix(".are").replace(Regex("\\W+"), "_")
        info("Parsing area file $areaFilePath ($id)")

        val sourceFile = SourceFile(
            id = id,
            fileName = areaFileName,
            filePath = areaFilePath.toString(),
        )

        AreaFileReader(areaFilePath).use { reader ->
            var loop = true

            while (loop) {
                val section = reader.readSection()
                debug("$id: Found section '$section'")

                when (section) {
                    Section.END_OF_FILE -> loop = false

                    Section.AREA -> {
                        // We expect only one area per source file (note, some source files have no area).
                        // However, it is probably "fine" to have multiple areas and to just simply remember the
                        // last found area as the current area. In practice, the server does not define areas this way.
                        if (sourceFile.area != null) {
                            throw ParseError(
                                "$id: AREA section redefined in file $areaFileName",
                                reader,
                            )
                        }
                        sourceFile.area = parseAreaSection(reader, id)
                    }

                    Section.AREA_SPECIAL -> {
                        // See note in AREA handling regarding redefinition.
                        if (sourceFile.areaSpecial != null) {
                            throw ParseError(
                                "$id: AREA_SPECIAL section redefined in file $areaFileName",
                                reader,
                            )
                        }
                        sourceFile.areaSpecial = parseAreaSpecialSection(reader)
                    }

                    Section.RECALL -> {
                        // See note in AREA handling regarding redefinition.
                        if (sourceFile.recall != null) {
                            throw ParseError(
                                "$id: RECALL section redefined in file $areaFileName",
                                reader,
                            )
                        }
                        sourceFile.recall = parseRecallSection(reader)
                    }

                    Section.MOBILES -> sourceFile.addMobiles(
                        parseMobilesSection(
                            sourceFile,
                            reader,
                        ),
                    )

                    Section.MOBPROGS -> {
                        val mobProgAssignments = parseMobProgsSection(reader)
                        sourceFile.addMobProgAssignments(mobProgAssignments)
                        sourceFile.addMobProgFiles(
                            mobProgAssignments.map {
                                loadMobProgFile(it.fileName)
                            },
                        )
                    }

                    Section.OBJECTS -> sourceFile.addObjects(
                        parseObjectsSection(
                            sourceFile,
                            reader,
                        ),
                    )

                    Section.OBJECT_SETS -> sourceFile.addObjectSets(
                        parseObjectSetsSection(
                            sourceFile,
                            reader,
                        ),
                    )

                    Section.ROOMS -> sourceFile.addRooms(parseRoomsSection(sourceFile, reader))

                    Section.ROOM_AMBIENT_SOUNDS -> sourceFile.addRoomAmbientSounds(
                        parseRoomAmbientSoundsSection(sourceFile, reader),
                    )

                    Section.RESETS -> sourceFile.addResets(parseResetsSection(sourceFile, reader))

                    Section.SHOPS -> sourceFile.addShops(parseShopsSection(sourceFile, reader))

                    Section.SPECIAL_FUNCTIONS ->
                        sourceFile.addSpecialFunctions(
                            parseSpecialFunctionsSection(
                                sourceFile,
                                reader,
                            ),
                        )

                    Section.HELPS -> sourceFile.addHelps(parseHelpsSection(reader))

                    Section.GAMES -> sourceFile.addGames(parseGamesSection(sourceFile, reader))

                    Section.EXIT_SOUNDS -> sourceFile.addExitSounds(
                        parseExitSoundsSection(sourceFile, reader),
                    )
                }
            }
        }

        return sourceFile
    }

    private fun parseAreaSection(reader: AreaFileReader, id: String): Area = Area(
        id = id,
        author = reader.readString(),
        name = reader.readString(),
        lowLevel = reader.readNumber(),
        highLevel = reader.readNumber(),
        enforcedLowLevel = reader.readNumber(),
        enforcedHighLevel = reader.readNumber(),
    )

    private fun parseAreaSpecialSection(reader: AreaFileReader): AreaSpecial {
        val flags = mutableSetOf<AreaSpecial.AreaFlag>()
        var experienceModifier: Int? = null
        var resetMessage: String? = null
        var ambientFile: String? = null
        var ambientVolume = 0
        var loop = true

        while (loop) {
            when (val word = reader.readWord()) {
                Markup.AREA_SPECIAL_END_OF_SECTION -> loop = false

                Markup.AREA_SPECIAL_EXPERIENCE_MODIFIER_TAG ->
                    experienceModifier = reader.readNumber()

                Markup.AREA_SPECIAL_AMBIENT_SOUND_FILE_TAG -> ambientFile = reader.readWord()

                Markup.AREA_SPECIAL_AMBIENT_SOUND_VOLUME_TAG -> ambientVolume = reader.readNumber()

                Markup.AREA_SPECIAL_RESET_MESSAGE_TAG -> resetMessage = reader.readString()

                else -> flags.add(AreaSpecial.AreaFlag.fromTag(word))
            }
        }

        return AreaSpecial(
            flags = flags,
            experienceModifier = experienceModifier,
            resetMessage = resetMessage,
            ambientSoundFile = ambientFile,
            ambientSoundVolume = ambientVolume,
        )
    }

    private fun parseRecallSection(reader: AreaFileReader): Recall = Recall(reader.readNumber())

    private fun parseMobilesSection(sourceFile: SourceFile, reader: AreaFileReader): List<Mobile> {
        val mobiles = mutableListOf<Mobile>()
        var vnum = reader.readVnum()

        while (vnum != Vnum.NULL_VNUM) {
            val name = reader.readString()
            val shortDescription = reader.readString()
            val longDescription = reader.readString()
            val fullDescription = reader.readString()
            val actFlags = Mobile.ActFlag.toSet(reader.readBits())
            val effectFlags = Mobile.EffectFlag.toSet(reader.readBits())
            val alignment = reader.readNumber()
            reader.readLetter() // 'S' (for "simple"?)
            val level = reader.readNumber()

            // Unused
            reader.readNumber()
            reader.readNumber()

            // '0d0+0'
            reader.readNumber()
            reader.readLetter()
            reader.readNumber()
            reader.readLetter()
            reader.readNumber()

            // '0d0+0'
            reader.readNumber()
            reader.readLetter()
            reader.readNumber()
            reader.readLetter()
            reader.readNumber()

            val bodyFormFlags = Mobile.BodyFormFlag.toSet(reader.readBits())

            // Unused
            reader.readNumber()
            reader.readNumber()
            reader.readNumber()

            val sex = Mobile.Sex.fromId(reader.readNumber())

            val mobProgs = mutableListOf<MobProg>()
            val taughtSkills = mutableListOf<Mobile.TaughtSkill>()
            var loop = true
            var mobSpec: MobSpec? = null

            while (loop) {
                reader.readWhitespace()
                when (val nextChar = reader.peekChar()?.uppercaseChar()) {
                    Markup.MOBILE_MOB_PROG_START_DELIMITER -> {
                        reader.readChar()
                        mobProgs.add(
                            MobProg(
                                type = reader.readWord(),
                                args = reader.readString(),
                                commands = reader.readString(),
                            ),
                        )
                    }

                    Markup.MOBILE_MOB_PROG_END_DELIMITER -> reader.readToEol()

                    Markup.MOBILE_TAUGHT_SKILLS_DELIMITER -> {
                        reader.readChar()
                        taughtSkills.add(
                            Mobile.TaughtSkill(
                                level = reader.readNumber(),
                                skill = reader.readWord(),
                            ),
                        )
                    }

                    Markup.MOBILE_SPEC_DELIMITER -> {
                        reader.readChar()
                        mobSpec = MobSpec(
                            name = reader.readString(),
                            rank = reader.readString(),
                        )
                    }

                    Markup.SECTION_DELIMITER -> loop = false

                    null -> throw ParseError("End of file")

                    else -> throw ParseError("Unexpected char '$nextChar'", reader)
                }
            }

            val mobile = Mobile(
                vnum = vnum,
                name = name,
                shortDescription = shortDescription,
                longDescription = longDescription,
                fullDescription = fullDescription,
                alignment = alignment,
                level = level,
                sex = sex,
                actFlags = actFlags,
                effectFlags = effectFlags,
                bodyFormFlags = bodyFormFlags,
                mobProgs = mobProgs,
                taughtSkills = taughtSkills,
                mobSpec = mobSpec,
            )

            debug("${sourceFile.id}: $mobile")
            mobiles.add(mobile)
            vnum = reader.readVnum()
        }

        return mobiles
    }

    private fun parseMobProgsSection(reader: AreaFileReader): List<MobProgAssignment> {
        val mobProgAssignments = mutableListOf<MobProgAssignment>()
        var loop = true

        while (loop) {
            reader.readWhitespace()
            when (val nextChar = reader.readChar()?.uppercaseChar()) {
                Markup.MOB_PROG_START_DELIMITER -> {
                    mobProgAssignments.add(
                        MobProgAssignment(
                            type = nextChar,
                            mobileVnum = reader.readNumber(),
                            fileName = reader.readWord(),
                            comment = reader.readToEol(),
                        ),
                    )
                }

                Markup.MOB_PROG_END_OF_SECTION_DELIMITER -> loop = false

                Markup.MOB_PROG_COMMENT_DELIMITER -> reader.readToEol()

                null -> throw ParseError("End of file")

                else -> throw ParseError("Unexpected char '$nextChar'", reader)
            }
        }

        return mobProgAssignments
    }

    private fun parseObjectsSection(sourceFile: SourceFile, reader: AreaFileReader): List<Item> {
        val objects = mutableListOf<Item>()
        var vnum = reader.readVnum()

        while (vnum != 0) {
            val name = reader.readString()
            val shortDescription = reader.readString()
            val fullDescription = reader.readString().upperCaseFirst()
            reader.readString() // Unused: Action description

            val type = Item.Type.fromId(reader.readNumber())
            val extraFlags = Item.ExtraFlag.toSet(reader.readBits())

            val trap =
                if (extraFlags.contains(Item.ExtraFlag.TRAP)) {
                    Item.Trap(
                        damage = reader.readNumber(),
                        effect = reader.readNumber(),
                        charge = reader.readNumber(),
                    )
                } else {
                    null
                }

            val ego =
                if (extraFlags.contains(Item.ExtraFlag.EGO)) {
                    Item.Ego(
                        flags = reader.readNumber(),
                    )
                } else {
                    null
                }

            val wearFlags = Item.WearFlag.toSet(reader.readBits())
            val value1 = reader.readString()
            val value2 = reader.readString()
            val value3 = reader.readString()
            val value4 = reader.readString()
            val weight = reader.readNumber()
            val cost = reader.readNumber()
            val level = reader.readNumber()

            val extraDescriptions = mutableListOf<Item.ExtraDescription>()
            val effects = mutableListOf<Item.Effect>()
            val materials = mutableListOf<String>()
            var maxInstances: Int? = null
            var loop = true

            while (loop) {
                reader.readWhitespace()
                when (reader.peekChar()) {
                    Markup.OBJECT_EXTRA_DESCRIPTION_DELIMITER -> {
                        reader.readChar()
                        extraDescriptions.add(
                            Item.ExtraDescription(
                                keywords = reader.readString(),
                                description = reader.readString(),
                            ),
                        )
                    }

                    Markup.OBJECT_EFFECT_DELIMITER -> {
                        reader.readChar()
                        effects.add(
                            Item.Effect(
                                attribute = Item.EffectAttribute.fromId(reader.readNumber()),
                                modifier = reader.readNumber(),
                            ),
                        )
                    }

                    Markup.OBJECT_MATERIAL_DELIMITER -> {
                        reader.readChar()
                        materials.addAll(
                            reader.readString()
                                .split(OBJECT_MATERIAL_ELEMENT_SEPARATOR_PATTERN)
                                .filter { it.isNotBlank() },
                        )
                    }

                    Markup.OBJECT_MAX_INSTANCES_DELIMITER -> {
                        reader.readChar()
                        maxInstances = reader.readNumber()
                    }

                    null -> throw ParseError("End of file")

                    else -> loop = false
                }
            }

            val values = Item.Values(value1, value2, value3, value4)
            val typeProperties = typePropertiesFor(type, values)

            val item = Item(
                vnum = vnum,
                name = name,
                shortDescription = shortDescription,
                fullDescription = fullDescription,
                extraDescriptions = extraDescriptions,
                type = type,
                values = values,
                weight = weight,
                cost = cost,
                level = level,
                effects = effects,
                extraFlags = extraFlags,
                wearFlags = wearFlags,
                trap = trap,
                ego = ego,
                typeProperties = typeProperties,
                maxInstances = maxInstances,
                materials = materials,
            )

            debug("${sourceFile.id}: $item")
            objects.add(item)
            vnum = reader.readVnum()
        }

        return objects
    }

    private fun parseObjectSetsSection(
        sourceFile: SourceFile,
        reader: AreaFileReader,
    ): List<ItemSet> {
        val objectSets = mutableListOf<ItemSet>()
        var vnum = reader.readVnum()

        // TODO: Actually store ItemSet data
        while (vnum != 0) {
            reader.readString() // name
            reader.readString() // description

            reader.readNumber() // object (vnums?)
            reader.readNumber()
            reader.readNumber()
            reader.readNumber()
            reader.readNumber()

            reader.readNumber() // bonus number (?)
            reader.readNumber()
            reader.readNumber()
            reader.readNumber()
            reader.readNumber()

            var loop = true

            while (loop) {
                reader.readWhitespace()
                when (reader.peekChar()) {
                    Markup.OBJECT_SET_EFFECT_DELIMITER -> {
                        reader.readChar()
                        reader.readNumber()
                        reader.readNumber()
                    }

                    null -> throw ParseError("End of file")

                    else -> loop = false
                }
            }

            val itemSet = ItemSet(
                vnum = vnum,
            )

            debug("${sourceFile.id}: $itemSet")
            objectSets.add(itemSet)
            vnum = reader.readVnum()
        }

        return objectSets
    }

    private fun typePropertiesFor(type: Item.Type, values: Item.Values): Item.TypeProperties {
        val properties = Item.TypeProperties()

        when (type) {
            Item.Type.WEAPON -> {
                try {
                    properties.weaponAttackType =
                        Item.WeaponAttackType.fromId(values.value3.toInt())
                } catch (_: Exception) {
                    // This is probably bollocks
                    properties.weaponAttackType = Item.WeaponAttackType.HIT
                }
            }

            Item.Type.POTION,
            Item.Type.SCROLL,
            Item.Type.PAINT,
            Item.Type.PILL,
            -> {
                properties.spellLevel = values.value0.toInt()
                properties.spells = validSpells(values.value1, values.value2, values.value3)
            }

            Item.Type.STAFF,
            Item.Type.WAND,
            -> {
                properties.spellLevel = values.value0.toInt()
                properties.maxCharges = values.value1.toInt()
                properties.currentCharges = values.value2.toInt()
                properties.spells = validSpells(values.value3)
            }

            Item.Type.CONTAINER -> {
                properties.containerCapacity = values.value0.toInt()
            }

            else -> {}
        }

        return properties
    }

    private fun validSpells(vararg spells: String) = spells.filter {
        it.isNotBlank() &&
            it != "0" &&
            it != "-1"
    }

    private fun parseRoomsSection(sourceFile: SourceFile, reader: AreaFileReader): List<Room> {
        val rooms = mutableListOf<Room>()
        var vnum = reader.readVnum()

        while (vnum != 0) {
            val name = reader.readString()
            val description = reader.readString()
            reader.readNumber() // Unused: Area number
            val flags = Room.Flag.toSet(reader.readBits())
            val sectorType =
                Room.SectorType.findById(reader.readNumber()) ?: Room.SectorType.UNKNOWN

            val exits = mutableMapOf<Direction, Exit>()
            val extraDescriptions = mutableListOf<Room.ExtraDescription>()
            var loop = true

            while (loop) {
                reader.readWhitespace()

                when (val nextChar = reader.readChar()?.uppercaseChar()) {
                    Markup.ROOM_DOOR_DELIMITER -> {
                        val direction = Direction.fromId(reader.readNumber())
                        val exit = Exit(
                            direction = direction,
                            description = reader.readString(),
                            keywords = reader.readString(),
                            flags = Exit.Flag.fromLocks(reader.readNumber()),
                            keyVnum = reader.readNumber(),
                            destinationVnum = reader.readNumber(),
                        )

                        if (exits.containsKey(direction)) {
                            info(
                                "${sourceFile.id}: Warning: exit direction $direction redefined in room $vnum",
                            )
                        }
                        exits[direction] = exit
                    }

                    Markup.ROOM_EXTRA_DESCRIPTION_DELIMITER -> {
                        extraDescriptions.add(
                            Room.ExtraDescription(
                                keywords = reader.readString(),
                                description = reader.readString(),
                            ),
                        )
                    }

                    Markup.ROOM_END_OF_SECTION_DELIMITER -> loop = false

                    null -> throw ParseError("End of file")

                    else -> throw ParseError("Unexpected char '$nextChar'", reader)
                }
            }

            val room = Room(
                vnum = vnum,
                name = name,
                description = description,
                flags = flags,
                sectorType = sectorType,
                exits = exits,
                extraDescriptions = extraDescriptions,
            )

            debug("${sourceFile.id}: $room")
            rooms.add(room)
            vnum = reader.readVnum()
        }

        return rooms
    }

    private fun parseRoomAmbientSoundsSection(
        sourceFile: SourceFile,
        reader: AreaFileReader,
    ): List<RoomAmbientSound> {
        val roomAmbientSounds = mutableListOf<RoomAmbientSound>()

        while (true) {
            val tryVnum = reader.readWord()
            if (tryVnum == Markup.ROOM_AMBIENT_SOUNDS_END_OF_SECTION_DELIMITER) break
            val vnum = tryVnum.toInt()
            val file = reader.readWord()
            val volume = reader.readNumber()

            val roomAmbientSound = RoomAmbientSound(
                roomVnum = vnum,
                file = file,
                volume = volume,
            )

            debug("${sourceFile.id}: $roomAmbientSound")
            roomAmbientSounds.add(roomAmbientSound)
        }

        return roomAmbientSounds
    }

    private fun parseResetsSection(sourceFile: SourceFile, reader: AreaFileReader): List<Reset> {
        val resets = mutableListOf<Reset>()
        var loop = true

        while (loop) {
            reader.readWhitespace()

            when (val nextChar = reader.readChar()?.uppercaseChar()) {
                Markup.RESET_END_OF_SECTION_DELIMITER -> loop = false

                Markup.RESET_COMMENT_DELIMITER -> reader.readToEol()

                null -> throw ParseError("End of file")

                else -> {
                    val type = Reset.Type.fromId(nextChar)
                    val reset = Reset(
                        type = type,
                        arg0 = reader.readNumber(),
                        arg1 = reader.readNumber(),
                        arg2 = reader.readNumber(),
                        arg3 = when (type) {
                            Reset.Type.OBJECT_TO_MOBILE_INVENTORY -> 0
                            Reset.Type.RANDOMIZE_EXITS -> 0
                            Reset.Type.UNKNOWN_F -> 0
                            else -> reader.readNumber()
                        },
                        comment = reader.readToEol(),
                    )

                    debug("${sourceFile.id}: $reset")
                    resets.add(reset)
                }
            }
        }

        return resets
    }

    private fun parseShopsSection(sourceFile: SourceFile, reader: AreaFileReader): List<Shop> {
        val shops = mutableListOf<Shop>()
        var keeperVnum = reader.readNumber()

        while (keeperVnum != Vnum.NULL_VNUM) {
            val shop = Shop(
                keeperVnum = keeperVnum,
                buyTypes = (1..Shop.SHOP_BUY_TYPE_SLOTS)
                    .map { reader.readNumber() }
                    .filter { it > 0 }
                    .mapNotNull { Item.Type.findById(it) }
                    .toSet(),
                buyProfit = reader.readNumber(),
                sellProfit = reader.readNumber(),
                openingHour = reader.readNumber(),
                closingHour = reader.readNumber(),
                comment = reader.readToEol(),
            )

            debug("${sourceFile.id}: $shop")
            shops.add(shop)
            keeperVnum = reader.readNumber()
        }

        return shops
    }

    private fun parseSpecialFunctionsSection(
        sourceFile: SourceFile,
        reader: AreaFileReader,
    ): List<SpecialFunction> {
        val specialFunctions = mutableListOf<SpecialFunction>()
        var loop = true

        while (loop) {
            reader.readWhitespace()

            when (val nextChar = reader.readChar()?.uppercaseChar()) {
                Markup.SPECIAL_FUNCTION_END_OF_SECTION_DELIMITER -> loop = false

                Markup.SPECIAL_FUNCTION_COMMENT_DELIMITER -> reader.readToEol()

                Markup.SPECIAL_FUNCTION_MOBILE_DELIMITER -> {
                    val specialFunction = SpecialFunction(
                        mobileVnum = reader.readNumber(),
                        function = reader.readWord(),
                        comment = reader.readToEol(),
                    )

                    debug("${sourceFile.id}: $specialFunction")
                    specialFunctions.add(specialFunction)
                }

                null -> throw ParseError("End of file")

                else -> throw ParseError("Unexpected char '$nextChar'", reader)
            }
        }

        return specialFunctions
    }

    private fun parseHelpsSection(reader: AreaFileReader): List<Help> {
        val helps = mutableListOf<Help>()

        while (reader.hasContent()) {
            val level = reader.readNumber()
            val keywords = reader.readString()
            if (keywords == Markup.HELP_END_OF_SECTION_DELIMITER) break

            helps.add(
                Help(
                    level = level,
                    keywords = keywords,
                    text = reader.readString(),
                ),
            )
        }

        return helps
    }

    private fun parseGamesSection(sourceFile: SourceFile, reader: AreaFileReader): List<Game> {
        val games = mutableListOf<Game>()
        var loop = true

        while (loop) {
            reader.readWhitespace()

            when (val nextChar = reader.readChar()?.uppercaseChar()) {
                Markup.GAME_END_OF_SECTION_DELIMITER -> loop = false

                Markup.GAME_COMMENT_DELIMITER -> reader.readToEol()

                Markup.GAME_MOBILE_DELIMITER -> {
                    // TODO: Populate Game completely
                    val croupierVnum = reader.readNumber()
                    reader.readWord()
                    reader.readNumber()
                    reader.readNumber()
                    reader.readNumber()
                    reader.readToEol() // Comments

                    val game = Game(
                        croupierVnum = croupierVnum,
                    )

                    debug("${sourceFile.id}: $game")
                    games.add(game)
                }

                null -> throw ParseError("End of file")

                else -> throw ParseError("Unexpected char '$nextChar'", reader)
            }
        }

        return games
    }

    private fun parseExitSoundsSection(
        sourceFile: SourceFile,
        reader: AreaFileReader,
    ): List<ExitSound> {
        val exitSounds = mutableListOf<ExitSound>()

        while (true) {
            val tryVnum = reader.readWord()
            if (tryVnum == Markup.EXIT_SOUNDS_END_OF_SECTION_DELIMITER) break
            val vnum = tryVnum.toInt()
            val direction = Direction.fromTag(reader.readWord())
            val action = reader.readWord()
            val file = reader.readWord()
            val volume = reader.readNumber()

            val exitSound = ExitSound(
                roomVnum = vnum,
                direction = direction,
                action = action,
                file = file,
                volume = volume,
            )

            debug("${sourceFile.id}: $exitSound")
            exitSounds.add(exitSound)
        }

        return exitSounds
    }

    private fun loadMobProgFile(fileName: String): MobProgFile {
        debug("Loading mob prog file '$fileName'")
        val mobProgs: MutableList<MobProg> = mutableListOf()
        val filePath = Paths.get(this.inputDirName, MOB_PROG_FILE_DIR, fileName)

        AreaFileReader(filePath).use { reader ->
            while (reader.hasContent()) {
                reader.readWhitespace()

                when (val char = reader.readChar()) {
                    Markup.MOBILE_MOB_PROG_START_DELIMITER -> {
                        mobProgs.add(
                            MobProg(
                                type = reader.readWord(),
                                args = reader.readString(),
                                commands = reader.readString(),
                            ),
                        )
                    }

                    Markup.MOBILE_MOB_PROG_END_DELIMITER -> break

                    else -> throw ParseError(
                        "Unexpected character '$char' when reading mob prog file '$fileName",
                    )
                }
            }
        }

        return MobProgFile(
            fileName = fileName,
            mobProgs = mobProgs,
        )
    }
}
