package dd4.mapmaker

import dd4.core.model.Direction
import dd4.core.model.Room
import dd4.core.model.SourceFile

abstract class Hint(
        val areaId: String
)


class ExitHint(
        areaId: String,
        val roomVnum: Int,
        val exitDirection: Direction,
        val type: Type
) : Hint(areaId) {

    enum class Type {
        FORCE_JUMP
    }
}


interface Hinter {

    fun hintsForExit(sourceFile: SourceFile, room: Room, exitDirection: Direction): List<ExitHint>
}


class StaticHinter : Hinter {

    companion object {
        val hints = listOf<Hint>(
                ExitHint(
                        "midgaard",
                        3049,
                        Direction.SOUTH,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "midgaard",
                        3203,
                        Direction.NORTH,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "midgaard",
                        3051,
                        Direction.SOUTH,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "midgaard",
                        3100,
                        Direction.NORTH,
                        ExitHint.Type.FORCE_JUMP
                ),

                ExitHint(
                        "tcwn",
                        31018,
                        Direction.EAST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "tcwn",
                        31016,
                        Direction.WEST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "tcwn",
                        31017,
                        Direction.EAST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "tcwn",
                        31019,
                        Direction.WEST,
                        ExitHint.Type.FORCE_JUMP
                ),

                ExitHint(
                        "krondor",
                        30000,
                        Direction.EAST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "krondor",
                        30056,
                        Direction.WEST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "krondor",
                        30094,
                        Direction.EAST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "krondor",
                        30093,
                        Direction.WEST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "krondor",
                        30092,
                        Direction.NORTH,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "krondor",
                        30050,
                        Direction.SOUTH,
                        ExitHint.Type.FORCE_JUMP
                ),

                ExitHint(
                        "kerofk",
                        30279,
                        Direction.EAST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "kerofk",
                        30278,
                        Direction.WEST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "kerofk",
                        30340,
                        Direction.EAST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "kerofk",
                        30276,
                        Direction.WEST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "kerofk",
                        30335,
                        Direction.EAST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "kerofk",
                        30277,
                        Direction.WEST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "kerofk",
                        30250,
                        Direction.NORTH,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "kerofk",
                        30250,
                        Direction.SOUTH,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "kerofk",
                        30250,
                        Direction.EAST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "kerofk",
                        30250,
                        Direction.WEST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "kerofk",
                        30268,
                        Direction.NORTH,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "kerofk",
                        30268,
                        Direction.SOUTH,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "kerofk",
                        30268,
                        Direction.EAST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "kerofk",
                        30268,
                        Direction.WEST,
                        ExitHint.Type.FORCE_JUMP
                ),

                ExitHint(
                        "mahntor",
                        2339,
                        Direction.WEST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "mahntor",
                        2336,
                        Direction.EAST,
                        ExitHint.Type.FORCE_JUMP
                ),

                ExitHint(
                        "vampcat4",
                        25786,
                        Direction.SOUTH,
                        ExitHint.Type.FORCE_JUMP
                ),

                ExitHint(
                        "witch",
                        10130,
                        Direction.SOUTH,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "witch",
                        10114,
                        Direction.NORTH,
                        ExitHint.Type.FORCE_JUMP
                ),

                ExitHint(
                        "quake",
                        12157,
                        Direction.SOUTH,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "quake",
                        12010,
                        Direction.NORTH,
                        ExitHint.Type.FORCE_JUMP
                ),

                ExitHint(
                        "tentusks",
                        25467,
                        Direction.NORTH,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "tentusks",
                        25450,
                        Direction.WEST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "tentusks",
                        25435,
                        Direction.EAST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "tentusks",
                        25438,
                        Direction.WEST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "tentusks",
                        25532,
                        Direction.EAST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "tentusks",
                        25436,
                        Direction.WEST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "tentusks",
                        25434,
                        Direction.EAST,
                        ExitHint.Type.FORCE_JUMP
                ),
                ExitHint(
                        "tentusks",
                        25500,
                        Direction.WEST,
                        ExitHint.Type.FORCE_JUMP
                )
        )
    }

    override fun hintsForExit(sourceFile: SourceFile, room: Room, exitDirection: Direction): List<ExitHint> =
            hints.filterIsInstance<ExitHint>()
                    .filter { it.areaId == sourceFile.id }
                    .filter { it.roomVnum == room.vnum }
                    .filter { it.exitDirection == exitDirection }
}
