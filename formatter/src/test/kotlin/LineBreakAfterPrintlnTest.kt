import config.FormatterStyleConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import rules.implementations.LineBreakBeforePrintln

class LineBreakAfterPrintlnTest {
    private fun createToken(
        type: TokenType,
        lexeme: String,
    ) = Token(type, lexeme, Location(1, 1, 1))

    private fun createConfig(lineBreakAfterPrintln: Int) =
        FormatterStyleConfig(
            lineBreakBeforePrintln = lineBreakAfterPrintln,
            lineBreakAfterSemicolon = true,
            spaceBeforeColon = true,
            spaceAfterColon = true,
            spaceAroundAssignment = true,
            spaceAroundOperators = true,
            indentation = 4,
            ifBraceBelowLine = false,
            inlineIfBraceIfStatement = true,
        )

    @Test
    fun `after no agrega saltos cuando esta deshabilitado`() {
        val prev = createToken(TokenType.Semicolon, ";")
        val curr = createToken(TokenType.Identifier, "println")
        val next = createToken(TokenType.LeftParen, "(")
        val config = createConfig(lineBreakAfterPrintln = 0)
        val doc = DocBuilder.inMemory().write("x = 5;")

        val result = LineBreakBeforePrintln.before(prev, curr, next, config, doc)

        assertEquals("x = 5;", result.build())
    }

    @Test
    fun `after no afecta otros identificadores`() {
        val prev = createToken(TokenType.Semicolon, ";")
        val curr = createToken(TokenType.Identifier, "myFunction")
        val next = createToken(TokenType.LeftParen, "(")
        val config = createConfig(lineBreakAfterPrintln = 2)
        val doc = DocBuilder.inMemory().write("x = 5;")

        val result = LineBreakBeforePrintln.before(prev, curr, next, config, doc)

        assertEquals("x = 5;", result.build())
    }
}
