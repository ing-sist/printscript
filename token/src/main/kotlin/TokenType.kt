sealed interface TokenType {
    // 1. Keywords
    data object VariableDeclaration : TokenType

    data object FunctionCall : TokenType

    // 2. Symbols
    data object Colon : TokenType

    data object Semicolon : TokenType

    data object LeftParen : TokenType

    data object RightParen : TokenType

    data object LeftBrace : TokenType

    data object RightBrace : TokenType

    data object Comma : TokenType

    // 3. Operators
    data object Assignment : TokenType

    data object Plus : TokenType

    data object Minus : TokenType

    data object Multiply : TokenType

    data object Divide : TokenType

    data object Equals : TokenType

    // Missing comparison operators
    data object NotEquals : TokenType

    data object LessThan : TokenType

    data object LessThanOrEqual : TokenType

    data object GreaterThan : TokenType

    data object GreaterThanOrEqual : TokenType

    // 4. Variables
    data object Identifier : TokenType

    data object StringLiteral : TokenType

    data object NumberLiteral : TokenType

    // 5. DataTypes
    data object StringType : TokenType

    data object NumberType : TokenType

    data object Comment : TokenType

    // 6. End of file
    data object EOF : TokenType

    data object Newline : TokenType

    data object Indent : TokenType

    data object Dedent : TokenType

    data object Space : TokenType
}
