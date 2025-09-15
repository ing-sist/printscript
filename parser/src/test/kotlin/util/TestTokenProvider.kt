package util

import Location
import Token
import TokenStream
import TokenType

/**
 * Simple TokenStream for tests that supports lookahead and a single-step lookbehind (k = -1).
 * It does not depend on the lexer module, keeping parser tests decoupled.
 */
class TestTokenProvider(
    tokens: List<Token>,
) : TokenStream {
    private val tokens: MutableList<Token> = tokens.toMutableList()
    private val history: MutableList<Token> = mutableListOf()
    private var index: Int = 0

    override fun peek(k: Int): Token {
        if (k >= 0) {
            val i = index + k
            return if (i in tokens.indices) tokens[i] else tokens.lastOrNull() ?: eof()
        }
        // Support peek(-1) used in some validators for error messages
        val histIndex = history.size + k // k is negative
        return if (histIndex in history.indices) history[histIndex] else history.lastOrNull() ?: eof()
    }

    override fun consume(): Token {
        if (index >= tokens.size) return tokens.lastOrNull() ?: eof()
        val token = tokens[index]
        index++
        history.add(token)
        return token
    }

    private fun eof(): Token = Token(TokenType.EOF, "EOF", Location(0, 0, 0))
}

/** Test helpers */
fun tok(
    type: TokenType,
    lexeme: String = type.toString(),
): Token = Token(type, lexeme, Location(1, 1, 1 + lexeme.length))
