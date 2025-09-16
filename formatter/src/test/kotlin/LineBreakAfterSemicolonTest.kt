import config.FormatterStyleConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import rules.implementations.LineBreakAfterSemicolon

class LineBreakAfterSemicolonTest {
    private fun createToken(
        type: TokenType,
        lexeme: String,
    ) = Token(type, lexeme, Location(1, 1, 1))

    private fun createConfig(lineBreakAfterSemicolon: Boolean) =
        FormatterStyleConfig(
            lineBreakBeforePrintln = 1,
            lineBreakAfterSemicolon = lineBreakAfterSemicolon,
            spaceBeforeColon = true,
            spaceAfterColon = true,
            spaceAroundAssignment = true,
            spaceAroundOperators = true,
            indentation = 4,
            ifBraceBelowLine = false,
            inlineIfBraceIfStatement = true,
        )

    @Test
    fun `after agrega salto de linea despues de semicolon cuando esta habilitado`() {
        val prev = createToken(TokenType.NumberLiteral, "5")
        val curr = createToken(TokenType.Semicolon, ";")
        val next = createToken(TokenType.Identifier, "x")
        val config = createConfig(lineBreakAfterSemicolon = true)
        val doc = DocBuilder.inMemory().write("x = 5;")

        val result = LineBreakAfterSemicolon.after(prev, curr, next, config, doc)

        assertEquals("x = 5;\n", result.build())
    }

    @Test
    fun `after no agrega salto de linea cuando esta deshabilitado`() {
        val prev = createToken(TokenType.NumberLiteral, "5")
        val curr = createToken(TokenType.Semicolon, ";")
        val next = createToken(TokenType.Identifier, "x")
        val config = createConfig(lineBreakAfterSemicolon = false)
        val doc = DocBuilder.inMemory().write("x = 5;")

        val result = LineBreakAfterSemicolon.after(prev, curr, next, config, doc)

        assertEquals("x = 5;", result.build())
    }

    @Test
    fun `after no afecta tokens que no son semicolon`() {
        val prev = createToken(TokenType.Identifier, "x")
        val curr = createToken(TokenType.Assignment, "=")
        val next = createToken(TokenType.NumberLiteral, "5")
        val config = createConfig(lineBreakAfterSemicolon = true)
        val doc = DocBuilder.inMemory().write("x =")

        val result = LineBreakAfterSemicolon.after(prev, curr, next, config, doc)

        assertEquals("x =", result.build())
    }
}
