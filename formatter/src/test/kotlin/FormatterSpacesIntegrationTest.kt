import config.FormatterRuleImplementations
import config.FormatterStyleConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FormatterSpacesIntegrationTest {
    private fun t(
        type: TokenType,
        lex: String,
    ) = Token(type, lex, Location(1, 1, 1))

    private fun id(name: String) = t(TokenType.Identifier, name)

    private fun num(n: String) = t(TokenType.NumberLiteral, n)

    private fun strType() = t(TokenType.StringType, "string")

    private fun plus() = t(TokenType.Plus, "+")

    private fun mul() = t(TokenType.Multiply, "*")

    private fun eq() = t(TokenType.Assignment, "=")

    private fun colon() = t(TokenType.Colon, ":")

    private fun sp() = t(TokenType.Space, " ")

    private fun defaultStyle() =
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

    /** Formatea una lista de tokens con un estilo, devolviendo el string final. */
    private fun format(
        style: FormatterStyleConfig = defaultStyle(),
        vararg tokens: Token,
    ): String {
        val stream = MockTokenStream(tokens.toList())
        val fmt = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)
        val out = fmt.format(stream, style, DocBuilder.inMemory())
        return out.build()
    }

    @Nested
    inner class ColonSpaces {
        // 2) Quita espacios si el estilo NO los requiere antes de ':'
        @Test
        fun `remueve espacio antes de colon si estilo no lo requiere`() {
            val style = defaultStyle().copy(spaceBeforeColon = false, spaceAfterColon = true)
            val actual = format(style, id("x"), sp(), colon(), strType())
            assertEquals("x: string", actual)
        }

        // sanity: cuando sí lo requiere antes y después
        @Test
        fun `mantiene espacios a ambos lados de colon cuando estilo lo requiere`() {
            val style = defaultStyle().copy(spaceBeforeColon = true, spaceAfterColon = true)
            val actual = format(style, id("x"), sp(), colon(), strType())
            // El formatter puede normalizar múltiples espacios a uno
            assertEquals("x : string", actual)
        }

        // solo después del colon
        @Test
        fun `sin espacio antes y con espacio despues cuando estilo lo indica`() {
            val style = defaultStyle().copy(spaceBeforeColon = false, spaceAfterColon = true)
            val actual = format(style, id("x"), colon(), strType())
            assertEquals("x: string", actual)
        }
    }

    @Nested
    inner class AssignmentSpaces {
        // 5) Quita espacio si el estilo NO lo requiere alrededor de '='
        @Test
        fun `remueve espacio alrededor de assignment si estilo no lo requiere`() {
            val style = defaultStyle().copy(spaceAroundAssignment = false)
            val actual = format(style, id("x"), sp(), eq(), sp(), num("5"))
            assertEquals("x=5", actual)
        }

        // sanity: agrega espacios si faltan y el estilo los requiere
        @Test
        fun `agrega espacios alrededor de assignment si faltan y estilo los requiere`() {
            val style = defaultStyle().copy(spaceAroundAssignment = true)
            val actual = format(style, id("x"), eq(), num("5"))
            assertEquals("x = 5", actual)
        }
    }

    @Nested
    inner class OperatorSpaces {
        // 6) Operadores: conserva un espacio (colapsa a uno) si el estilo lo requiere
        @Test
        fun `operadores conservan un espacio si estilo lo requiere`() {
            val style = defaultStyle().copy(spaceAroundOperators = true)
            val actual =
                format(
                    style,
                    id("a"),
                    sp(),
                    plus(),
                    sp(),
                    id("b"),
                    sp(),
                    mul(),
                    sp(),
                    num("2"),
                )
            assertEquals("a + b * 2", actual)
        }

        // 7) Operadores: elimina espacios si el estilo no lo requiere
        @Test
        fun `operadores sin espacios si estilo no lo requiere`() {
            val style = defaultStyle().copy(spaceAroundOperators = false)
            val actual = format(style, id("a"), sp(), plus(), sp(), id("b"))
            assertEquals("a+b", actual)
        }

        // sanity: agrega si faltan y el estilo los requiere
        @Test
        fun `operadores agregan espacio si faltan y estilo lo requiere`() {
            val style = defaultStyle().copy(spaceAroundOperators = true)
            val actual = format(style, id("a"), plus(), id("b"))
            assertEquals("a + b", actual)
        }
    }
}
