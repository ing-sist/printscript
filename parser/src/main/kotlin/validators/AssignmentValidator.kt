package validators

import AstNode
import Result
import Token
import TokenProvider
import TokenType
import builders.AssignmentBuilder
import parser.ParseError
import validators.helpers.TokenConsumer

class AssignmentValidator : AstValidator {
    override fun validateAndBuild(stream: TokenProvider): Result<AstNode, ParseError?> {
        // 1. Peek at the structure: <id> =
        if (stream.peek(0).type is TokenType.Identifier && stream.peek(1).type is TokenType.Assignment) {
            val consumedTokens = mutableListOf<Token>()

            // 2. Consume the identifier and assignment operator
            consumedTokens.add(stream.consume())
            consumedTokens.add(stream.consume())

            // 3. Llama a la una funcion auxiliar para consumir la expresión y el ';'
            consumedTokens.addAll(TokenConsumer.consumeExpressionAndSemicolon(stream))

            // 4. Call the Builder
            val node = AssignmentBuilder().build(consumedTokens)
            return Result.Success(node)
        }

        return Result.Failure(null)
    }
}
