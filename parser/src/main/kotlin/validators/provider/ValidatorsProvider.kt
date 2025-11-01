package validators.provider

import AstNode
import Result
import TokenStream
import parser.ParseError
import validators.AstValidator

interface ValidatorsProvider {
    fun findValidatorAndBuild(stream: TokenStream): Result<AstNode, ParseError?>
}

class DefaultValidatorsProvider(
    version: String,
) : ValidatorsProvider {
    private val validators: List<AstValidator> = ValidatorsFactory.createValidators(version)

    override fun findValidatorAndBuild(stream: TokenStream): Result<AstNode, ParseError?> {
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
