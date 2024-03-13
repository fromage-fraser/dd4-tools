package dd4.core.util

import java.util.*

fun String.upperCaseFirst(): String =
        this.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault())
            else it.toString()
        }
