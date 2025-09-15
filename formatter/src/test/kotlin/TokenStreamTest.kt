import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TokenStreamTest {
    private fun createToken(
        type: TokenType,
        lexeme: String,
    ) = Token(type, lexeme, Location(1, 1, 1))

    @Test
    fun `consume tokens en orden`() {
        val tokens =
            listOf(
                createToken(TokenType.Identifier, "x"),
                createToken(TokenType.Assignment, "="),
                createToken(TokenType.NumberLiteral, "5"),
            )
        val stream = TokenStream(tokens)

        assertEquals("x", stream.consume().lexeme)
        assertEquals("=", stream.consume().lexeme)
        assertEquals("5", stream.consume().lexeme)
    }

    @Test
    fun `peek no consume tokens`() {
        val tokens =
            listOf(
                createToken(TokenType.Identifier, "x"),
                createToken(TokenType.Assignment, "="),
            )
        val stream = TokenStream(tokens)

        assertEquals("x", stream.peek(0).lexeme)
        assertEquals("=", stream.peek(1).lexeme)
        assertEquals("x", stream.peek(0).lexeme) // peek no cambia posición
    }

    @Test
    fun `peek con indices altos devuelve EOF`() {
        val tokens = listOf(createToken(TokenType.Identifier, "x"))
        val stream = TokenStream(tokens)

        assertEquals("x", stream.peek(0).lexeme)
        assertEquals("EOF", stream.peek(1).lexeme)
        assertEquals("EOF", stream.peek(10).lexeme)
    }

    @Test
    fun `consume despues del final devuelve EOF`() {
        val tokens = listOf(createToken(TokenType.Identifier, "x"))
        val stream = TokenStream(tokens)

        assertEquals("x", stream.consume().lexeme)
        assertEquals("EOF", stream.consume().lexeme)
        assertEquals("EOF", stream.consume().lexeme)
    }

    @Test
    fun `stream vacio retorna EOF inmediatamente`() {
        val stream = TokenStream(emptyList())

        assertEquals("EOF", stream.peek(0).lexeme)
        assertEquals("EOF", stream.consume().lexeme)
    }
}
