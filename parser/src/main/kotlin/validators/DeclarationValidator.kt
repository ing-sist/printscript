package validators

import Result
import Token
import TokenType
import builders.AstBuilder
import builders.DeclarationBuilder
import parser.ParseError

class DeclarationValidator : AstValidators {
    override fun validate(tokens: List<Token>): Result<AstBuilder, ParseError> {
        val isValid =
            tokens.size == 5 &&
                tokens[0].type is TokenType.VariableDeclaration &&
                tokens[1].type is TokenType.Identifier &&
                tokens[2].type is TokenType.Colon &&
                (tokens[3].type is TokenType.StringType || tokens[3].type is TokenType.NumberType) &&
                tokens[4].type is TokenType.Semicolon

        return if (isValid) {
            Result.Success(DeclarationBuilder())
        } else {
            Result.Failure(ParseError.InvalidSyntax(tokens, "Expected: let <identifier> : <type> ;"))
        }
    }
}
