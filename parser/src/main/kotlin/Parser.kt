
class Parser {

    fun parse(tokenList: List<Token>): Result<Ast, ParseError> {
        if (tokenList.isEmpty()) {
            return Result.Failure(ParseError.WrongTokenOrder(Token(TokenType.EOF, "", Location(1, 1, 1))))
        }
        val rules = listOf<List<TokenType>>(
            listOf(
                TokenType.FunctionCall,
                TokenType.LeftParen,
                TokenType.Identifier,
                TokenType.RightParen,
                TokenType.Semicolon
            ),
            listOf(
                TokenType.VariableDeclaration,
                TokenType.Identifier,
                TokenType.Colon,
                TokenType.NumberType,
                TokenType.Assignment,
                TokenType.NumberLiteral,
                TokenType.Semicolon
            ),
            listOf(
                TokenType.VariableDeclaration,
                TokenType.Identifier,
                TokenType.Colon,
                TokenType.StringType,
                TokenType.Assignment,
                TokenType.StringLiteral,
                TokenType.Semicolon
            ),
            listOf(
                TokenType.VariableDeclaration,
                TokenType.Identifier,
                TokenType.Colon,
                TokenType.StringType,
                TokenType.Semicolon
            ),
            listOf(
                TokenType.VariableDeclaration,
                TokenType.Identifier,
                TokenType.Colon,
                TokenType.NumberType,
                TokenType.Semicolon
            ),
        )
    }
}