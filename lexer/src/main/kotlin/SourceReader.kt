/**
 * Maneja la lectura y posición del código fuente.
 */
class SourceReader(
    private val source: String,
) {
    private var position = 0
    private var line = 1
    private var column = 1

    fun hasMore(): Boolean = position < source.length

    fun peek(): Char? = if (hasMore()) source[position] else null

    fun read(): Char? {
        if (!hasMore()) return null

        val char = source[position++]
        if (char == '\n') {
            line++
            column = 1
        } else {
            column++
        }
        return char
    }

    fun readWhile(predicate: (Char) -> Boolean): String {
        val result = StringBuilder()
        while (hasMore() && predicate(peek()!!)) {
            result.append(read())
        }
        return result.toString()
    }

    fun getPosition(): SourcePosition = SourcePosition(source.substring(position), line, column)
}
