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

    // Fixed-size read chunk; reused, not the source of OOM
    private val readBuffer = CharArray(1024)

    /**
     * Returns next token. If readSpace == false, whitespace tokens are skipped.
     */
    fun nextToken(readSpace: Boolean): Token {
        while (true) {
            val token = produceNextToken()
            if (!readSpace && token.type is TokenType.Space) continue
            return token
        }
    }

    /**
     * Produces the next token (including spaces). On a match failure, attempts
     * to read more input to complete a partial token before emitting an error.
     * Refactored to have a single return to satisfy Detekt ReturnCount.
     */
    private fun produceNextToken(): Token {
        var emitted: Token? = null

        while (emitted == null) {
            if (buffer.isEmpty()) {
                fillBuffer()
                if (buffer.isEmpty()) {
                    emitted = createToken("EOF", TokenType.EOF)
                    return emitted
                }
            }

            when (
                val matchResult =
                    tokenMatcher.findNextToken(
                        buffer.toString(),
                        currentLine,
                        currentColumn,
                    )
            ) {
                is Result.Success -> {
                    val token = matchResult.value
                    consumeFromBuffer(token.lexeme)
                    emitted = token
                }

                is Result.Failure -> {
                    // Try to append more data to resolve a partial token
                    if (!isEndOfFile) {
                        val before = buffer.length
                        fillBuffer()
                        if (buffer.length > before) {
                            // Got more input; retry matching with the larger buffer
                            continue
                        }
                    }
                    // No more input (or no progress): consume one char and report error
                    val invalidChar = buffer.first()
                    consumeFromBuffer(invalidChar.toString())
                    emitted = createToken(matchResult.toString(), TokenType.ERROR)
                }
            }
        }

        return emitted
    }

    private fun consumeFromBuffer(lexeme: String) {
        val newlines = lexeme.count { it == '\n' }
        if (newlines > 0) {
            currentLine += newlines
            currentColumn = lexeme.length - lexeme.lastIndexOf('\n')
        } else {
            currentColumn += lexeme.length
        }
        buffer.delete(0, lexeme.length)
    }

    /**
     * Reads more characters from the Reader and appends them to the buffer.
     */
    private fun fillBuffer() {
        if (isEndOfFile) return

        val charsRead = reader.read(readBuffer)
        if (charsRead != -1) {
            buffer.append(readBuffer, 0, charsRead)
        } else {
            isEndOfFile = true
            reader.close()
        }
    }

    private fun createToken(
        lexeme: String,
        tokenType: TokenType,
    ): Token =
        Token(
            tokenType,
            lexeme,
            Location(currentLine, currentColumn, currentColumn + lexeme.length - 1),
        )
}
