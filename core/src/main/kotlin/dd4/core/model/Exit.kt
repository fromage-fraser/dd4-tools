package dd4.core.model

import com.fasterxml.jackson.annotation.JsonValue

data class Exit(
    val direction: Direction,
    val description: String,
    val keywords: String,
    val flags: Set<Flag>,
    val destinationVnum: Int,
    val keyVnum: Int,
) {
    enum class Flag(@JsonValue val tag: String, val bit: Int) {
        DOOR("door", 0x1),
        CLOSED("closed", 0x2),
        LOCKED("locked", 0x4),
        BASHED("bashed", 0x8),
        BASH_PROOF("bash_proof", 0x10),
        PICK_PROOF("pick_proof", 0x20),
        PASS_PROOF("pass_proof", 0x40),
        WALL("wall", 0x80),
        SECRET("secret", 0x100),
        ;

        companion object {
            fun fromInt(value: Int) = entries.filter { value.and(it.bit) != 0 }.toSet()

            fun fromLocks(value: Int): Set<Flag> = when (value) {
                1 -> setOf(DOOR)
                2 -> setOf(DOOR, PICK_PROOF)
                3 -> setOf(DOOR, BASH_PROOF)
                4 -> setOf(DOOR, PICK_PROOF, BASH_PROOF)
                5 -> setOf(DOOR, PASS_PROOF)
                6 -> setOf(DOOR, PICK_PROOF, PASS_PROOF)
                7 -> setOf(DOOR, BASH_PROOF, PASS_PROOF)
                8 -> setOf(DOOR, PICK_PROOF, BASH_PROOF, PASS_PROOF)
                9 -> setOf(WALL)
                10 -> setOf(DOOR, SECRET)
                11 -> setOf(DOOR, PICK_PROOF, BASH_PROOF, PASS_PROOF, SECRET)
                12 -> setOf(SECRET)
                // Actual MUD is relaxed about unrecognised values
                else -> setOf()
            }
        }
    }

    override fun toString(): String =
        "Exit(direction=$direction, flags=$flags, destinationVnum=$destinationVnum)"

    fun hasDestinationRoom(): Boolean = destinationVnum > 0
}
