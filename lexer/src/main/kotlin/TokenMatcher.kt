import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Handles token matching logic using the provided rules.
 * Focuses only on finding the next token based on rules.
 */
class TokenMatcher(
    private val tokenRule: TokenRule,
) {
    private val compiledPatterns = compilePatterns(tokenRule)

    /**
     * Busca el siguiente token al principio del 'source'.
     * Devuelve el Token encontrado o un .LexError si no hay coincidencia.
     */
    fun findNextToken(
        source: String,
        line: Int,
        startCol: Int,
    ): Result<Token, LexError> {
        for ((pattern, tokenType) in compiledPatterns) {
            val matcher: Matcher = pattern.matcher(source)
            if (matcher.find() && matcher.start() == 0) {
                val lexeme = matcher.group()
                val token = createToken(lexeme, tokenType, line, startCol)
                // Simplemente devolvemos el token. El Lexer se encargará de la posición.
                return Result.Success(token)
            }
        }
        return createErrorResult(source, line, startCol)
    }

    private fun compilePatterns(tokenRule: TokenRule): List<Pair<Pattern, TokenType>> =
        tokenRule.tokenRules.map { (regex, tokenType) ->
            Pattern.compile("^$regex") to tokenType
        }

    private fun createToken(
        lexeme: String,
        tokenType: TokenType,
        line: Int,
        startCol: Int,
    ): Token {
        val endCol = startCol + lexeme.length - 1
        return Token(
            type = tokenType,
            lexeme = lexeme,
            location = Location(line, startCol, endCol),
        )
    }

    private fun createErrorResult(
        source: String,
        line: Int,
        column: Int,
    ): Result.Failure<LexError> {
        val preview = source.take(10)
        return Result.Failure(
            LexError.UnexpectedToken(
                line = line,
                column = column,
                preview = preview,
            ),
        )
    }
}

sealed class LexError {
    data class UnexpectedToken(
        val line: Int,
        val column: Int,
        val preview: String,
    ) : LexError()
}
