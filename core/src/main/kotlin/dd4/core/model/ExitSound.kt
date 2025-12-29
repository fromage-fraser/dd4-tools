package dd4.core.model

data class ExitSound(
    val roomVnum: Int,
    val direction: Direction,
    val action: String,
    val file: String,
    val volume: Int,
)
