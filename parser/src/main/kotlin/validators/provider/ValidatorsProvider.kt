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
        for (validator in validators) {
            when (val result = validator.validateAndBuild(stream)) {
                is Result.Success -> return result
                is Result.Failure -> {
                    if (result.error != null) {
                        // This validator matched but found a syntax error
                        return result
                    }
                    // This validator didn't match, try the next one
                }
            }
        }
        return Result.Failure(ParseError.NoValidParser(listOf(stream.peek())))
    }
}

class ParserValidatorsFactory {
    fun create(version : String): List<AstValidator> {
        return when (version) {
            "1.0" -> listOf(
                DeclarationAssignmentValidator(),
                DeclarationValidator(),
                AssignmentValidator(),
                FunctionCallValidator(),
            )
            "1.1" -> listOf(
                IfValidator(DefaultValidatorsProvider()), // New for PrintScript 1.1 - must be before others to handle if statements
                DeclarationAssignmentValidator(),
                DeclarationValidator(),
                AssignmentValidator(),
                FunctionCallValidator(),
            )
            else -> throw IllegalArgumentException("Unsupported version: $version")
        }
    }
}
