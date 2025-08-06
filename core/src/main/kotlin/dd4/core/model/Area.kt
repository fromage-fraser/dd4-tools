package dd4.core.model

import com.fasterxml.jackson.annotation.JsonIgnore

data class Area(
    val id: String,
    val name: String,
    val author: String,
    val lowLevel: Int,
    val highLevel: Int,
    val enforcedLowLevel: Int,
    val enforcedHighLevel: Int,
) {
    companion object {
        const val LEVEL_ALL = -2
        const val LEVEL_NONE = -3
        const val LEVEL_CLAN = -4
    }

    @JsonIgnore
    fun isClanHeadquarters() = lowLevel == LEVEL_CLAN
}
