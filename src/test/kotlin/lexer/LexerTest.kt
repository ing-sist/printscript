package lexer

import org.example.common.TokenType
import org.example.lexer.LexerGenerator
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test
import kotlin.test.assertEquals

class LexerTest {
    private val lexer = LexerGenerator.createLexer("1.0")

    @Test
    fun specialKeywordTest() {
        val tokens = lexer.lex("let")

        assertEquals(TokenType.Let, tokens.first().type)

        assertEquals(TokenType.EOF, tokens.last().type)

    }



    @Test

    fun identifierTest() {

        val tokens = lexer.lex("miVariable")

        val first = tokens.first()

        assertTrue(first.type is TokenType.Identifier)

        assertEquals("miVariable", (first.type as TokenType.Identifier).name)

    }



    @Test

    fun dataTypeStringTest() {

        val tokens = lexer.lex("string")

        assertEquals(TokenType.DataType.String, tokens.first().type)

    }



    @Test

    fun numberLiteralsTest() {

        val intTokens = lexer.lex("42")

        assertTrue(intTokens.first().type is TokenType.NumberLiteral)

        assertEquals(42.0, (intTokens.first().type as TokenType.NumberLiteral).value)



        val decimalTokens = lexer.lex("3.14")

        assertTrue(decimalTokens.first().type is TokenType.NumberLiteral)

        assertEquals(3.14, (decimalTokens.first().type as TokenType.NumberLiteral).value)

    }



    @Test

    fun stringLiteralsTest() {

        val doubleQuoteTokens = lexer.lex("\"Hola\"")

        assertTrue(doubleQuoteTokens.first().type is TokenType.StringLiteral)

        assertEquals("Hola", (doubleQuoteTokens.first().type as TokenType.StringLiteral).value)



        val singleQuoteTokens = lexer.lex("'Hola'")

        assertTrue(singleQuoteTokens.first().type is TokenType.StringLiteral)

        assertEquals("Hola", (singleQuoteTokens.first().type as TokenType.StringLiteral).value)

    }



    @Test

    fun operatorsTest() {

        val tokens = lexer.lex("+ - * / =")

        val expectedOps = listOf("+", "-", "*", "/", "=")



        expectedOps.forEachIndexed { i, op ->

            val token = tokens[i]

            assertTrue(token.type is TokenType.Operator)

            assertEquals(op, (token.type as TokenType.Operator).value)

        }

    }



    @Test

    fun symbolsTest() {

        val tokens = lexer.lex(": ; ( )")

        assertEquals(TokenType.Colon, tokens[0].type)

        assertEquals(TokenType.Semicolon, tokens[1].type)

        assertEquals(TokenType.LeftParen, tokens[2].type)

        assertEquals(TokenType.RightParen, tokens[3].type)

    }



    @Test

    fun declarationTest() {

        val source = "let x: number = 42;"

        val tokens = lexer.lex(source)



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

        val ex = assertThrows(IllegalStateException::class.java) {
            lexer.lex("@@@")
        }
        assertTrue(ex.message!!.contains("Token inesperado"))
    }



    @Test

    fun printlnAndVariableDeclarationTest() {

        val source = "println(\"Hola\") let var1: string = 'Mundo'; \n let name : int = 780"


        val tokens = lexer.lex(source)

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
