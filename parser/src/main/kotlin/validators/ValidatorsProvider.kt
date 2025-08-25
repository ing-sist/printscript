package validators

import Result
import Token
import builders.AstBuilder
import parser.ParseError

interface ValidatorsProvider {
    fun getValidator(tokens: List<Token>): Result<AstBuilder, ParseError>
}

class DefaultValidatorsProvider : ValidatorsProvider {
    private val validators =
        listOf(
            DeclarationAssignmentValidator(),
            DeclarationValidator(),
            AssignmentValidator(),
            PrintlnValidator(),
        )

    override fun getValidator(tokens: List<Token>): Result<AstBuilder, ParseError> {
        for (validator in validators) {
            val result = validator.validate(tokens)
            if (result is Result.Success<AstBuilder>) {
                return result
            }
        }
        return Result.Failure(ParseError.NoValidParser(tokens))
    }
}
