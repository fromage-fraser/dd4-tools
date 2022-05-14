package dd4.core.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue

data class Mobile(
        val vnum: Int,
        val name: String,
        val shortDescription: String,
        val longDescription: String,
        val fullDescription: String,
        val alignment: Int,
        val level: Int,
        val sex: Sex,
        val actFlags: Set<ActFlag>,
        val effectFlags: Set<EffectFlag>,
        val bodyFormFlags: Set<BodyFormFlag>,
        val mobProgs: List<MobProg>,
        val taughtSkills: List<TaughtSkill>
) {
    enum class Sex(
            @JsonValue val tag: String,
            val id: Int
    ) {
        NEUTRAL("neutral", 0),
        MALE("male", 1),
        FEMALE("female", 2);

        companion object {
            fun fromId(value: Int) =
                    try {
                        values().first { it.id == value }
                    }
                    catch (e: NoSuchElementException) {
                        throw IllegalArgumentException("Invalid sex ID: $value")
                    }
        }
    }

    enum class ActFlag(
            @JsonValue val tag: String,
            val bit: Int
    ) {
        NPC("npc", 0x1),
        SENTINEL("sentinel", 0x2),
        SCAVENGER("scavenger", 0x4),
        QUEST_MASTER("quest_master", 0x8),
        AGGRESSIVE("aggressive", 0x20),
        STAY_AREA("stay_area", 0x40),
        WIMPY("wimpy", 0x80),
        PET("pet", 0x100),
        NO_QUEST("no_quest", 0x200),
        PRACTICE("practice", 0x400),
        GAMBLE("gamble", 0x800),
        NO_CHARM("no_charm", 0x1000),
        HEALER("healer", 0x2000),
        FAMOUS("famous", 0x4000),
        LOSE_FAME("lose_fame", 0x8000),
        WIZINVIS("wizinvis", 0x10000),
        MOUNTABLE("mountable", 0x20000),
        BANKER("banker", 0x80000),
        IDENTIFIER("identifier", 0x100000),
        DIE_IF_MASTER_GONE("die_if_master_gone", 0x200000),
        CLAN_GUARD("clan_guard", 0x400000),
        NO_SUMMON("no_summon", 0x800000),
        NO_EXPERIENCE("no_experience", 0x1000000);

        companion object {
            fun fromInt(value: Int) = values().filter { value.and(it.bit) != 0 }.toSet()
        }
    }

    enum class EffectFlag(
            @JsonValue val tag: String,
            val bit: Int
    ) {
        BLIND("blind", 0x1),
        INVISIBLE("invisible", 0x2),
        DETECT_EVIL("detect_evil", 0x4),
        DETECT_INVIS("detect_invis", 0x8),
        DETECT_MAGIC("detect_magic", 0x10),
        DETECT_HIDDEN("detect_hidden", 0x20),
        HOLD("hold", 0x40),
        SANCTUARY("sanctuary", 0x80),
        FAERIE_FIRE("faerie_fire", 0x100),
        INFRARED("infrared", 0x200),
        CURSE("curse", 0x400),
        FLAMING("flaming", 0x800),
        POISON("poison", 0x1000),
        PROTECTION("protection", 0x2000),
        MEDITATE("meditate", 0x4000),
        SNEAK("sneak", 0x8000),
        HIDE("hide", 0x10000),
        SLEEP("sleep", 0x20000),
        CHARM("charm", 0x40000),
        FLYING("flying", 0x80000),
        PASS_DOOR("pass_door", 0x100000),
        DETECT_TRAPS("detect_traps", 0x200000),
        BATTLE_AURA("battle_aura", 0x400000),
        DETECT_SNEAK("detect_sneak", 0x800000),
        GLOBE("globe", 0x1000000),
        DETER("deter", 0x2000000),
        SWIM("swim", 0x4000000),
        PRAYER_PLAGUE("prayer_of_plague", 0x8000000),
        NON_CORPOREAL("non_corporeal", 0x10000000),
        DETECT_GOOD("detect_good", 0x40000000);

        companion object {
            fun fromInt(value: Int) = values().filter { value.and(it.bit) != 0 }.toSet()
        }
    }

    enum class BodyFormFlag(
            @JsonValue val tag: String,
            val bit: Int
    ) {
        NO_HEAD("no_head", 0x1),
        NO_EYES("no_eyes", 0x2),
        NO_ARMS("no_arms", 0x4),
        NO_LEGS("no_legs", 0x8),
        NO_HEART("no_heart", 0x10),
        NO_SPEECH("no_speech", 0x20),
        NO_CORPSE("no_corpse", 0x40),
        HUGE("huge", 0x80),
        INORGANIC("inorganic", 0x100);

        companion object {
            fun fromInt(value: Int) = values().filter { value.and(it.bit) != 0 }.toSet()
        }
    }

    data class TaughtSkill(
            val level: Int,
            val skill: String
    )

    @JsonIgnore
    fun isTeacher(): Boolean = taughtSkills.isNotEmpty()

    override fun toString(): String {
        return "Mobile(#$vnum '$shortDescription'" +
                " level=$level" +
                " align=$alignment" +
                " act=" + actFlags.joinToString(",") { it.tag }.ifEmpty { "none" } +
                " effect=" + effectFlags.joinToString(",") { it.tag }.ifEmpty { "none" } +
                ")"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Mobile

        if (vnum != other.vnum) return false

        return true
    }

    override fun hashCode(): Int {
        return vnum
    }
}
