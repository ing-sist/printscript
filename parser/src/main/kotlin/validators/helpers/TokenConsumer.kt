package validators.helpers

import Token
import TokenProvider
import parser.ParseError

/**
 * Consume tokens que forman una expresión hasta encontrar un punto y coma.
 * @return La lista de tokens de la expresión y el punto y coma final.
 */

object TokenConsumer {
    fun consumeExpressionAndSemicolon(stream: TokenProvider): List<Token> {
        val tokens = mutableListOf<Token>()
        var parenDepth = 0

        // Consume la expresión hasta encontrar un punto y coma,
        // o un salto de línea si está dentro de paréntesis
        while (true) {
            val nextToken = stream.peek()
            if (nextToken.type is TokenType.EOF) {
                throw ParseError.UnexpectedToken(nextToken, "';'")
            }

            if (nextToken.type is TokenType.LeftParen) {
                parenDepth++
            } else if (nextToken.type is TokenType.RightParen) {
                parenDepth--
            }

            if (parenDepth == 0 && nextToken.type is TokenType.Semicolon) {
                tokens.add(stream.consume())
                break
            }
            tokens.add(stream.consume())
        }
        return tokens
    }
}