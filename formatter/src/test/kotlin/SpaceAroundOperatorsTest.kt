import config.FormatterStyleConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import rules.implementations.SpaceAroundOperators

class SpaceAroundOperatorsTest {
    private fun createToken(
        type: TokenType,
        lexeme: String,
    ) = Token(type, lexeme, Location(1, 1, 1))

    private fun createConfig(spaceAroundOperators: Boolean) =
        FormatterStyleConfig(
            lineBreakAfterPrintln = 1,
            lineBreakAfterSemicolon = true,
            spaceBeforeColon = true,
            spaceAfterColon = true,
            spaceAroundAssignment = true,
            spaceAroundOperators = spaceAroundOperators,
            indentation = 4,
            inlineIfBraceIfStatement = true,
        )

    @Test
    fun `before agrega espacio antes de operador Plus cuando esta habilitado`() {
        val prev = createToken(TokenType.Identifier, "x")
        val curr = createToken(TokenType.Plus, "+")
        val next = createToken(TokenType.NumberLiteral, "5")
        val config = createConfig(spaceAroundOperators = true)
        val doc = DocBuilder.inMemory().write("x")

        val result = SpaceAroundOperators.before(prev, curr, next, config, doc)

        assertEquals("x ", result.build())
    }

    @Test
    fun `after agrega espacio despues de operador Plus cuando esta habilitado`() {
        val prev = createToken(TokenType.Identifier, "x")
        val curr = createToken(TokenType.Plus, "+")
        val next = createToken(TokenType.NumberLiteral, "5")
        val config = createConfig(spaceAroundOperators = true)
        val doc = DocBuilder.inMemory().write("x +")

        val result = SpaceAroundOperators.after(prev, curr, next, config, doc)

        assertEquals("x + ", result.build())
    }

    @Test
    fun `no agrega espacios cuando esta deshabilitado`() {
        val prev = createToken(TokenType.Identifier, "x")
        val curr = createToken(TokenType.Plus, "+")
        val next = createToken(TokenType.NumberLiteral, "5")
        val config = createConfig(spaceAroundOperators = false)
        val doc = DocBuilder.inMemory().write("x")

        val beforeResult = SpaceAroundOperators.before(prev, curr, next, config, doc)
        val afterResult = SpaceAroundOperators.after(prev, curr, next, config, doc)

        assertEquals("x", beforeResult.build())
        assertEquals("x", afterResult.build())
    }

    @Test
    fun `funciona con todos los operadores aritmeticos`() {
        val operators =
            listOf(
                TokenType.Plus to "+",
                TokenType.Minus to "-",
                TokenType.Multiply to "*",
                TokenType.Divide to "/",
            )
        val config = createConfig(spaceAroundOperators = true)

        operators.forEach { (type, lexeme) ->
            val prev = createToken(TokenType.Identifier, "x")
            val curr = createToken(type, lexeme)
            val next = createToken(TokenType.NumberLiteral, "5")
            val doc = DocBuilder.inMemory().write("x")

            val result = SpaceAroundOperators.before(prev, curr, next, config, doc)
            assertEquals("x ", result.build(), "Failed for operator: $lexeme")
        }
    }

    @Test
    fun `funciona con operadores de comparacion`() {
        val operators =
            listOf(
                TokenType.Equals to "==",
                TokenType.NotEquals to "!=",
                TokenType.LessThan to "<",
                TokenType.LessThanOrEqual to "<=",
                TokenType.GreaterThan to ">",
                TokenType.GreaterThanOrEqual to ">=",
            )
        val config = createConfig(spaceAroundOperators = true)

        operators.forEach { (type, lexeme) ->
            val prev = createToken(TokenType.Identifier, "x")
            val curr = createToken(type, lexeme)
            val next = createToken(TokenType.NumberLiteral, "5")
            val doc = DocBuilder.inMemory().write("x")

            val result = SpaceAroundOperators.before(prev, curr, next, config, doc)
            assertEquals("x ", result.build(), "Failed for operator: $lexeme")
        }
    }

    @Test
    fun `no afecta tokens que no son operadores`() {
        val prev = createToken(TokenType.Identifier, "x")
        val curr = createToken(TokenType.Assignment, "=")
        val next = createToken(TokenType.NumberLiteral, "5")
        val config = createConfig(spaceAroundOperators = true)
        val doc = DocBuilder.inMemory().write("x")

        val beforeResult = SpaceAroundOperators.before(prev, curr, next, config, doc)
        val afterResult = SpaceAroundOperators.after(prev, curr, next, config, doc)

        assertEquals("x", beforeResult.build())
        assertEquals("x", afterResult.build())
    }
}
