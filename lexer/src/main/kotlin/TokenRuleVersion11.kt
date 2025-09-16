/**
 * Token rules implementation for PrintScript 1.1.
 * Extends PrintScript 1.0 rules with additional features for conditionals and constants.
 */
class TokenRuleVersion11 : TokenRule {
    override val tokenRules: LinkedHashMap<String, TokenType> =
        linkedMapOf(
            // Espacios en blanco
            "\\s+" to TokenType.Space,
            // Keywords (word boundaries for exact matches) - 1.0 + 1.1 extensions
            "\\blet\\b" to TokenType.Keyword.VariableDeclaration,
            "\\bconst\\b" to TokenType.Keyword.ConstDeclaration, // New in 1.1
            "\\bif\\b" to TokenType.Keyword.If, // New in 1.1
            "\\belse\\b" to TokenType.Keyword.Else, // New in 1.1
            "\\bprintln\\b" to TokenType.FunctionCall,
            "\\breadInput\\b" to TokenType.FunctionCall, // New in 1.1
            "\\breadEnv\\b" to TokenType.FunctionCall, // New in 1.1
            // Boolean literals (must come before identifier) - New in 1.1
            "\\btrue\\b" to TokenType.BooleanLiteral,
            "\\bfalse\\b" to TokenType.BooleanLiteral,
            // Data types - 1.0 + 1.1 extensions
            "\\bstring\\b" to TokenType.Keyword.StringType,
            "\\bnumber\\b" to TokenType.Keyword.NumberType,
            "\\bboolean\\b" to TokenType.Keyword.BooleanType, // New in 1.1
            // String Literals (both single and double quotes as per PrintScript spec)
            "\"([^\"\\\\]|\\\\.)*\"" to TokenType.StringLiteral,
            "'([^'\\\\]|\\\\.)*'" to TokenType.StringLiteral,
            // Number Literals (integers and decimals as per PrintScript spec)
            "\\d+\\.\\d+" to TokenType.NumberLiteral, // Decimals first
            "\\d+" to TokenType.NumberLiteral, // Then integers
            // Single character operators and symbols
            "=" to TokenType.Assignment,
            "\\+" to TokenType.Plus,
            "-" to TokenType.Minus,
            "\\*" to TokenType.Multiply,
            "/" to TokenType.Divide,
            ":" to TokenType.Colon,
            ";" to TokenType.Semicolon,
            "\\(" to TokenType.LeftParen,
            "\\)" to TokenType.RightParen,
            // New symbols for control structures - New in 1.1
            "\\{" to TokenType.LeftBrace,
            "\\}" to TokenType.RightBrace,
            // Identifiers (must come last to not conflict with keywords)
            "[a-zA-Z_][a-zA-Z0-9_]*" to TokenType.Identifier,
        )
}
