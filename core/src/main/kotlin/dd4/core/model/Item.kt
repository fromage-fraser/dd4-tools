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
) {
    enum class Type(
            @JsonValue val tag: String,
            val id: Int
    ) {
        LIGHT("light", 1),
        SCROLL("scroll", 2),
        WAND("wand", 3),
        STAFF("staff", 4),
        WEAPON("weapon", 5),
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
        SPELLCRAFT("spellcrafting", 41);

        companion object {
            fun fromId(value: Int) =
                    try {
                        values().first { it.id == value }
                    }
                    catch (e: NoSuchElementException) {
                        throw IllegalArgumentException("Invalid object type ID: $value")
                    }

            fun findById(value: Int) = values().find { it.id == value }
        }
    }

    enum class ExtraFlag(
            @JsonValue val tag: String,
            val bit: Int
    ) {
        GLOW("glow", 0x1),
        HUM("hum", 0x2),
        EGO("ego", 0x4),
        ANTI_RANGER("anti_ranger", 0x8),
        EVIL("evil", 0x10),
        INVISIBLE("invisible", 0x20),
        MAGIC("magic", 0x40),
        NO_DROP("no_drop", 0x80),
        BLESSED("blessed", 0x100),
        ANTI_GOOD("anti_good", 0x200),
        ANTI_EVIL("anti_evil", 0x400),
        ANTI_NEUTRAL("anti_neutral", 0x800),
        NO_REMOVE("no_remove", 0x1000),
        INVENTORY("inventory", 0x2000),
        POISONED("poisoned", 0x4000),
        ANTI_MAGE("anti_mage", 0x8000),
        ANTI_CLERIC("anti_cleric", 0x10000),
        ANTI_THIEF("anti_thief", 0x20000),
        ANTI_WARRIOR("anti_warrior", 0x40000),
        ANTI_PSIONIC("anti_psionic", 0x80000),
        VORPAL("vorpal", 0x100000),
        TRAP("trap", 0x200000),
        DONATED("donated", 0x400000),
        BLADE_THIRST("blade_thirst", 0x800000),
        SHARP("sharp", 0x1000000),
        FORGED("forged", 0x2000000),
        BODY_PART("body_part", 0x4000000),
        LANCE("lance", 0x8000000),
        ANTI_BRAWLER("anti_brawler", 0x10000000),
        ANTI_SHAPE_SHIFTER("anti_shape_shifter", 0x20000000),
        BOW("bow", 0x40000000);

        companion object {
            fun fromInt(value: Int) = values().filter { value.and(it.bit) != 0 }.toMutableSet()
        }
    }

    enum class WearFlag(
            @JsonValue val tag: String,
            val bit: Int
    ) {
        TAKE("take", 0x1),
        WEAR_FINGER("wear_finger", 0x2),
        WEAR_NECK("wear_neck", 0x4),
        WEAR_BODY("wear_body", 0x8),
        WEAR_HEAD("wear_head", 0x10),
        WEAR_LEGS("wear_legs", 0x20),
        WEAR_FEET("wear_feet", 0x40),
        WEAR_HANDS("wear_hands", 0x80),
        WEAR_ARMS("wear_arms", 0x100),
        WEAR_SHIELD("wear_shield", 0x200),
        WEAR_ABOUT_BODY("wear_about_body", 0x400),
        WEAR_WAIST("wear_waist", 0x800),
        WEAR_WRIST("wear_wrist", 0x1000),
        WIELD("wield", 0x2000),
        HOLD("hold", 0x4000),
        FLOAT("float", 0x8000),
        WEAR_POUCH("wear_pouch", 0x10000),
        RANGED_WEAPON("ranged_weapon", 0x20000),
        SIZE_ALL("size_all", 0x8000000),
        SIZE_SMALL("size_small", 0x10000000),
        SIZE_MEDIUM("size_medium", 0x20000000),
        SIZE_LARGE("size_large", 0x40000000);

        companion object {
            fun fromInt(value: Int) = values().filter { value.and(it.bit) != 0 }.toSet()
        }
    }

    enum class EffectAttribute(
            @JsonValue val tag: String,
            val id: Int
    ) {
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
        BREATHE_WATER("breathe_water", 40);

        companion object {
            fun fromId(value: Int) =
                    try {
                        values().first { it.id == value }
                    }
                    catch (e: NoSuchElementException) {
                        throw IllegalArgumentException("Invalid effect attribute ID: $value")
                    }
        }
    }

    enum class WeaponAttackType(
            @JsonValue val tag: String,
            val id: Int
    ) {
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
        STING("sting", 16);

        companion object {
            fun fromId(value: Int) =
                    try {
                        values().first { it.id == value }
                    }
                    catch (e: NoSuchElementException) {
                        throw IllegalArgumentException("Invalid effect attribute ID: $value")
                    }
        }
    }

    data class ExtraDescription(
            val keywords: String,
            val description: String
    )

    data class Effect(
            val attribute: EffectAttribute,
            val modifier: Int
    ) {
        override fun toString(): String = attribute.tag + ":" + Format.modifier(modifier)
    }

    //TODO: Map effect
    data class Trap(
            val damage: Int,
            val effect: Int,
            val charge: Int
    )

    //TODO: Map flags
    data class Ego(
            val flags: Int
    )

    data class Values(
            val value0: String,
            val value1: String,
            val value2: String,
            val value3: String
    )

    data class TypeProperties(
            var weaponAttackType: WeaponAttackType? = null,
            var spellLevel: Int? = null,
            var spells: List<String>? = null,
            var currentCharges: Int? = null,
            var maxCharges: Int? = null,
            var containerCapacity: Int? = null,
    )

    override fun toString(): String {
        return "Object(#$vnum '$shortDescription'" +
                " type=$type" +
                " effect=" + effects.joinToString(",") { it.toString() }.ifEmpty { "none" } +
                " extra=" + extraFlags.joinToString(",") { it.tag }.ifEmpty { "none" } +
                " ${values.value0}/${values.value1}/${values.value2}/${values.value3}" +
                ")"
    }
}
