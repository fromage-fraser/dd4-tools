package dd4.core.model

import com.fasterxml.jackson.annotation.JsonValue
import dd4.core.util.Format

// Avoid using the class name "Object"
data class Item(
    val vnum: Int,
    val name: String,
    val shortDescription: String,
    val fullDescription: String,
    val extraDescriptions: List<ExtraDescription>,
    val type: Type,
    val values: Values,
    val weight: Int,
    val cost: Int,
    val level: Int,
    val effects: List<Effect>,
    val extraFlags: MutableSet<ExtraFlag>,
    val wearFlags: Set<WearFlag>,
    val trap: Trap?,
    val ego: Ego?,
    val typeProperties: TypeProperties,
    val maxInstances: Int?,
) {
    enum class Type(@JsonValue val tag: String, val id: Int) {
        LIGHT("light", 1),
        SCROLL("scroll", 2),
        WAND("wand", 3),
        STAFF("staff", 4),
        WEAPON("weapon", 5),
        DIGGER("digger", 6),
        HOARD("hoard", 7),
        TREASURE("treasure", 8),
        ARMOUR("armour", 9),
        POTION("potion", 10),
        FURNITURE("furniture", 12),
        TRASH("trash", 13),
        CONTAINER("container", 15),
        DRINK_CONTAINER("drink_container", 17),
        KEY("key", 18),
        FOOD("food", 19),
        MONEY("money", 20),
        BOAT("boat", 22),
        CORPSE_NPC("npc_corpse", 23),
        CORPSE_PC("pc_corpse", 24),
        FOUNTAIN("fountain", 25),
        PILL("pill", 26),
        CLIMBING_EQUIPMENT("climbing_equipment", 27),
        PAINT("paint", 28),
        MOB("mob", 29),
        ANVIL("anvil", 30),
        AUCTION_TICKET("auction_ticket", 31),
        CLAN_OBJECT("clan_object", 32),
        PORTAL("portal", 33),
        POISON_POWDER("poison_powder", 34),
        LOCK_PICK("lock_pick", 35),
        INSTRUMENT("instrument", 36),
        ARMOURERS_HAMMER("armourers_hammer", 37),
        MITHRIL("mithril", 38),
        WHETSTONE("whetstone", 39),
        CRAFT("crafting", 40),
        SPELLCRAFT("spellcrafting", 41),
        TURRET_MODULE("turret_module", 42),
        FORGE("forge", 43),
        ARRESTOR_UNIT("arrestor_unit", 44),
        DRIVER_UNIT("driver_unit", 45),
        REFLECTOR_UNIT("reflector_unit", 46),
        SHIELD_UNIT("shield_unit", 47),
        DEFENSIVE_TURRET_MODULE("defensive_turret_module", 48),
        TURRET("turret", 49),
        COMBAT_PULSE("combat_pulse", 50),
        DEFENSIVE_PULSE("defensive_pulse", 51),
        PIPE("pipe", 52),
        PIPE_CLEANER("pipe_cleaner", 53),
        SMOKEABLE("smokeable", 54),
        REMAINS("remains", 55),
        ;

        companion object {
            fun fromId(value: Int) = try {
                entries.first { it.id == value }
            } catch (_: NoSuchElementException) {
                throw IllegalArgumentException("Invalid object type ID: $value")
            }

            fun findById(value: Int) = entries.find { it.id == value }
        }
    }

    enum class ExtraFlag(@JsonValue val tag: String, val bit: ULong) {
        GLOW("glow", 0x1u),
        HUM("hum", 0x2u),
        EGO("ego", 0x4u),
        ANTI_RANGER("anti_ranger", 0x8u),
        EVIL("evil", 0x10u),
        INVISIBLE("invisible", 0x20u),
        MAGIC("magic", 0x40u),
        NO_DROP("no_drop", 0x80u),
        BLESSED("blessed", 0x100u),
        ANTI_GOOD("anti_good", 0x200u),
        ANTI_EVIL("anti_evil", 0x400u),
        ANTI_NEUTRAL("anti_neutral", 0x800u),
        NO_REMOVE("no_remove", 0x1000u),
        INVENTORY("inventory", 0x2000u),
        POISONED("poisoned", 0x4000u),
        ANTI_MAGE("anti_mage", 0x8000u),
        ANTI_CLERIC("anti_cleric", 0x10000u),
        ANTI_THIEF("anti_thief", 0x20000u),
        ANTI_WARRIOR("anti_warrior", 0x40000u),
        ANTI_PSIONIC("anti_psionic", 0x80000u),
        VORPAL("vorpal", 0x100000u),
        TRAP("trap", 0x200000u),
        DONATED("donated", 0x400000u),
        BLADE_THIRST("blade_thirst", 0x800000u),
        SHARP("sharp", 0x1000000u),
        FORGED("forged", 0x2000000u),
        BODY_PART("body_part", 0x4000000u),
        LANCE("lance", 0x8000000u),
        ANTI_BRAWLER("anti_brawler", 0x10000000u),
        ANTI_SHAPE_SHIFTER("anti_shape_shifter", 0x20000000u),
        BOW("bow", 0x40000000u),
        ANTI_SMITHY("anti_smithy", 0x400000000u),
        DEPLOYED("deployed", 0x800000000u),
        RUNE("rune", 0x1000000000u),
        DONOT_RANDOMISE("donot_randomise", 0x2000000000u),
        WEAK_RANDOMISE("weak_randomise", 0x8000000000u),
        CURSED("cursed", 0x2000000000000000u),
        ;

        companion object {
            fun toSet(value: ULong) = entries.filter { value.and(it.bit) != 0uL }.toMutableSet()
        }
    }

    enum class WearFlag(@JsonValue val tag: String, val bit: ULong) {
        TAKE("take", 0x1u),
        WEAR_FINGER("wear_finger", 0x2u),
        WEAR_NECK("wear_neck", 0x4u),
        WEAR_BODY("wear_body", 0x8u),
        WEAR_HEAD("wear_head", 0x10u),
        WEAR_LEGS("wear_legs", 0x20u),
        WEAR_FEET("wear_feet", 0x40u),
        WEAR_HANDS("wear_hands", 0x80u),
        WEAR_ARMS("wear_arms", 0x100u),
        WEAR_SHIELD("wear_shield", 0x200u),
        WEAR_ABOUT_BODY("wear_about_body", 0x400u),
        WEAR_WAIST("wear_waist", 0x800u),
        WEAR_WRIST("wear_wrist", 0x1000u),
        WIELD("wield", 0x2000u),
        HOLD("hold", 0x4000u),
        FLOAT("float", 0x8000u),
        WEAR_POUCH("wear_pouch", 0x10000u),
        RANGED_WEAPON("ranged_weapon", 0x20000u),
        SIZE_ALL("size_all", 0x8000000u),
        SIZE_SMALL("size_small", 0x10000000u),
        SIZE_MEDIUM("size_medium", 0x20000000u),
        SIZE_LARGE("size_large", 0x40000000u),
        ;

        companion object {
            fun toSet(value: ULong) = entries.filter { value.and(it.bit) != 0uL }.toSet()
        }
    }

    enum class EffectAttribute(@JsonValue val tag: String, val id: Int) {
        NONE("none", 0),
        STRENGTH("strength", 1),
        DEXTERITY("dexterity", 2),
        INTELLIGENCE("intelligence", 3),
        WISDOM("wisdom", 4),
        CONSTITUTION("constitution", 5),
        SEX("sex", 6),
        CLASS("class", 7),
        LEVEL("level", 8),
        AGE("age", 9),
        HEIGHT("height", 10),
        WEIGHT("weight", 11),
        MANA("mana", 12),
        HIT_POINTS("hit_points", 13),
        MOVEMENT_POINTS("movement_points", 14),
        GOLD("gold", 15),
        EXPERIENCE("experience", 16),
        ARMOUR_CLASS("armour_class", 17),
        HIT_ROLL("hit_roll", 18),
        DAM_ROLL("dam_roll", 19),
        SAVE_VS_PARALYSIS("save_vs_paralysis", 20),
        SAVE_VS_ROD("save_vs_rod", 21),
        SAVE_VS_PETRIFICATION("save_vs_petrification", 22),
        SAVE_VS_BREATH("save_vs_breath", 23),
        SAVE_VS_SPELL("save_vs_spell", 24),
        SANCTUARY("sanctuary", 25),
        SNEAK("sneak", 26),
        FLY("fly", 27),
        INVISIBILITY("invisibility", 28),
        DETECT_INVISIBLE("detect_invisible", 29),
        DETECT_HIDDEN("detect_hidden", 30),
        FLAMING("flaming", 31),
        PROTECTION("protection", 32),
        PASS_DOOR("pass_door", 33),
        GLOBE("globe", 34),
        DRAGON_AURA("dragon_aura", 35),
        RESIST_HEAT("resist_heat", 36),
        RESIST_COLD("resist_cold", 37),
        RESIST_LIGHTNING("resist_lightning", 38),
        RESIST_ACID("resist_acid", 39),
        BREATHE_WATER("breathe_water", 40),
        BALANCE("balance", 41),
        SET_UNCOMMON("uncommon_set", 42),
        SET_RARE("rare_set", 43),
        SET_EPIC("epic_set", 44),
        SET_LEGENDARY("legendary_set", 45),
        STRENGTHEN("strengthen", 46),
        ENGRAVED("engraved", 47),
        SERRATED("serrated", 48),
        INSCRIBED("inscribed", 49),
        CRIT("critical_hit", 50),
        SWIFTNESS("swiftness", 51),
        ;

        companion object {
            fun fromId(value: Int) = try {
                entries.first { it.id == value }
            } catch (_: NoSuchElementException) {
                throw IllegalArgumentException("Invalid effect attribute ID: $value")
            }
        }
    }

    enum class WeaponAttackType(@JsonValue val tag: String, val id: Int) {
        HIT("hit", 0),
        SLICE("slice", 1),
        STAB("stab", 2),
        SLASH("slash", 3),
        WHIP("whip", 4),
        CLAW("claw", 5),
        BLAST("blast", 6),
        POUND("pound", 7),
        CRUSH("crush", 8),
        GREP("grep", 9),
        BITE("bite", 10),
        PIERCE("pierce", 11),
        SUCTION("suction", 12),
        CHOP("chop", 13),
        RAKE("rake", 14),
        SWIPE("swipe", 15),
        STING("sting", 16),
        SCOOP("scoop", 17),
        ;

        companion object {
            fun fromId(value: Int) = try {
                entries.first { it.id == value }
            } catch (_: NoSuchElementException) {
                throw IllegalArgumentException("Invalid effect attribute ID: $value")
            }
        }
    }

    data class ExtraDescription(val keywords: String, val description: String)

    data class Effect(val attribute: EffectAttribute, val modifier: Int) {
        override fun toString(): String = attribute.tag + ":" + Format.modifier(modifier)
    }

    // TODO: Map effect
    data class Trap(val damage: Int, val effect: Int, val charge: Int)

    // TODO: Map flags
    data class Ego(val flags: Int)

    data class Values(
        val value0: String,
        val value1: String,
        val value2: String,
        val value3: String,
    )

    data class TypeProperties(
        var weaponAttackType: WeaponAttackType? = null,
        var spellLevel: Int? = null,
        var spells: List<String>? = null,
        var currentCharges: Int? = null,
        var maxCharges: Int? = null,
        var containerCapacity: Int? = null,
    )

    override fun toString(): String = "Object(#$vnum '$shortDescription'" +
        " type=$type" +
        " effect=" + effects.joinToString(",") { it.toString() }.ifEmpty { "none" } +
        " extra=" + extraFlags.joinToString(",") { it.tag }.ifEmpty { "none" } +
        " ${values.value0}/${values.value1}/${values.value2}/${values.value3}" +
        ")"
}
