import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import kotlin.test.DefaultAsserter.fail
import kotlin.test.Test
import kotlin.test.assertEquals

class LexerTest {
    private lateinit var lexer: Lexer

    @BeforeEach
    fun setup() {
        val lexerResult = LexerGenerator.createLexer("1.0")
        lexerResult.fold(
            { lexer ->
                this.lexer = lexer },
            { error ->
                fail("Error creando lexer: $error") }
        )
    }

    private fun getTokens(input: String): List<Token> {
        val result = lexer.lex(input)
        return result.fold(
            { tokens -> tokens },
            { error -> fail("Error al procesar '$input': $error") }
        )
    }

    @Test
    fun specialKeywordTest() {
        val tokens = getTokens("let")

        assertEquals(TokenType.Let, tokens.first().type)

        assertEquals(TokenType.EOF, tokens.last().type)

    }



    @Test

    fun identifierTest() {

        val tokens = getTokens("miVariable")

        val first = tokens.first()

        assertTrue(first.type is TokenType.Identifier)
        assertEquals("miVariable", first.lexeme)

    }



    @Test

    fun dataTypeStringTest() {

        val tokens = getTokens("string")

        assertEquals(TokenType.DataType.String, tokens.first().type)

    }



    @Test

    fun numberLiteralsTest() {

        val intTokens = getTokens("42")

        assertTrue(intTokens.first().type is TokenType.NumberLiteral)

        assertEquals("42", intTokens.first().lexeme)



        val decimalTokens = getTokens("3.14")

        assertTrue(decimalTokens.first().type is TokenType.NumberLiteral)

        assertEquals("3.14", decimalTokens.first().lexeme)
    }



    @Test

    fun stringLiteralsTest() {

        val doubleQuoteTokens = getTokens("\"Hola\"")

        assertTrue(doubleQuoteTokens.first().type is TokenType.StringLiteral)

        assertEquals("\"Hola\"", doubleQuoteTokens.first().lexeme)



        val singleQuoteTokens = getTokens("'Hola'")

        assertTrue(singleQuoteTokens.first().type is TokenType.StringLiteral)

        assertEquals("\'Hola\'", singleQuoteTokens.first().lexeme)

    }



    @Test

    fun operatorsTest() {

        val tokens = getTokens("+ - * / =")

        val expectedOps = listOf("+", "-", "*", "/", "=")



        expectedOps.forEachIndexed { i, op ->

            val token = tokens[i]

            assertTrue(token.type is TokenType.Operator)

            assertEquals(op, token.lexeme)

        }

    }



    @Test

    fun symbolsTest() {

        val tokens = getTokens(": ; ( )")

        assertEquals(TokenType.Colon, tokens[0].type)

        assertEquals(TokenType.Semicolon, tokens[1].type)

        assertEquals(TokenType.LeftParen, tokens[2].type)

        assertEquals(TokenType.RightParen, tokens[3].type)

    }



    @Test

    fun declarationTest() {

        val source = "let x: number = 42;"

        val tokens = getTokens(source)



        assertEquals(TokenType.Let, tokens[0].type)

        assertTrue(tokens[1].type is TokenType.Identifier)

        assertEquals(TokenType.Colon, tokens[2].type)

        assertEquals(TokenType.DataType.Number, tokens[3].type)

        assertTrue(tokens[4].type is TokenType.Operator)

        assertTrue(tokens[5].type is TokenType.NumberLiteral)
        assertEquals(TokenType.Semicolon, tokens[6].type)

    }



    @Test
    fun unknownTokensTest() {
        // Trabajamos directamente con el Result en lugar de usar getTokens
        val result = lexer.lex("@@@")
        assertTrue(result.isFailure)
        result.fold(
            { fail("Debería haber fallado para tokens desconocidos") },
            { error -> assertTrue(error.toString().contains("Token inesperado")) }
        )
    }



    @Test

    fun printlnAndVariableDeclarationTest() {

        val source = "println(\"Hola\") let var1: string = 'Mundo'; \n let name : int = 780"


        val tokens = getTokens(source)

        assertEquals(TokenType.Println, tokens[0].type)
        assertEquals(TokenType.LeftParen, tokens[1].type)
        assertTrue(tokens[2].type is TokenType.StringLiteral)
        assertEquals(TokenType.RightParen, tokens[3].type)
        assertEquals(TokenType.Let, tokens[4].type)
        assertTrue(tokens[5].type is TokenType.Identifier)
        assertEquals(TokenType.Colon, tokens[6].type)
        assertEquals(TokenType.DataType.String, tokens[7].type)
        assertTrue(tokens[8].type is TokenType.Operator)
        assertTrue(tokens[9].type is TokenType.StringLiteral)
        assertEquals(TokenType.Semicolon, tokens[10].type)

    }

}