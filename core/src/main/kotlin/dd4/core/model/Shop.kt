package dd4.core.model

data class Shop(
        val keeperVnum: Int,
        val buyTypes: Set<Item.Type>,
        val buyProfit: Int,
        val sellProfit: Int,
        val openingHour: Int,
        val closingHour: Int,
        val comment: String,
) {
    companion object {
        const val SHOP_BUY_TYPE_SLOTS = 5
    }

    override fun toString(): String {
        return "Shop(#$keeperVnum" +
                " hours=$openingHour-$closingHour" +
                " buyTypes=" + buyTypes.joinToString("/") { it.tag }.ifEmpty { "none" } +
                ")"
    }
}
