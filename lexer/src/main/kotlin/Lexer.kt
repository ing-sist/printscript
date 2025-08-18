import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.iterator

class Lexer(
    private val keywordRules: Map<String, TokenType>,
    private val generalRules: Map<Pattern, TokenType>

) {
    fun lex(source: String): Result<List<Token>, LexError> {

        val tokens = mutableListOf<Token>()
        var remainingSource = source
        var currentLine = 1
        var currentColumn = 1

        while (remainingSource.isNotEmpty()) {
            val initialWhitespace = remainingSource.takeWhile { it.isWhitespace() }
            if (initialWhitespace.isNotEmpty()) {
                val newlines = initialWhitespace.count { it == '\n' }
                if (newlines > 0) {
                    currentLine += newlines
                    currentColumn = initialWhitespace.length - initialWhitespace.lastIndexOf('\n')
                } else {
                    currentColumn += initialWhitespace.length
                }
                remainingSource = remainingSource.substring(initialWhitespace.length)
                if (remainingSource.isEmpty()) break
            }
            var matchFound = false

            // Paso 1: Keywords y data types
            for ((kw, type) in keywordRules) {
                val regex = Regex("^\\b$kw\\b")
                val match = regex.find(remainingSource)
                if (match != null) {
                    val lexeme = match.value
                    tokens.add(Token(type, lexeme, Location(currentLine, currentColumn)))
                    currentColumn += lexeme.length
                    remainingSource = remainingSource.substring(lexeme.length)
                    matchFound = true
                    break
                }
            }
            if (matchFound) continue

            // Paso 2: Reglas generales (Map de Pattern)
            for ((pattern, tokenType) in generalRules) {
                val matcher: Matcher = pattern.matcher(remainingSource)
                if (matcher.find() && matcher.start() == 0) {
                    val lexeme = matcher.group()
                    tokens.add(Token(tokenType, lexeme, Location(currentLine, currentColumn)))
                    currentColumn += lexeme.length
                    remainingSource = remainingSource.substring(lexeme.length)
                    matchFound = true
                    break
                }
            }
            if (!matchFound) {
                return Result.Failure(
                    LexError
                        .SyntaxError(
                            "Error de syntax: Token inesperado en línea $currentLine, " +
                                    "columna $currentColumn cerca de '${remainingSource.take(10)}...'"))
            }
        }
        tokens.add(Token(TokenType.EOF, "", Location(currentLine, currentColumn)))
        return Result.Success(tokens)
    }
}