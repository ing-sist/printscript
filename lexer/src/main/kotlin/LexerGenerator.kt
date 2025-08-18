import java.util.regex.Pattern

object LexerGenerator {

    fun createLexer(version: String): Result<Lexer, LexError> {
        return when (version) {
            "1.0" -> Result.Success(Lexer(getKeywords(), getRules()))
            else -> Result.Failure(
                LexError
                    .InvalidVersion("Versión no soportada: $version")
            )
        }
    }

    private fun getKeywords(): Map<String, TokenType> {
        return mapOf(
            "let" to TokenType.Let,
            "println" to TokenType.Println,
            "string" to TokenType.DataType.String,
            "number" to TokenType.DataType.Number
        )
    }

    private fun getRules(): Map<Pattern, TokenType> {
        return mapOf(
            // String Literals with "" or ''
            Pattern.compile("^\"[^\"]*\"") to TokenType.StringLiteral,
            Pattern.compile("^'[^']*'") to TokenType.StringLiteral,
            // Number Literals
            Pattern.compile("^\\d+(\\.\\d+)?") to TokenType.NumberLiteral,
            // Identifiers
            Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*") to TokenType.Identifier,

            // Symbols
            Pattern.compile("^:") to TokenType.Colon,
            Pattern.compile("^;") to TokenType.Semicolon,
            Pattern.compile("^\\(") to TokenType.LeftParen,
            Pattern.compile("^\\)") to TokenType.RightParen,

            // Operators (multi-character first)
            Pattern.compile("^(==|!=|<=|>=|[=+\\-*/])") to TokenType.Operator
        )
    }
}