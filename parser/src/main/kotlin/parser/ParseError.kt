package parser

import Token

/**
 * Represents different types of parsing errors
 */
sealed class ParseError(
    message: String,
) : Exception(message) {
    data class InvalidSyntax(
        val tokenGroup: List<Token>,
        val reason: String,
    ) : ParseError("Invalid syntax: $reason for tokens: ${tokenGroup.joinToString(" ") { it.lexeme }}")

    data class NoValidParser(
        val tokenGroup: List<Token>,
    ) : ParseError("No valid parser found for tokens: ${tokenGroup.joinToString(" ") { it.lexeme }}")

    data class UnexpectedToken(
        val token: Token,
        val expected: String,
    ) : ParseError("Unexpected token '${token.lexeme}' at ${token.location}. Expected: $expected")
}
