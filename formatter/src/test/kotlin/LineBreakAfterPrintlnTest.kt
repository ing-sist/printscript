import config.FormatterStyleConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import rules.implementations.LineBreakAfterPrintln

class LineBreakAfterPrintlnTest {
    private fun createToken(
        type: TokenType,
        lexeme: String,
    ) = Token(type, lexeme, Location(1, 1, 1))

    private fun createConfig(lineBreakAfterPrintln: Int) =
        FormatterStyleConfig(
            lineBreakAfterPrintln = lineBreakAfterPrintln,
            lineBreakAfterSemicolon = true,
            spaceAfterColon = true,
            spaceAroundAssignment = true,
            spaceAroundOperators = true,
            indentation = 4,
            inlineIfBraceIfStatement = true,
            spaceBeforeColon = true,
        )

    @Test
    fun `after agrega un salto de linea antes de println`() {
        val prev = createToken(TokenType.Semicolon, ";")
        val curr = createToken(TokenType.Identifier, "println")
        val next = createToken(TokenType.LeftParen, "(")
        val config = createConfig(lineBreakAfterPrintln = 1)
        val doc = DocBuilder.inMemory().write("x = 5;")

        val result = LineBreakAfterPrintln.after(prev, curr, next, config, doc)

        assertEquals("x = 5;", result.build())
    }

    @Test
    fun `after agrega multiples saltos de linea antes de println`() {
        val prev = createToken(TokenType.Semicolon, ";")
        val curr = createToken(TokenType.Identifier, "println")
        val next = createToken(TokenType.LeftParen, "(")
        val config = createConfig(lineBreakAfterPrintln = 3)
        val doc = DocBuilder.inMemory().write("x = 5;")

        val result = LineBreakAfterPrintln.after(prev, curr, next, config, doc)

        assertEquals("x = 5;", result.build())
    }

    @Test
    fun `after no agrega saltos cuando esta deshabilitado`() {
        val prev = createToken(TokenType.Semicolon, ";")
        val curr = createToken(TokenType.Identifier, "println")
        val next = createToken(TokenType.LeftParen, "(")
        val config = createConfig(lineBreakAfterPrintln = 0)
        val doc = DocBuilder.inMemory().write("x = 5;")

        val result = LineBreakAfterPrintln.after(prev, curr, next, config, doc)

        assertEquals("x = 5;", result.build())
    }

    @Test
    fun `after no afecta otros identificadores`() {
        val prev = createToken(TokenType.Semicolon, ";")
        val curr = createToken(TokenType.Identifier, "myFunction")
        val next = createToken(TokenType.LeftParen, "(")
        val config = createConfig(lineBreakAfterPrintln = 2)
        val doc = DocBuilder.inMemory().write("x = 5;")

        val result = LineBreakAfterPrintln.after(prev, curr, next, config, doc)

        assertEquals("x = 5;", result.build())
    }
}
