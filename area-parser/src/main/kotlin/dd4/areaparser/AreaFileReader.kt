package dd4.areaparser

import dd4.core.file.Markup
import dd4.core.model.Section
import java.io.Closeable
import java.io.FileInputStream
import java.io.PushbackReader
import java.nio.file.Path

class AreaFileReader(areaFilePath: Path) : Closeable {

    private val reader = PushbackReader(FileInputStream(areaFilePath.toFile()).reader(Charsets.UTF_8))

    override fun close() {
        reader.close()
    }

    fun hasContent(): Boolean = reader.ready()

    fun readSection(): Section {
        readWhitespace()
        val char = readChar() ?: throw ParseError("End of file")

        if (char != Markup.SECTION_DELIMITER)
            throw ParseError("Expected section delimiter '${Markup.SECTION_DELIMITER}' but found '$char'", this)

        val tag = readBareWord()

        return try {
            Section.fromTag(tag)
        } catch (e: NoSuchElementException) {
            throw ParseError("Unrecognised section: $tag", this)
        }
    }

    fun readVnum(): Int {
        readWhitespace()
        val char = readChar() ?: throw ParseError("End of file")

        if (char != Markup.VNUM_DELIMITER)
            throw ParseError("Expected VNUM delimiter '${Markup.VNUM_DELIMITER}' but found '$char'", this)

        val tag = readBareWord()

        return try {
            tag.toInt()
        } catch (e: NumberFormatException) {
            throw ParseError("Bad VNUM: $tag", this)
        }
    }

    fun readWhitespace() = readWhile { it.isWhitespace() }

    fun readBareWord() = readWhile { !it.isWhitespace() }

    fun readToEol(): String {
        val text = readWhile { it != '\n' && it != '\r' }
        readWhile { it == '\n' || it == '\r' }
        return text.trim()
    }

    fun readWord(): String {
        readWhitespace()
        val firstChar = readChar() ?: throw ParseError("End of file")
        var quoteChar: Char? = null
        var word = ""

        if (firstChar == '\'' || firstChar == '"') {
            quoteChar = firstChar
        }
        else {
            word += firstChar
        }

        while (hasContent()) {
            val peekChar = peekChar()
            if (peekChar == null) {
                if (quoteChar == null) break
                throw ParseError("End of file")
            }
            if (quoteChar != null && peekChar == quoteChar) {
                readChar()
                break
            }
            if (quoteChar == null && peekChar.isWhitespace()) break
            word += readChar()
        }

        return word
    }

    fun readString(): String {
        readWhitespace()
        val word = readWhile { it != Markup.STRING_DELIMITER }
        readChar() ?: throw ParseError("Unterminated string: '${snippet(word)}", this)
        return word
    }

    fun readNumber(): Int {
        readWhitespace()
        val firstChar = readChar() ?: throw ParseError("End of file")

        if (!(firstChar.isDigit() || firstChar == '+' || firstChar == '-'))
            throw ParseError("Expected start of number, found '$firstChar'", this)

        val unparsed = firstChar + readWhile { it.isDigit() || it == Markup.BIT_DELIMITER }

        try {
            if (unparsed.contains(Markup.BIT_DELIMITER)) {
                return unparsed.split(Markup.BIT_DELIMITER)
                        .filter { it.isNotBlank() }
                        .sumOf { it.toInt() }
            }

            return unparsed.toInt()
        }
        catch (e: Exception) {
            throw ParseError("Unable to read number from value '$unparsed' (too large?)", this, e)
        }
    }

    fun readBits(): ULong {
        readWhitespace()
        val firstChar = readChar() ?: throw ParseError("End of file")

        if (!firstChar.isDigit())
            throw ParseError("Expected start of number, found '$firstChar'", this)

        val unparsed = firstChar + readWhile { it.isDigit() || it == Markup.BIT_DELIMITER }

        if (unparsed.contains(Markup.BIT_DELIMITER)) {
            return unparsed.split(Markup.BIT_DELIMITER)
                    .filter { it.isNotBlank() }
                    .map { it.toULong() }
                    .sum()
        }

        return try {
            unparsed.toULong()
        }
        catch (e: Exception) {
            throw ParseError("Unable to read bits from value '$unparsed' (too large?)", this, e)
        }
    }

    fun readLetter(): Char {
        readWhitespace()
        return readChar() ?: throw ParseError("End of file")
    }

    fun readUpTo(length: Int = 1): String {
        var text = ""
        repeat(length) {
            readChar()?.let { text += it }
        }
        return text
    }

    fun peekChar(): Char? {
        val next = reader.read()
        if (next < 0) return null
        reader.unread(next)
        return next.toChar()
    }

    fun readChar(): Char? {
        val next = reader.read()
        return if (next < 0) null else next.toChar()
    }

    private fun readWhile(predicate: (Char) -> Boolean): String {
        var value = ""
        var char = peekChar()

        while (char != null && predicate.invoke(char)) {
            value += char
            readChar()
            char = peekChar()
        }

        return value
    }

    private fun snippet(text: String, length: Int = 50) =
            if (text.length < length) text else text.substring(0, length) + "..."
}
