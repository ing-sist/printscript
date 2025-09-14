package validators

import AstNode
import Result
import Token
import TokenProvider
import TokenType
import builders.FunctionCallBuilder
import parser.ParseError

class FunctionCallValidator : AstValidator {
    override fun validateAndBuild(stream: TokenProvider): Result<AstNode, ParseError?> {
        // 1. Peek at the structure: function ( ... ) ;
        val functionToken = stream.peek(0)
        val leftParen = stream.peek(1)

        var result: Result<AstNode, ParseError?> = Result.Failure(null)

        if (validateFunction(functionToken) && leftParen.type is TokenType.LeftParen) {
            val consumedTokens = mutableListOf<Token>()

            // 2. Consume function name and left parenthesis
            consumedTokens.add(stream.consume())
            consumedTokens.add(stream.consume())

            // 3. Consume expression tokens until ')'
            var parenDepth = 1
            while (parenDepth > 0 && stream.peek().type !is TokenType.EOF) {
                val token = stream.consume()
                consumedTokens.add(token)

                when (token.type) {
                    is TokenType.LeftParen -> parenDepth++
                    is TokenType.RightParen -> parenDepth--
                    else -> { /* do nothing */ }
                }
            }

            // 4. Consume the final ';'
            if (stream.peek().type is TokenType.Semicolon) {
                consumedTokens.add(stream.consume())

                // 5. Call the Builder
                val node = FunctionCallBuilder().build(consumedTokens)
                result = Result.Success(node)
            } else {
                result = Result.Failure(ParseError.UnexpectedToken(stream.peek(), "';'"))
            }
        }

        return result
    }

    private fun validateFunction(token: Token): Boolean =
        token.type is TokenType.FunctionCall &&
            validateFunctionName(token)

    private fun validateFunctionName(token: Token): Boolean = token.lexeme in listOf("println", "readEnv", "readInput")
}
