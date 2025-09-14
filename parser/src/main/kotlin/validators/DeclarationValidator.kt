package validators

import AstNode
import Result
import TokenProvider
import TokenType
import builders.DeclarationBuilder
import parser.ParseError
import validators.helpers.DeclarationHelper

class DeclarationValidator : AstValidator {
    override fun validateAndBuild(stream: TokenProvider): Result<AstNode, ParseError?> {
        // 1. Peek para verificar el patrón básico de declaración
        val tokens = listOf(
            stream.peek(0), stream.peek(1), stream.peek(2), stream.peek(3), stream.peek(4)
        )
        val afterSemicolon = stream.peek(5)

        if (DeclarationHelper.matchesDeclarationPattern(tokens) &&
            tokens[4].type is TokenType.Semicolon &&
            afterSemicolon.type !is TokenType.Assignment
        ) {
            // 2. Si coincide, consumir y construir
            val consumedTokens = tokens.take(5).map { stream.consume() }
            val node = DeclarationBuilder().build(consumedTokens)
            return Result.Success(node)
        }

        return Result.Failure(null) // No coincide, intenta con el siguiente validador
    }
}
