// src/test/kotlin/builders/expresionHelpers/AstNodeFactoryErrorTest.kt
package builders

import TokenType
import builders.expresionHelpers.AstNodeFactory
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import util.tok

class AstNodeFactoryErrorTest {
    @Test
    @DisplayName("AstNodeFactory lanza IllegalArgumentException para tokens no soportados")
    fun `throws on unsupported token types`() {
        val factory = AstNodeFactory()

        // Elegimos tokens claramente no mapeados en el when del factory
        val unsupported =
            listOf(
                tok(TokenType.Plus, "+"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.RightParen, ")"),
            )

        unsupported.forEach { token ->
            val ex =
                assertThrows(IllegalArgumentException::class.java) {
                    factory.createFromToken(token)
                }
            // Mensaje esperado: "Cannot create AstNode from token type: ${token.type} with value: '${token.lexeme}'"
            val msg = ex.message ?: ""
            assertTrue(
                msg.contains("Cannot create AstNode"),
                "El mensaje debe explicar el motivo",
            )
            assertTrue(
                msg.contains(token.type.toString()),
                "El mensaje debe incluir el tipo: ${token.type}",
            )
            assertTrue(
                msg.contains("'${token.lexeme}'"),
                "El mensaje debe incluir el lexema con comillas: '${token.lexeme}'",
            )
        }
    }
}
