import java.io.Reader

class Lexer(
    private val reader: Reader,
    tokenRule: TokenRule,
) {
    private val tokenMatcher = TokenMatcher(tokenRule)
    private val buffer = StringBuilder()
    private var currentLine = 1
    private var currentColumn = 1
    private var isEndOfFile = false

    // Reusable read chunk
    private val readBuffer = CharArray(1024)

    /**
     * Returns next token. If readSpace == false or readNewline == false, those tokens are skipped.
     */
    fun nextToken(
        readSpace: Boolean,
        readNewline: Boolean,
    ): Token {
        while (true) {
            val token = produceNextToken()
            val skip =
                (token.type is TokenType.Space && !readSpace) ||
                    (token.type is TokenType.Newline && !readNewline)
            if (!skip) return token
        }
    }

    /**
     * Boundary-safe producer with a single return and no continue/break in the loop body.
     * Maximal munch across buffer edges:
     * - If a match ends at buffer end and not EOF, try to read more and retry.
     * - On failure, try to read more; if no progress or EOF, emit single-char error.
     */
    private fun produceNextToken(): Token {
        var emitted: Token? = null

        while (emitted == null) {
            if (!ensureBufferNotEmpty()) {
                emitted = eofToken()
            } else {
                val startLine = currentLine
                val startCol = currentColumn
                val source = buffer.toString()
                val match = tokenMatcher.findNextToken(source, startLine, startCol)

                if (match is Result.Success) {
                    val token = match.value
                    val needMore = shouldReadMoreFor(token) && tryReadMore()
                    if (!needMore) {
                        consumeFromBuffer(token.lexeme)
                        emitted = token
                    }
                } else {
                    val readMore = tryReadMore()
                    if (!readMore) {
                        val badLexeme = buffer.substring(0, 1)
                        val error = createErrorToken(badLexeme, startLine, startCol)
                        consumeFromBuffer(badLexeme)
                        emitted = error
                    }
                }
            }
        }

        return emitted
    }

    // --- Helpers ---

    private fun ensureBufferNotEmpty(): Boolean {
        if (buffer.isNotEmpty()) return true
        fillBuffer()
        return buffer.isNotEmpty()
    }

    private fun eofToken(): Token = Token(TokenType.EOF, "EOF", Location(currentLine, currentColumn, currentColumn))

    private fun shouldReadMoreFor(token: Token): Boolean = token.lexeme.length == buffer.length && !isEndOfFile

    private fun tryReadMore(): Boolean {
        val before = buffer.length
        fillBuffer()
        return buffer.length > before
    }

    private fun createErrorToken(
        lexeme: String,
        line: Int,
        col: Int,
    ): Token = Token(TokenType.ERROR, lexeme, Location(line, col, col + lexeme.length - 1))

    private fun consumeFromBuffer(lexeme: String) {
        val newlines = lexeme.count { it == '\n' }
        if (newlines > 0) {
            currentLine += newlines
            val lastNl = lexeme.lastIndexOf('\n')
            val charsAfterLastNl = lexeme.length - lastNl
            currentColumn = charsAfterLastNl
        } else {
            currentColumn += lexeme.length
        }
        buffer.delete(0, lexeme.length)
    }

    private fun fillBuffer() {
        if (isEndOfFile) return
        val read = reader.read(readBuffer)
        if (read > 0) {
            buffer.append(readBuffer, 0, read)
        } else {
            isEndOfFile = true
            try {
                reader.close()
            } catch (_: Exception) {
                // Ignore close errors
            }
        }
    }
}
