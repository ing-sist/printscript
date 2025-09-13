package validators

import Result
import Token
import TokenType
import builders.AstBuilder
import builders.DeclarationAssignmentBuilder
import parser.ParseError

class DeclarationAssignmentValidator : AstValidators {
    override fun validate(tokens: List<Token>): Result<AstBuilder, ParseError> {
        val isValid =
            tokens.size >= 7 &&
                tokens[0].type is TokenType.Keyword.VariableDeclaration &&
                tokens[1].type is TokenType.Identifier &&
                tokens[2].type is TokenType.Colon &&
                (tokens[3].type is TokenType.StringType || tokens[3].type is TokenType.NumberType) &&
                tokens[4].type is TokenType.Assignment &&
                tokens.last().type is TokenType.Semicolon

        return if (isValid) {
            Result.Success(DeclarationAssignmentBuilder())
        } else {
            Result.Failure(ParseError.InvalidSyntax(tokens, "Expected: let <identifier> : <type> = <expression> ;"))
        }
    }
}
