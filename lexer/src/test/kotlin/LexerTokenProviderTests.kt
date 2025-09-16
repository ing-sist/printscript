import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.StringReader

class LexerTokenProviderTests {
    private fun providerFrom(
        code: String,
        readSpace: Boolean = false,
        readNewline: Boolean = false,
    ): LexerTokenProvider {
        val rule = RuleGenerator.createDefaultTokenRule()
        val lex = Lexer(StringReader(code), rule)
        return LexerTokenProvider(lex, readSpace, readNewline)
    }

    private fun drainTypes(p: LexerTokenProvider): List<TokenType> {
        val types = mutableListOf<TokenType>()
        while (true) {
            val t = p.consume()
            types += t.type
            if (t.type is TokenType.EOF) break
        }
        return types
    }

    @Test
    @DisplayName("peek(0), peek(2) y peek(k grande) rellenan el buffer y devuelven EOF al pasar el tamaño")
    fun peekFillsBufferAndReturnsEOFWhenBeyond() {
        val p = providerFrom("1 + 2")

        // peek(0) -> '1'
        val t0 = p.peek(0)
        assertTrue(t0.type is TokenType.NumberLiteral)
        assertEquals("1", t0.lexeme)

        // peek(2) -> '2'
        val t2 = p.peek(2)
        assertTrue(t2.type is TokenType.NumberLiteral)
        assertEquals("2", t2.lexeme)

        // peek muy grande (debe devolver el último token en buffer, que es EOF)
        val t100 = p.peek(100)
        assertTrue(t100.type is TokenType.EOF)
    }

    @Test
    @DisplayName("consume() sin peek previo usa peek interno y entrega tokens en orden")
    fun consumeWithoutPriorPeekUsesInternalPeek() {
        val p = providerFrom("1 + 2")
        val c1 = p.consume()
        val c2 = p.consume()
        val c3 = p.consume()
        val c4 = p.consume()

        assertTrue(c1.type is TokenType.NumberLiteral) // 1
        assertTrue(c2.type is TokenType.Plus) // +
        assertTrue(c3.type is TokenType.NumberLiteral) // 2
        assertTrue(c4.type is TokenType.EOF) // EOF
    }

    @Test
    @DisplayName("peek no consume: tras peek(1), consume() entrega el primer token")
    fun peekDoesNotConsume() {
        val p = providerFrom("1 + 2")
        val peek1 = p.peek(1) // fuerza lookahead a contener al menos [1,+]
        assertTrue(peek1.type is TokenType.Plus)

        val c1 = p.consume()
        assertTrue(c1.type is TokenType.NumberLiteral) // aún entrega '1', no '+'
        assertEquals("1", c1.lexeme)
    }

    @Test
    @DisplayName("ignora espacios y saltos de línea cuando readSpace=false y readNewline=false")
    fun ignoresWhitespaceWhenFlagsFalse() {
        val p = providerFrom("1 \n   +   2", readSpace = false, readNewline = false)
        val types = drainTypes(p)

        // Esperamos: 1, +, 2, EOF
        assertEquals(4, types.size)
        assertTrue(types[0] is TokenType.NumberLiteral)
        assertTrue(types[1] is TokenType.Plus)
        assertTrue(types[2] is TokenType.NumberLiteral)
        assertTrue(types[3] is TokenType.EOF)
    }

    @Test
    @DisplayName("peek más allá del EOF devuelve siempre EOF (rama getOrElse)")
    fun peekBeyondAfterEOFAlwaysReturnsEOF() {
        val p = providerFrom("42")
        // Fuerza a que el buffer llegue a EOF
        assertTrue(p.peek(1).type is TokenType.EOF)

        // Ahora pedir mucho más allá usa la rama getOrElse { last() } -> EOF
        repeat(3) {
            assertTrue(p.peek(999).type is TokenType.EOF)
        }
    }

    @Test
    @DisplayName("RightParen suelto no rompe: provider procesa y termina en EOF")
    fun strayRightParenHandled() {
        val p = providerFrom(")")
        val first = p.consume()
        // Dependiendo de la gramática, puede venir como RightParen o ERROR -> si es ERROR lo cubre el test de error abajo.
        if (first.type is TokenType.ERROR) {
            // Dejado explícito: si es ERROR, el test de abajo cubre la excepción, aquí sólo verificamos EOF si no lanza.
            return
        }
        val rest = drainTypes(p)
        assertTrue(rest.last() is TokenType.EOF)
    }

    @Test
    @DisplayName("TokenType.ERROR del lexer dispara LexerException con mensaje formateado")
    fun errorTokenFromLexerThrowsLexerException() {
        val p = providerFrom("@") // carácter inválido típico

        val ex =
            assertThrows(LexerException::class.java) {
                p.peek(0) // forzamos a leer el primer token del lexer
            }

        val msg = ex.message ?: ""
        // Ej: "Error léxico en línea 1, columna 1: '@'"
        assertTrue(msg.contains("Error léxico en línea"), msg)
        assertTrue(msg.contains("columna"), msg)
        assertTrue(msg.contains("@"), msg)
    }

    @Test
    @DisplayName("consumir todo hasta EOF y no consumir de más (invariantes básicos)")
    fun consumeUntilEOF() {
        val p = providerFrom("10 * (2 + 3)")
        val types = drainTypes(p)

        assertTrue(types.first() is TokenType.NumberLiteral)
        assertTrue(types.any { it is TokenType.Multiply })
        assertTrue(types.any { it is TokenType.Plus })
        assertTrue(types.last() is TokenType.EOF)

        // No intentamos consumir luego de EOF para no forzar comportamiento indefinido.
    }
}
