/**
 * Lexical analyzer for PrintScript language.
 * Follows Single Responsibility Principle by focusing only on orchestrating the lexing process.
 */
class Lexer(private val tokenRule: TokenRule) {
    private val tokenMatcher = TokenMatcher(tokenRule)

    fun lex(source: String): Result<List<Token>, LexError> {
        val tokens = mutableListOf<Token>()
        var currentSource = SourcePosition(source, 1, 1)

        while (currentSource.hasRemaining()) {
            currentSource = skipWhitespace(currentSource)

            if (!currentSource.hasRemaining()) break

            when (val matchResult = processNextToken(currentSource)) {
                is Result.Success -> {
                    tokens.add(matchResult.value.token)
                    currentSource = matchResult.value.position
                }
                is Result.Failure -> return Result.Failure(matchResult.error)
            }
        }

        return finalizeTokenList(tokens, currentSource)
    }

    private fun skipWhitespace(source: SourcePosition): SourcePosition {
        var remaining = source.text
        var currentLine = source.line
        var currentColumn = source.column

        while (remaining.isNotEmpty() && remaining.first().isWhitespace()) {
            if (remaining.first() == '\n') {
                currentLine++
                currentColumn = 1
            } else {
                currentColumn++
            }
            remaining = remaining.substring(1)
        }

        return SourcePosition(remaining, currentLine, currentColumn)
    }

    private fun processNextToken(source: SourcePosition): Result<TokenResult, LexError> {
        return tokenMatcher.findNextToken(source.text, source.line, source.column)
    }

    private fun finalizeTokenList(tokens: List<Token>, source: SourcePosition): Result<List<Token>, LexError> {
        val tokensWithEof = tokens.toMutableList().apply {
            add(Token(TokenType.EOF, "", Location(source.line, source.column, source.column)))
        }
        return Result.Success(tokensWithEof)
    }
}