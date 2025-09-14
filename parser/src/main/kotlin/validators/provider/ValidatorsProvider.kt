package validators.provider

import AstNode
import Result
import TokenProvider
import parser.ParseError
import validators.AssignmentValidator
import validators.AstValidator
import validators.DeclarationAssignmentValidator
import validators.DeclarationValidator
import validators.FunctionCallValidator
import validators.IfValidator

interface ValidatorsProvider {
    fun findValidatorAndBuild(stream: TokenProvider): Result<AstNode, ParseError?>
}

class DefaultValidatorsProvider : ValidatorsProvider {
    private val validators: List<AstValidator> by lazy {
        listOf(
            IfValidator(this), // New for PrintScript 1.1 - must be before others to handle if statements
            DeclarationAssignmentValidator(),
            DeclarationValidator(),
            AssignmentValidator(),
            FunctionCallValidator(),
        )
    }

    override fun findValidatorAndBuild(stream: TokenProvider): Result<AstNode, ParseError?> {
        var result: Result<AstNode, ParseError?> = Result.Failure(ParseError.NoValidParser(listOf(stream.peek())))

        for (validator in validators) {
            when (val validatorResult = validator.validateAndBuild(stream)) {
                is Result.Success -> {
                    result = validatorResult
                    break
                }
                is Result.Failure -> {
                    if (validatorResult.error != null) {
                        // This validator matched but found a syntax error
                        result = validatorResult
                        return result
                    }
                    // This validator didn't match, try the next one
                }
            }
        }

        return result
    }
}
