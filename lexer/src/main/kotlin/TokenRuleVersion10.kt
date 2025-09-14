/**
 * Token rules implementation for PrintScript 1.0.
 * Contains the basic tokenization rules for the initial version.
 */
class TokenRuleVersion10 : TokenRule {
    override val tokenRules: LinkedHashMap<String, TokenType> =
        linkedMapOf(
            // Espacios en blanco
            "\\s+" to TokenType.Space,
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
            // Identifiers (must come last to not conflict with keywords)
            "[a-zA-Z_][a-zA-Z0-9_]*" to TokenType.Identifier,
        )
}
