package parser

import AstNode
import Result
import TokenStream
import TokenType
import validators.provider.ValidatorsProvider

class Parser(
    private val validatorsProvider: ValidatorsProvider,
) {
    fun parse(stream: TokenStream): Result<AstNode, ParseError> {
        // Process the stream and return the first valid AST node found
        while (stream.peek().type !is TokenType.EOF) {
            return when (val result = validatorsProvider.findValidatorAndBuild(stream)) {
                is Result.Success -> result // Return immediately when we find a valid node
                is Result.Failure -> {
                    Result.Failure(result.error ?: ParseError.NoValidParser(listOf(stream.peek())))
                }
            }
        }

        // If we reach here, the stream was empty
        return Result.Failure(ParseError.NoValidParser(listOf(stream.peek())))
    }
}
