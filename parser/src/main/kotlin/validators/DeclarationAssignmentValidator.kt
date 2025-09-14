package validators

import AstNode
import Result
import Token
import TokenProvider
import TokenType
import builders.DeclarationAssignmentBuilder
import parser.ParseError
import validators.helpers.DeclarationHelper
import validators.helpers.TokenConsumer

class DeclarationAssignmentValidator : AstValidator {
    override fun validateAndBuild(stream: TokenProvider): Result<AstNode, ParseError?> {
        // 1. Peek para verificar el patrón de declaración y asignación
        val tokens = listOf(
            stream.peek(0), stream.peek(1), stream.peek(2), stream.peek(3), stream.peek(4)
        )

        // Usar la lógica compartida para la parte de la declaración y el token de asignación
        if (DeclarationHelper.matchesDeclarationPattern(tokens) && tokens[4].type is TokenType.Assignment) {
            val consumedTokens = mutableListOf<Token>()

            // 2. Consumir la parte de la declaración y asignación
            for (i in 0..4) {
                consumedTokens.add(stream.consume())
            }

            // 3. Llama a la función auxiliar para consumir la expresión y el ';'
            consumedTokens.addAll(TokenConsumer.consumeExpressionAndSemicolon(stream))

            // 4. Construir el nodo
            val node = DeclarationAssignmentBuilder().build(consumedTokens)
            return Result.Success(node)
        }

        return Result.Failure(null) // No coincide
    }
}