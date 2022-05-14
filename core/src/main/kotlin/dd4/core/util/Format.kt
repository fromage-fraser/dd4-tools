package dd4.core.util

object Format {

    /**
     * Format a numeric modifier: ensure '-' and '+' are present. E.g. -4, 0, +7.
     */
    fun modifier(value: Int): String = if (value > 0) "+$value" else value.toString()
}
