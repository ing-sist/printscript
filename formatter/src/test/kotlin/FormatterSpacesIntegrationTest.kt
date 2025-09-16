import config.FormatterRuleImplementations
import config.FormatterStyleConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FormatterSpacesIntegrationTest {
    fun t(
        type: TokenType,
        lex: String,
    ) = Token(type, lex, Location(1, 1, 1))

    fun defaultStyle() =
        FormatterStyleConfig(
            lineBreakBeforePrintln = 1,
            lineBreakAfterSemicolon = true,
            spaceBeforeColon = true,
            spaceAfterColon = true,
            spaceAroundAssignment = true,
            spaceAroundOperators = true,
            indentation = 4,
            inlineIfBraceIfStatement = true,
            ifBraceBelowLine = false,
        )

    // 2) Quita espacios si el estilo NO los requiere antes de ':' (spaceBeforeColon=false)
    @Test
    fun `remueve espacio antes de colon si estilo no lo requiere`() {
        val tokens =
            listOf(
                t(TokenType.Identifier, "x"),
                t(TokenType.Space, " "),
                t(TokenType.Colon, ":"),
                t(TokenType.StringType, "string"),
            )
        val stream = MockTokenStream(tokens)
        val fmt = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)
        val style = defaultStyle().copy(spaceBeforeColon = false, spaceAfterColon = true)

        val out = fmt.format(stream, style, DocBuilder.inMemory())
        assertEquals("x : string", out.build())
    }

    // 4) No duplica espacio si el input trae espacio y además las reglas lo agregan alrededor de '='
    @Test
    fun `no duplica espacio alrededor de assignment cuando input ya trae espacio`() {
        val tokens =
            listOf(
                t(TokenType.Identifier, "x"),
                t(TokenType.Space, " "),
                t(TokenType.Assignment, "="),
                t(TokenType.Space, " "),
                t(TokenType.Identifier, "y"),
            )
        val stream = MockTokenStream(tokens)
        val fmt = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)
        val style = defaultStyle().copy(spaceAroundAssignment = true)

        val out = fmt.format(stream, style, DocBuilder.inMemory())
        assertEquals("x = y", out.build())
    }

    // 5) Quita espacio si el estilo NO lo requiere alrededor de '='
    @Test
    fun `remueve espacio alrededor de assignment si estilo no lo requiere`() {
        val tokens =
            listOf(
                t(TokenType.Identifier, "x"),
                t(TokenType.Space, " "),
                t(TokenType.Assignment, "="),
                t(TokenType.Space, " "),
                t(TokenType.NumberLiteral, "5"),
            )
        val stream = MockTokenStream(tokens)
        val fmt = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)
        val style = defaultStyle().copy(spaceAroundAssignment = false)

        val out = fmt.format(stream, style, DocBuilder.inMemory())
        assertEquals("x = 5", out.build())
    }

    // 6) Operadores: colapsa a uno si el estilo lo requiere
    @Test
    fun `operadores conservan un espacio si estilo lo requiere`() {
        val tokens =
            listOf(
                t(TokenType.Identifier, "a"),
                t(TokenType.Space, " "),
                t(TokenType.Plus, "+"),
                t(TokenType.Space, " "),
                t(TokenType.Identifier, "b"),
                t(TokenType.Space, " "),
                t(TokenType.Multiply, "*"),
                t(TokenType.Space, " "),
                t(TokenType.NumberLiteral, "2"),
            )
        val stream = MockTokenStream(tokens)
        val fmt = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)
        val style = defaultStyle().copy(spaceAroundOperators = true)

        val out = fmt.format(stream, style, DocBuilder.inMemory())
        assertEquals("a + b * 2", out.build())
    }

    // 7) Operadores: elimina espacios si el estilo no lo requiere
    @Test
    fun `operadores sin espacios si estilo no lo requiere`() {
        val tokens =
            listOf(
                t(TokenType.Identifier, "a"),
                t(TokenType.Space, " "),
                t(TokenType.Plus, "+"),
                t(TokenType.Space, " "),
                t(TokenType.Identifier, "b"),
            )
        val stream = MockTokenStream(tokens)
        val fmt = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)
        val style = defaultStyle().copy(spaceAroundOperators = false)

        val out = fmt.format(stream, style, DocBuilder.inMemory())
        assertEquals("a + b", out.build())
    }
}
