package dd4.areaparser

class ParseError(message: String, e: Exception?) : RuntimeException(message, e) {

    companion object {
        fun readerContext(reader: AreaFileReader) = reader.readUpTo(200)
            .replace("\r", "")
            .replace("\n", "\\n")
    }

    constructor(message: String) : this(message, null)

    constructor(message: String, reader: AreaFileReader) : this(message, reader, null)

    constructor(message: String, reader: AreaFileReader, e: Exception?) :
        this("$message (context: ${readerContext(reader)})", e)
}
