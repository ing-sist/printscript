import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Handles token matching logic using the provided rules.
 * Follows Single Responsibility Principle by focusing only on token matching.
 */
class TokenMatcher(private val tokenRule: TokenRule) {
    private val compiledPatterns = compilePatterns(tokenRule)

    fun findNextToken(source: String, line: Int, startCol: Int): Result<TokenResult, LexError> {
        for ((pattern, tokenType) in compiledPatterns) {
            val matcher: Matcher = pattern.matcher(source)
            if (matcher.find() && matcher.start() == 0) {
                val lexeme = matcher.group()
                val token = createToken(lexeme, tokenType, line, startCol)
                val newPosition = calculateNewPosition(source, lexeme, line, startCol)

                return Result.Success(
                    TokenResult(token, newPosition)
                )
            }
        }

        return createErrorResult(source, line, startCol)
    }

    private fun compilePatterns(tokenRule: TokenRule): List<Pair<Pattern, TokenType>> {
        return tokenRule.tokenRules.map { (regex, tokenType) ->
            Pattern.compile("^$regex") to tokenType
        }
    }

    private fun createToken(lexeme: String, tokenType: TokenType, line: Int, startCol: Int): Token {
        val endCol = startCol + lexeme.length - 1
        return Token(
            type = tokenType,
            lexeme = lexeme,
            location = Location(line, startCol, endCol)
        )
    }

    private fun calculateNewPosition(source: String, lexeme: String, line: Int, startCol: Int): SourcePosition {
        val remainingSource = source.substring(lexeme.length)
        val newlines = lexeme.count { it == '\n' }

        val (newLine, newColumn) = if (newlines > 0) {
            line + newlines to lexeme.length - lexeme.lastIndexOf('\n')
        } else {
            line to startCol + lexeme.length
        }

        return SourcePosition(remainingSource, newLine, newColumn)
    }

    private fun createErrorResult(source: String, line: Int, column: Int): Result.Failure<LexError> {
        val preview = source.take(10)
        return Result.Failure(
            LexError.UnexpectedToken(
                line = line,
                column = column,
                preview = preview
            )
        )
    }
}