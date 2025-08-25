package validators

import Result
import Token
import TokenType
import builders.AssignmentBuilder
import builders.AstBuilder
import parser.ParseError

class AssignmentValidator : AstValidators {
    override fun validate(tokens: List<Token>): Result<AstBuilder, ParseError> {
        val isValid =
            tokens.size >= 4 &&
                tokens[0].type is TokenType.Identifier &&
                tokens[1].type is TokenType.Assignment &&
                tokens.last().type is TokenType.Semicolon

        return if (isValid) {
            Result.Success(AssignmentBuilder())
        } else {
            Result.Failure(ParseError.InvalidSyntax(tokens, "Expected: <identifier> = <expression> ;"))
        }
    }
}
