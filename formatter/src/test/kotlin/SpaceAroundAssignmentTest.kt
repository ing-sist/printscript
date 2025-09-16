import config.FormatterStyleConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import rules.implementations.SpaceAroundAssignment
import rules.implementations.SpaceForbid

class SpaceAroundAssignmentTest {
    private fun createToken(
        type: TokenType,
        lexeme: String,
    ) = Token(type, lexeme, Location(1, 1, 1))

    private fun createConfig(spaceAroundAssignment: Boolean) =
        FormatterStyleConfig(
            lineBreakBeforePrintln = 1,
            lineBreakAfterSemicolon = true,
            spaceBeforeColon = true,
            spaceAfterColon = true,
            spaceAroundAssignment = spaceAroundAssignment,
            spaceAroundOperators = true,
            indentation = 4,
            ifBraceBelowLine = false,
            inlineIfBraceIfStatement = true,
        )

    @Test
    fun `before agrega espacio antes de assignment cuando esta habilitado`() {
        val prev = createToken(TokenType.Identifier, "x")
        val curr = createToken(TokenType.Assignment, "=")
        val next = createToken(TokenType.NumberLiteral, "5")
        val config = createConfig(spaceAroundAssignment = true)
        val doc = DocBuilder.inMemory().write("x")
        val spaceForbid = SpaceForbid()

        val result = SpaceAroundAssignment.before(prev, curr, next, config, doc, spaceForbid)

        assertEquals("x ", result.build())
    }

    @Test
    fun `before no agrega espacio cuando esta deshabilitado`() {
        val prev = createToken(TokenType.Identifier, "x")
        val curr = createToken(TokenType.Assignment, "=")
        val next = createToken(TokenType.NumberLiteral, "5")
        val config = createConfig(spaceAroundAssignment = false)
        val doc = DocBuilder.inMemory().write("x")
        val spaceForbid = SpaceForbid()

        val result = SpaceAroundAssignment.before(prev, curr, next, config, doc, spaceForbid)

        assertEquals("x", result.build())
    }

    @Test
    fun `before no afecta tokens que no son assignment`() {
        val prev = createToken(TokenType.Identifier, "x")
        val curr = createToken(TokenType.Plus, "+")
        val next = createToken(TokenType.NumberLiteral, "5")
        val config = createConfig(spaceAroundAssignment = true)
        val doc = DocBuilder.inMemory().write("x")
        val spaceForbid = SpaceForbid()

        val result = SpaceAroundAssignment.before(prev, curr, next, config, doc, spaceForbid)

        assertEquals("x", result.build())
    }

    @Test
    fun `after agrega espacio despues de assignment cuando esta habilitado`() {
        val prev = createToken(TokenType.Identifier, "x")
        val curr = createToken(TokenType.Assignment, "=")
        val next = createToken(TokenType.NumberLiteral, "5")
        val config = createConfig(spaceAroundAssignment = true)
        val doc = DocBuilder.inMemory().write("x=")
        val spaceForbid = SpaceForbid()

        val result = SpaceAroundAssignment.after(prev, curr, next, config, doc, spaceForbid)

        assertEquals("x= ", result.build())
    }

    @Test
    fun `after no agrega espacio cuando esta deshabilitado`() {
        val prev = createToken(TokenType.Identifier, "x")
        val curr = createToken(TokenType.Assignment, "=")
        val next = createToken(TokenType.NumberLiteral, "5")
        val config = createConfig(spaceAroundAssignment = false)
        val doc = DocBuilder.inMemory().write("x=")
        val spaceForbid = SpaceForbid()

        val result = SpaceAroundAssignment.after(prev, curr, next, config, doc, spaceForbid)

        assertEquals("x=", result.build())
    }

    @Test
    fun `after no afecta tokens que no son assignment`() {
        val prev = createToken(TokenType.Identifier, "x")
        val curr = createToken(TokenType.Plus, "+")
        val next = createToken(TokenType.NumberLiteral, "5")
        val config = createConfig(spaceAroundAssignment = true)
        val doc = DocBuilder.inMemory().write("x+")
        val spaceForbid = SpaceForbid()

        val result = SpaceAroundAssignment.after(prev, curr, next, config, doc, spaceForbid)

        assertEquals("x+", result.build())
    }
}
