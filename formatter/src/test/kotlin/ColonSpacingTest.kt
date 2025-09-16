import config.FormatterStyleConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import rules.implementations.ColonSpacing

class ColonSpacingTest {
    private fun createToken(
        type: TokenType,
        lexeme: String,
    ) = Token(type, lexeme, Location(1, 1, 1))

    private fun createConfig(
        spaceBeforeColon: Boolean,
        spaceAfterColon: Boolean,
    ) = FormatterStyleConfig(
        lineBreakBeforePrintln = 1,
        lineBreakAfterSemicolon = true,
        spaceBeforeColon = spaceBeforeColon,
        spaceAfterColon = spaceAfterColon,
        spaceAroundAssignment = true,
        spaceAroundOperators = true,
        indentation = 4,
        inlineIfBraceIfStatement = true,
    )

    @Test
    fun `before agrega espacio antes de colon cuando esta habilitado`() {
        val prev = createToken(TokenType.Identifier, "x")
        val curr = createToken(TokenType.Colon, ":")
        val next = createToken(TokenType.StringType, "string")
        val config = createConfig(spaceBeforeColon = true, spaceAfterColon = false)
        val doc = DocBuilder.inMemory().write("x")

        val result = ColonSpacing.before(prev, curr, next, config, doc)

        assertEquals("x ", result.build())
    }

    @Test
    fun `before no agrega espacio cuando esta deshabilitado`() {
        val prev = createToken(TokenType.Identifier, "x")
        val curr = createToken(TokenType.Colon, ":")
        val next = createToken(TokenType.StringType, "string")
        val config = createConfig(spaceBeforeColon = false, spaceAfterColon = false)
        val doc = DocBuilder.inMemory().write("x")

        val result = ColonSpacing.before(prev, curr, next, config, doc)

        assertEquals("x", result.build())
    }

    @Test
    fun `after agrega espacio despues de colon cuando esta habilitado`() {
        val prev = createToken(TokenType.Identifier, "x")
        val curr = createToken(TokenType.Colon, ":")
        val next = createToken(TokenType.StringType, "string")
        val config = createConfig(spaceBeforeColon = false, spaceAfterColon = true)
        val doc = DocBuilder.inMemory().write("x:")

        val result = ColonSpacing.after(prev, curr, next, config, doc)

        assertEquals("x: ", result.build())
    }

    @Test
    fun `after no agrega espacio cuando esta deshabilitado`() {
        val prev = createToken(TokenType.Identifier, "x")
        val curr = createToken(TokenType.Colon, ":")
        val next = createToken(TokenType.StringType, "string")
        val config = createConfig(spaceBeforeColon = false, spaceAfterColon = false)
        val doc = DocBuilder.inMemory().write("x:")

        val result = ColonSpacing.after(prev, curr, next, config, doc)

        assertEquals("x:", result.build())
    }

    @Test
    fun `ambos habilitados agregan espacios antes y despues`() {
        val prev = createToken(TokenType.Identifier, "x")
        val curr = createToken(TokenType.Colon, ":")
        val next = createToken(TokenType.StringType, "string")
        val config = createConfig(spaceBeforeColon = true, spaceAfterColon = true)

        val doc1 = DocBuilder.inMemory().write("x")
        val result1 = ColonSpacing.before(prev, curr, next, config, doc1)

        val doc2 = result1.write(":")
        val result2 = ColonSpacing.after(prev, curr, next, config, doc2)

        assertEquals("x : ", result2.build())
    }

    @Test
    fun `no afecta tokens que no son colon`() {
        val prev = createToken(TokenType.Identifier, "x")
        val curr = createToken(TokenType.Assignment, "=")
        val next = createToken(TokenType.NumberLiteral, "5")
        val config = createConfig(spaceBeforeColon = true, spaceAfterColon = true)
        val doc = DocBuilder.inMemory().write("x")

        val beforeResult = ColonSpacing.before(prev, curr, next, config, doc)
        val afterResult = ColonSpacing.after(prev, curr, next, config, doc)

        assertEquals("x", beforeResult.build())
        assertEquals("x", afterResult.build())
    }
}
