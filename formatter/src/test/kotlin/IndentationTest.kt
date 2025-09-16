import config.FormatterStyleConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IndentationTest {
    private fun createToken(
        type: TokenType,
        lexeme: String,
    ) = Token(type, lexeme, Location(1, 1, 1))

    private fun createConfig(indentation: Int) =
        FormatterStyleConfig(
            lineBreakAfterPrintln = 1,
            lineBreakAfterSemicolon = true,
            spaceBeforeColon = true,
            spaceAfterColon = true,
            spaceAroundAssignment = true,
            spaceAroundOperators = true,
            indentation = indentation,
            inlineIfBraceIfStatement = true,
        )

    @Test
    fun `before agrega newline antes de RightBrace cuando prev no es Semicolon`() {
        val prev = createToken(TokenType.Identifier, "x")
        val curr = createToken(TokenType.RightBrace, "}")
        val next = createToken(TokenType.EOF, "")
        val config = createConfig(indentation = 4)
        val doc = DocBuilder.inMemory().write("x")

        val result = Indentation.before(prev, curr, next, config, doc)

        assertEquals("x\n", result.build())
    }

    @Test
    fun `before no agrega newline cuando prev es Semicolon`() {
        val prev = createToken(TokenType.Semicolon, ";")
        val curr = createToken(TokenType.RightBrace, "}")
        val next = createToken(TokenType.EOF, "")
        val config = createConfig(indentation = 4)
        val doc = DocBuilder.inMemory().write("x;")

        val result = Indentation.before(prev, curr, next, config, doc)

        assertEquals("x;", result.build())
    }

    @Test
    fun `before no hace nada cuando indentation es cero`() {
        val prev = createToken(TokenType.Identifier, "x")
        val curr = createToken(TokenType.RightBrace, "}")
        val next = createToken(TokenType.EOF, "")
        val config = createConfig(indentation = 0)
        val doc = DocBuilder.inMemory().write("x")

        val result = Indentation.before(prev, curr, next, config, doc)

        assertEquals("x", result.build())
    }

    @Test
    fun `before no afecta tokens que no son RightBrace`() {
        val prev = createToken(TokenType.Identifier, "x")
        val curr = createToken(TokenType.Plus, "+")
        val next = createToken(TokenType.NumberLiteral, "5")
        val config = createConfig(indentation = 4)
        val doc = DocBuilder.inMemory().write("x")

        val result = Indentation.before(prev, curr, next, config, doc)

        assertEquals("x", result.build())
    }

    @Test
    fun `after agrega newline despues de LeftBrace cuando no esta al inicio de linea`() {
        val prev = createToken(TokenType.Keyword.If, "if")
        val curr = createToken(TokenType.LeftBrace, "{")
        val next = createToken(TokenType.Identifier, "x")
        val config = createConfig(indentation = 4)
        val doc = DocBuilder.inMemory().write("if {")

        val result = Indentation.after(prev, curr, next, config, doc)

        assertEquals("if {\n", result.build())
    }

    @Test
    fun `after no hace nada cuando indentation es cero`() {
        val prev = createToken(TokenType.Keyword.If, "if")
        val curr = createToken(TokenType.LeftBrace, "{")
        val next = createToken(TokenType.Identifier, "x")
        val config = createConfig(indentation = 0)
        val doc = DocBuilder.inMemory().write("if {")

        val result = Indentation.after(prev, curr, next, config, doc)

        assertEquals("if {", result.build())
    }

    @Test
    fun `after no afecta tokens que no son LeftBrace`() {
        val prev = createToken(TokenType.Keyword.If, "if")
        val curr = createToken(TokenType.LeftParen, "(")
        val next = createToken(TokenType.Identifier, "x")
        val config = createConfig(indentation = 4)
        val doc = DocBuilder.inMemory().write("if (")

        val result = Indentation.after(prev, curr, next, config, doc)

        assertEquals("if (", result.build())
    }
}
