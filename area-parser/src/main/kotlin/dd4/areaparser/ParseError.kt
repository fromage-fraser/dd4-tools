package dd4.areaparser

class ParseError(message: String) : RuntimeException(message) {

    companion object {
        fun readerContext(reader: AreaFileReader) =
                reader.readUpTo(200)
                        .replace("\r", "")
                        .replace("\n", "\\n")
    }

    constructor(message: String, reader: AreaFileReader) : this("$message (context: ${readerContext(reader)})")
}
