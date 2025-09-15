import config.FormatterStyleConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import rules.implementations.LineBreakBeforePrintln

class LineBreakBeforePrintlnTest {
    private fun createToken(
        type: TokenType,
        lexeme: String,
    ) = Token(type, lexeme, Location(1, 1, 1))

    private fun createConfig(lineBreakBeforePrintln: Int) =
        FormatterStyleConfig(
            lineBreakBeforePrintln = lineBreakBeforePrintln,
            lineBreakAfterSemicolon = true,
            spaceBeforeColon = true,
            spaceAfterColon = true,
            spaceAroundAssignment = true,
            spaceAroundOperators = true,
            indentation = 4,
            inlineIfBraceIfStatement = true,
        )

    @Test
    fun `before agrega un salto de linea antes de println`() {
        val prev = createToken(TokenType.Semicolon, ";")
        val curr = createToken(TokenType.Identifier, "println")
        val next = createToken(TokenType.LeftParen, "(")
        val config = createConfig(lineBreakBeforePrintln = 1)
        val doc = DocBuilder.inMemory().write("x = 5;")

        val result = LineBreakBeforePrintln.before(prev, curr, next, config, doc)

        assertEquals("x = 5;\n", result.build())
    }

    @Test
    fun `before agrega multiples saltos de linea antes de println`() {
        val prev = createToken(TokenType.Semicolon, ";")
        val curr = createToken(TokenType.Identifier, "println")
        val next = createToken(TokenType.LeftParen, "(")
        val config = createConfig(lineBreakBeforePrintln = 3)
        val doc = DocBuilder.inMemory().write("x = 5;")

        val result = LineBreakBeforePrintln.before(prev, curr, next, config, doc)

        assertEquals("x = 5;\n\n\n", result.build())
    }

    @Test
    fun `before no agrega saltos cuando esta deshabilitado`() {
        val prev = createToken(TokenType.Semicolon, ";")
        val curr = createToken(TokenType.Identifier, "println")
        val next = createToken(TokenType.LeftParen, "(")
        val config = createConfig(lineBreakBeforePrintln = 0)
        val doc = DocBuilder.inMemory().write("x = 5;")

        val result = LineBreakBeforePrintln.before(prev, curr, next, config, doc)

        assertEquals("x = 5;", result.build())
    }

    @Test
    fun `before no afecta otros identificadores`() {
        val prev = createToken(TokenType.Semicolon, ";")
        val curr = createToken(TokenType.Identifier, "myFunction")
        val next = createToken(TokenType.LeftParen, "(")
        val config = createConfig(lineBreakBeforePrintln = 2)
        val doc = DocBuilder.inMemory().write("x = 5;")

        val result = LineBreakBeforePrintln.before(prev, curr, next, config, doc)

        assertEquals("x = 5;", result.build())
    }
}
