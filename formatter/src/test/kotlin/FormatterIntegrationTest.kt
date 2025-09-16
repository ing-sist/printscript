import config.FormatterRuleImplementations
import config.FormatterStyleConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FormatterIntegrationTest {
    private fun createToken(
        type: TokenType,
        lexeme: String,
    ) = Token(type, lexeme, Location(1, 1, 1))

    private fun createDefaultConfig() =
        FormatterStyleConfig(
            lineBreakBeforePrintln = 1,
            lineBreakAfterSemicolon = true,
            spaceBeforeColon = true,
            spaceAfterColon = true,
            spaceAroundAssignment = true,
            spaceAroundOperators = true,
            indentation = 4,
            ifBraceBelowLine = false,
            inlineIfBraceIfStatement = false,
        )

    @Test
    fun `formato completo de asignacion simple`() {
        val tokens =
            listOf(
                createToken(TokenType.Keyword.VariableDeclaration, "let"),
                createToken(TokenType.Identifier, "x"),
                createToken(TokenType.Colon, ":"),
                createToken(TokenType.StringType, "string"),
                createToken(TokenType.Assignment, "="),
                createToken(TokenType.StringLiteral, "\"hello\""),
                createToken(TokenType.Semicolon, ";"),
            )

        val stream = MockTokenStream(tokens)
        val formatter = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)
        val config = createDefaultConfig()

        val result = formatter.format(stream, config, DocBuilder.inMemory())

        assertEquals("let x : string = \"hello\";", result.build())
    }

    @Test
    fun `formato de bloque con indentacion`() {
        val tokens =
            listOf(
                createToken(TokenType.Keyword.If, "if"),
                createToken(TokenType.LeftParen, "("),
                createToken(TokenType.BooleanLiteral, "true"),
                createToken(TokenType.RightParen, ")"),
                createToken(TokenType.LeftBrace, "{"),
                createToken(TokenType.Identifier, "println"),
                createToken(TokenType.LeftParen, "("),
                createToken(TokenType.StringLiteral, "\"test\""),
                createToken(TokenType.RightParen, ")"),
                createToken(TokenType.Semicolon, ";"),
                createToken(TokenType.RightBrace, "}"),
            )

        val stream = MockTokenStream(tokens)
        val formatter = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)
        val config = createDefaultConfig()

        val result = formatter.format(stream, config, DocBuilder.inMemory())

        val expected = "if (true){\n\n    println(\"test\");\n}"
        assertEquals(expected, result.build())
    }

    @Test
    fun `formato con operadores matematicos`() {
        val tokens =
            listOf(
                createToken(TokenType.Keyword.VariableDeclaration, "let"),
                createToken(TokenType.Identifier, "result"),
                createToken(TokenType.Assignment, "="),
                createToken(TokenType.Identifier, "x"),
                createToken(TokenType.Plus, "+"),
                createToken(TokenType.Identifier, "y"),
                createToken(TokenType.Multiply, "*"),
                createToken(TokenType.NumberLiteral, "2"),
                createToken(TokenType.Semicolon, ";"),
            )

        val stream = MockTokenStream(tokens)
        val formatter = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)
        val config = createDefaultConfig()

        val result = formatter.format(stream, config, DocBuilder.inMemory())

        assertEquals("let result = x + y * 2;", result.build())
    }

    @Test
    fun `formato sin espacios alrededor de operadores`() {
        val tokens =
            listOf(
                createToken(TokenType.Identifier, "x"),
                createToken(TokenType.Plus, "+"),
                createToken(TokenType.NumberLiteral, "5"),
            )

        val config = createDefaultConfig().copy(spaceAroundOperators = false)
        val stream = MockTokenStream(tokens)
        val formatter = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)

        val result = formatter.format(stream, config, DocBuilder.inMemory())

        assertEquals("x+5", result.build())
    }

    @Test
    fun `formato sin espacios alrededor de asignacion`() {
        val tokens =
            listOf(
                createToken(TokenType.Identifier, "x"),
                createToken(TokenType.Assignment, "="),
                createToken(TokenType.NumberLiteral, "5"),
            )

        val config = createDefaultConfig().copy(spaceAroundAssignment = false)
        val stream = MockTokenStream(tokens)
        val formatter = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)

        val result = formatter.format(stream, config, DocBuilder.inMemory())

        assertEquals("x=5", result.build())
    }

    @Test
    fun `formato sin salto de linea despues de semicolon`() {
        val tokens =
            listOf(
                createToken(TokenType.Identifier, "x"),
                createToken(TokenType.Assignment, "="),
                createToken(TokenType.NumberLiteral, "5"),
                createToken(TokenType.Semicolon, ";"),
                createToken(TokenType.Identifier, "y"),
                createToken(TokenType.Assignment, "="),
                createToken(TokenType.NumberLiteral, "10"),
            )

        val config = createDefaultConfig().copy(lineBreakAfterSemicolon = false)
        val stream = MockTokenStream(tokens)
        val formatter = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)

        val result = formatter.format(stream, config, DocBuilder.inMemory())

        assertEquals("x = 5;y = 10", result.build())
    }

    @Test
    fun `formato con multiples saltos de linea antes de println`() {
        val tokens =
            listOf(
                createToken(TokenType.Identifier, "x"),
                createToken(TokenType.Semicolon, ";"),
                createToken(TokenType.Identifier, "println"),
                createToken(TokenType.LeftParen, "("),
                createToken(TokenType.StringLiteral, "\"test\""),
                createToken(TokenType.RightParen, ")"),
            )

        val config = createDefaultConfig().copy(lineBreakBeforePrintln = 3)
        val stream = MockTokenStream(tokens)
        val formatter = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)

        val result = formatter.format(stream, config, DocBuilder.inMemory())

        assertEquals("x;\n\n\n\nprintln(\"test\")", result.build())
    }
}
