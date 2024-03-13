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
        val taughtSkills: List<TaughtSkill>,
        val mobSpec: MobSpec?,
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
            val bit: ULong
    ) {
        NPC("npc", 0x1u),
        SENTINEL("sentinel", 0x2u),
        SCAVENGER("scavenger", 0x4u),
        QUEST_MASTER("quest_master", 0x8u),
        AGGRESSIVE("aggressive", 0x20u),
        STAY_AREA("stay_area", 0x40u),
        WIMPY("wimpy", 0x80u),
        PET("pet", 0x100u),
        NO_QUEST("no_quest", 0x200u),
        PRACTICE("practice", 0x400u),
        GAMBLE("gamble", 0x800u),
        NO_CHARM("no_charm", 0x1000u),
        HEALER("healer", 0x2000u),
        FAMOUS("famous", 0x4000u),
        LOSE_FAME("lose_fame", 0x8000u),
        WIZINVIS("wizinvis", 0x10000u),
        MOUNTABLE("mountable", 0x20000u),
        BANKER("banker", 0x80000u),
        IDENTIFIER("identifier", 0x100000u),
        DIE_IF_MASTER_GONE("die_if_master_gone", 0x200000u),
        CLAN_GUARD("clan_guard", 0x400000u),
        NO_SUMMON("no_summon", 0x800000u),
        NO_EXPERIENCE("no_experience", 0x1000000u),
        NO_HEAL("no_heal", 0x2000000u),
        NO_FIGHT("no_fight", 0x4000000u),
        OBJECT("object", 0x8000000u),
        INVULNERABLE("invulnerable", 0x10000000u),
        UNKILLABLE("unkillable", 0x8000000000000000u);

        companion object {
            fun toSet(value: ULong) = values().filter { value.and(it.bit) != 0uL }.toSet()
        }
    }

    enum class EffectFlag(
            @JsonValue val tag: String,
            val bit: ULong
    ) {
        BLIND("blind", 0x1u),
        INVISIBLE("invisible", 0x2u),
        DETECT_EVIL("detect_evil", 0x4u),
        DETECT_INVIS("detect_invis", 0x8u),
        DETECT_MAGIC("detect_magic", 0x10u),
        DETECT_HIDDEN("detect_hidden", 0x20u),
        HOLD("hold", 0x40u),
        SANCTUARY("sanctuary", 0x80u),
        FAERIE_FIRE("faerie_fire", 0x100u),
        INFRARED("infrared", 0x200u),
        CURSE("curse", 0x400u),
        FLAMING("flaming", 0x800u),
        POISON("poison", 0x1000u),
        PROTECTION("protection", 0x2000u),
        MEDITATE("meditate", 0x4000u),
        SNEAK("sneak", 0x8000u),
        HIDE("hide", 0x10000u),
        SLEEP("sleep", 0x20000u),
        CHARM("charm", 0x40000u),
        FLYING("flying", 0x80000u),
        PASS_DOOR("pass_door", 0x100000u),
        DETECT_TRAPS("detect_traps", 0x200000u),
        BATTLE_AURA("battle_aura", 0x400000u),
        DETECT_SNEAK("detect_sneak", 0x800000u),
        GLOBE("globe", 0x1000000u),
        DETER("deter", 0x2000000u),
        SWIM("swim", 0x4000000u),
        PRAYER_PLAGUE("prayer_of_plague", 0x8000000u),
        NON_CORPOREAL("non_corporeal", 0x10000000u),
        DETECT_GOOD("detect_good", 0x40000000u),
        SWALLOWED("swallowed", 0x80000000u),
        NO_RECALL("no_recall", 0x100000000u),
        DAMAGE_OVER_TIME("damage_over_time", 0x200000000u),
        PRONE("prone", 0x400000000u),
        DAZED("dazed", 0x800000000u),
        SLOW("slow", 0x8000000000000000u);

        companion object {
            fun toSet(value: ULong) = values().filter { value.and(it.bit) != 0uL }.toSet()
        }
    }

    enum class BodyFormFlag(
            @JsonValue val tag: String,
            val bit: ULong
    ) {
        NO_HEAD("no_head", 0x1u),
        NO_EYES("no_eyes", 0x2u),
        NO_ARMS("no_arms", 0x4u),
        NO_LEGS("no_legs", 0x8u),
        NO_HEART("no_heart", 0x10u),
        NO_SPEECH("no_speech", 0x20u),
        NO_CORPSE("no_corpse", 0x40u),
        HUGE("huge", 0x80u),
        INORGANIC("inorganic", 0x100u),
        HAS_TAIL("has_tail", 0x200u);

        companion object {
            fun toSet(value: ULong) = values().filter { value.and(it.bit) != 0uL }.toSet()
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
