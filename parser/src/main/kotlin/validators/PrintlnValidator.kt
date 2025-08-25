package validators

import Result
import Token
import TokenType
import builders.AstBuilder
import builders.PrintlnBuilder
import parser.ParseError

class PrintlnValidator : AstValidators {
    override fun validate(tokens: List<Token>): Result<AstBuilder, ParseError> {
        val isValid =
            tokens.size >= 5 &&
                tokens[0].type is TokenType.FunctionCall &&
                tokens[0].lexeme == "println" &&
                tokens[1].type is TokenType.LeftParen &&
                tokens[tokens.size - 2].type is TokenType.RightParen &&
                tokens.last().type is TokenType.Semicolon

        return if (isValid) {
            Result.Success(PrintlnBuilder())
        } else {
            Result.Failure(ParseError.InvalidSyntax(tokens, "Expected: println( <expression> ) ;"))
        }
    }
}
