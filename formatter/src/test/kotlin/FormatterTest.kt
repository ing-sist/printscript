import config.FormatterStyleConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import rules.implementations.AfterRule
import rules.implementations.BeforeRule

class FormatterTest {
    private fun createToken(
        type: TokenType,
        lexeme: String,
    ) = Token(type, lexeme, Location(1, 1, 1))

    private fun createConfig(
        indentation: Int = 4,
        spaceAroundAssignment: Boolean = true,
        lineBreakAfterSemicolon: Boolean = true,
    ) = FormatterStyleConfig(
        lineBreakAfterPrintln = 1,
        lineBreakAfterSemicolon = lineBreakAfterSemicolon,
        spaceBeforeColon = true,
        spaceAfterColon = true,
        spaceAroundAssignment = spaceAroundAssignment,
        spaceAroundOperators = true,
        indentation = indentation,
        inlineIfBraceIfStatement = true,
    )

    // Mock rule para testing
    private class MockBeforeRule(
        private val action: (
            Token,
            Token,
            Token,
            FormatterStyleConfig,
            DocBuilder,
        ) -> DocBuilder,
    ) : BeforeRule {
        override fun before(
            prev: Token,
            curr: Token,
            next: Token,
            style: FormatterStyleConfig,
            out: DocBuilder,
        ): DocBuilder = action(prev, curr, next, style, out)
    }

    private class MockAfterRule(
        private val action: (
            Token,
            Token,
            Token,
            FormatterStyleConfig,
            DocBuilder,
        ) -> DocBuilder,
    ) : AfterRule {
        override fun after(
            prev: Token,
            curr: Token,
            next: Token,
            style: FormatterStyleConfig,
            out: DocBuilder,
        ): DocBuilder = action(prev, curr, next, style, out)
    }

    @Test
    fun `format token unico`() {
        val tokens = listOf(createToken(TokenType.Identifier, "x"))
        val stream = MockTokenStream(tokens)
        val formatter = Formatter(emptyList())
        val config = createConfig()

        val result = formatter.format(stream, config, DocBuilder.inMemory())

        assertEquals("x", result.build())
    }

    @Test
    fun `format con indentacion basica`() {
        val tokens =
            listOf(
                createToken(TokenType.LeftBrace, "{"),
                createToken(TokenType.Identifier, "x"),
                createToken(TokenType.RightBrace, "}"),
            )
        val stream = MockTokenStream(tokens)
        val formatter = Formatter(emptyList())
        val config = createConfig(indentation = 2)

        val result = formatter.format(stream, config, DocBuilder.inMemory())

        assertEquals("{x}", result.build())
    }

    @Test
    fun `format aplica reglas before`() {
        val beforeRule =
            MockBeforeRule { _, curr, _, _, out ->
                if (curr.type is TokenType.Assignment) out.write("BEFORE") else out
            }

        val tokens =
            listOf(
                createToken(TokenType.Identifier, "x"),
                createToken(TokenType.Assignment, "="),
                createToken(TokenType.NumberLiteral, "5"),
            )
        val stream = MockTokenStream(tokens)
        val formatter = Formatter(listOf(beforeRule))
        val config = createConfig()

        val result = formatter.format(stream, config, DocBuilder.inMemory())

        assertEquals("xBEFORE=5", result.build())
    }

    @Test
    fun `format aplica reglas after`() {
        val afterRule =
            MockAfterRule { _, curr, _, _, out ->
                if (curr.type is TokenType.Assignment) out.write("AFTER") else out
            }

        val tokens =
            listOf(
                createToken(TokenType.Identifier, "x"),
                createToken(TokenType.Assignment, "="),
                createToken(TokenType.NumberLiteral, "5"),
            )
        val stream = MockTokenStream(tokens)
        val formatter = Formatter(listOf(afterRule))
        val config = createConfig()

        val result = formatter.format(stream, config, DocBuilder.inMemory())

        assertEquals("x=AFTER5", result.build())
    }

    @Test
    fun `multiples niveles de braces`() {
        val tokens =
            listOf(
                createToken(TokenType.LeftBrace, "{"),
                createToken(TokenType.LeftBrace, "{"),
                createToken(TokenType.Identifier, "x"),
                createToken(TokenType.RightBrace, "}"),
                createToken(TokenType.RightBrace, "}"),
            )
        val stream = MockTokenStream(tokens)
        val formatter = Formatter(emptyList())
        val config = createConfig(indentation = 2)

        val result = formatter.format(stream, config, DocBuilder.inMemory())
        // Sin reglas adicionales, no hay saltos de línea, por lo que no se aplica indentación
        assertEquals("{{x}}", result.build())
    }

    @Test
    fun `reglas multiple en orden`() {
        val beforeRule1 =
            MockBeforeRule { _, curr, _, _, out ->
                if (curr.type is TokenType.Assignment) out.write("B1") else out
            }
        val beforeRule2 =
            MockBeforeRule { _, curr, _, _, out ->
                if (curr.type is TokenType.Assignment) out.write("B2") else out
            }

        val tokens =
            listOf(
                createToken(TokenType.Identifier, "x"),
                createToken(TokenType.Assignment, "="),
            )
        val stream = MockTokenStream(tokens)
        val formatter = Formatter(listOf(beforeRule1, beforeRule2))
        val config = createConfig()

        val result = formatter.format(stream, config, DocBuilder.inMemory())

        // Primera regla que cambie el DocBuilder gana
        assertEquals("xB1=", result.build())
    }
}
