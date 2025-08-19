/**
 * Factory for creating Lexer instances with different configurations.
 * Follows Factory pattern and Open/Closed principle for easy extension.
 */
object LexerGenerator {
    fun createLexer(tokenRule: TokenRule): Lexer = Lexer(tokenRule)

    fun createLexer(tokenRules: Map<String, TokenType>): Lexer {
        val tokenRule =
            TokenRule
                .builder()
                .addRules(tokenRules)
                .build()
        return Lexer(tokenRule)
    }

    fun createLexer(tokenRules: LinkedHashMap<String, TokenType>): Lexer = Lexer(TokenRule.fromMap(tokenRules))

    fun createDefaultLexer(): Lexer {
        val rules =
            linkedMapOf(
                // Keywords (word boundaries for exact matches)
                "\\blet\\b" to TokenType.VariableDeclaration,
                "\\bprintln\\b" to TokenType.FunctionCall,
                // Data types
                "\\bstring\\b" to TokenType.StringType,
                "\\bnumber\\b" to TokenType.NumberType,
                // String Literals (both single and double quotes as per PrintScript spec)
                "\"([^\"\\\\]|\\\\.)*\"" to TokenType.StringLiteral,
                "'([^'\\\\]|\\\\.)*'" to TokenType.StringLiteral,
                // Number Literals (integers and decimals as per PrintScript spec)
                "\\d+\\.\\d+" to TokenType.NumberLiteral, // Decimals first
                "\\d+" to TokenType.NumberLiteral, // Then integers
                // Multi-character operators (must come before single character ones)
                "==" to TokenType.Equals,
                "!=" to TokenType.NotEquals,
                "<=" to TokenType.LessThanOrEqual,
                ">=" to TokenType.GreaterThanOrEqual,
                // Single character operators and symbols
                "=" to TokenType.Assignment,
                "\\+" to TokenType.Plus,
                "-" to TokenType.Minus,
                "\\*" to TokenType.Multiply,
                "/" to TokenType.Divide,
                "<" to TokenType.LessThan,
                ">" to TokenType.GreaterThan,
                ":" to TokenType.Colon,
                ";" to TokenType.Semicolon,
                "\\(" to TokenType.LeftParen,
                "\\)" to TokenType.RightParen,
                // Identifiers (must come last to not conflict with keywords)
                "[a-zA-Z_][a-zA-Z0-9_]*" to TokenType.Identifier,
            )
        return createLexer(rules)
    }
}
