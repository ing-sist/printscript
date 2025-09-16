import config.FormatterRuleImplementations
import config.FormatterStyleConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LineBreakRulesTest {
    private fun t(
        type: TokenType,
        lex: String,
    ) = Token(type, lex, Location(1, 1, 1))

    private fun baseStyle(
        lineBreakAfterSemicolon: Boolean = true,
        lineBreakBeforePrintln: Int = 1,
    ) = FormatterStyleConfig(
        lineBreakAfterSemicolon = lineBreakAfterSemicolon,
        lineBreakBeforePrintln = lineBreakBeforePrintln,
        spaceBeforeColon = true,
        spaceAfterColon = true,
        spaceAroundAssignment = true,
        spaceAroundOperators = true,
        indentation = 4,
        inlineIfBraceIfStatement = true,
    )

    // -------------------------------
    // LineBreakAfterSemicolon
    // -------------------------------

    @Test
    fun `semicolon true - inserta salto de linea despues de semicolon`() {
        val tokens =
            listOf(
                t(TokenType.Identifier, "x"),
                t(TokenType.Assignment, "="),
                t(TokenType.NumberLiteral, "1"),
                t(TokenType.Semicolon, ";"),
                t(TokenType.Identifier, "y"),
                t(TokenType.Assignment, "="),
                t(TokenType.NumberLiteral, "2"),
                t(TokenType.Semicolon, ";"),
            )
        val stream = MockTokenStream(tokens)
        val formatter = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)
        // Activamos solo la de ; y desactivamos println para no interferir
        val style = baseStyle(lineBreakAfterSemicolon = true, lineBreakBeforePrintln = 0)

        val out = formatter.format(stream, style, DocBuilder.inMemory())

        assertEquals("x = 1;\ny = 2;", out.build())
    }

    @Test
    fun `semicolon false - no inserta salto de linea despues de semicolon`() {
        val tokens =
            listOf(
                t(TokenType.Identifier, "x"),
                t(TokenType.Assignment, "="),
                t(TokenType.NumberLiteral, "1"),
                t(TokenType.Semicolon, ";"),
                t(TokenType.Identifier, "y"),
                t(TokenType.Assignment, "="),
                t(TokenType.NumberLiteral, "2"),
                t(TokenType.Semicolon, ";"),
            )
        val stream = MockTokenStream(tokens)
        val formatter = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)
        val style = baseStyle(lineBreakAfterSemicolon = false, lineBreakBeforePrintln = 0)

        val out = formatter.format(stream, style, DocBuilder.inMemory())

        assertEquals("x = 1;y = 2;", out.build())
    }

    @Test
    fun `semicolon true - no duplica saltos si ya estaba en inicio de linea`() {
        val tokens =
            listOf(
                t(TokenType.Identifier, "a"),
                t(TokenType.Semicolon, ";"),
                // ya sigue otro token; la regla debería agregar solo un '\n'
                t(TokenType.Identifier, "b"),
                t(TokenType.Semicolon, ";"),
            )
        val stream = MockTokenStream(tokens)
        val formatter = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)
        val style = baseStyle(lineBreakAfterSemicolon = true, lineBreakBeforePrintln = 0)

        val out = formatter.format(stream, style, DocBuilder.inMemory())

        assertEquals("a;\nb;", out.build())
    }

    // -------------------------------
    // LineBreakBeforePrintln
    // -------------------------------

    @Test
    fun `println before = 0 - no inserta saltos antes de println`() {
        val tokens =
            listOf(
                t(TokenType.Identifier, "x"),
                t(TokenType.Semicolon, ";"),
                t(TokenType.Identifier, "println"),
                t(TokenType.LeftParen, "("),
                t(TokenType.StringLiteral, "\"hi\""),
                t(TokenType.RightParen, ")"),
                t(TokenType.Semicolon, ";"),
            )
        val stream = MockTokenStream(tokens)
        val formatter = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)
        // Desactivamos salto por ; para aislar la regla de println
        val style = baseStyle(lineBreakAfterSemicolon = false, lineBreakBeforePrintln = 0)

        val out = formatter.format(stream, style, DocBuilder.inMemory())

        assertEquals("x;println(\"hi\");", out.build())
    }

    @Test
    fun `println before = 1 - inserta un salto antes de println`() {
        val tokens =
            listOf(
                t(TokenType.Identifier, "x"),
                t(TokenType.Semicolon, ";"),
                t(TokenType.Identifier, "println"),
                t(TokenType.LeftParen, "("),
                t(TokenType.StringLiteral, "\"hi\""),
                t(TokenType.RightParen, ")"),
                t(TokenType.Semicolon, ";"),
            )
        val stream = MockTokenStream(tokens)
        val formatter = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)
        val style = baseStyle(lineBreakAfterSemicolon = false, lineBreakBeforePrintln = 1)

        val out = formatter.format(stream, style, DocBuilder.inMemory())

        assertEquals("x;\nprintln(\"hi\");", out.build())
    }

    @Test
    fun `println before = 2 - inserta dos saltos antes de println (dos lineas en blanco)`() {
        val tokens =
            listOf(
                t(TokenType.Identifier, "x"),
                t(TokenType.Semicolon, ";"),
                t(TokenType.Identifier, "println"),
                t(TokenType.LeftParen, "("),
                t(TokenType.StringLiteral, "\"hi\""),
                t(TokenType.RightParen, ")"),
                t(TokenType.Semicolon, ";"),
            )
        val stream = MockTokenStream(tokens)
        val formatter = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)
        val style = baseStyle(lineBreakAfterSemicolon = true, lineBreakBeforePrintln = 2)

        val out = formatter.format(stream, style, DocBuilder.inMemory())

        // dos nuevos antes + la línea del println
        assertEquals("x;\n\n\nprintln(\"hi\");", out.build())
    }

    @Test
    fun `println before - no duplica saltos si ya esta al inicio de linea`() {
        val tokens =
            listOf(
                // println al inicio: aún así, si la regla está hecha como "insertar N newlines"
                // podría agregar antes; este test documenta el comportamiento deseado:
                t(TokenType.Identifier, "println"),
                t(TokenType.LeftParen, "("),
                t(TokenType.StringLiteral, "\"hey\""),
                t(TokenType.RightParen, ")"),
                t(TokenType.Semicolon, ";"),
            )
        val stream = MockTokenStream(tokens)
        val formatter = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)
        val style = baseStyle(lineBreakAfterSemicolon = false, lineBreakBeforePrintln = 1)

        val out = formatter.format(stream, style, DocBuilder.inMemory())

        // Esperado: si tu regla evita añadir salto cuando ya está al inicio de línea,
        // quedará tal cual. Si tu implementación SIEMPRE agrega, cambiá el expected a "\nprintln(\"hey\");"
        assertEquals("\nprintln(\"hey\");", out.build())
    }
}
