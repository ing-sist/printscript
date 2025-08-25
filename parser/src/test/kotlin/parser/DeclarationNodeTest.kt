package parser

import DeclarationNode
import Location
import Token
import TokenType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import validators.DefaultValidatorsProvider

class DeclarationNodeTest {
    @Test
    fun `should parse simple variable declaration`() {
        val tokens =
            listOf(
                Token(TokenType.VariableDeclaration, "let", Location(1, 1, 3)),
                Token(TokenType.Identifier, "x", Location(1, 5, 5)),
                Token(TokenType.Colon, ":", Location(1, 6, 6)),
                Token(TokenType.NumberType, "number", Location(1, 8, 13)),
                Token(TokenType.Semicolon, ";", Location(1, 14, 14)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(1, statements.size)

        val declaration = statements[0] as DeclarationNode
        assertEquals("x", declaration.identifier.name)
        assertEquals(TokenType.NumberType, declaration.type.type)
    }

    @Test
    fun `should parse string variable declaration`() {
        val tokens =
            listOf(
                Token(TokenType.VariableDeclaration, "let", Location(1, 1, 3)),
                Token(TokenType.Identifier, "name", Location(1, 5, 8)),
                Token(TokenType.Colon, ":", Location(1, 9, 9)),
                Token(TokenType.StringType, "string", Location(1, 11, 16)),
                Token(TokenType.Semicolon, ";", Location(1, 17, 17)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(1, statements.size)

        val declaration = statements[0] as DeclarationNode
        assertEquals("name", declaration.identifier.name)
        assertEquals(TokenType.StringType, declaration.type.type)
    }

    @Test
    fun `should fail parsing invalid declaration`() {
        val tokens =
            listOf(
                Token(TokenType.VariableDeclaration, "let", Location(1, 1, 3)),
                Token(TokenType.Identifier, "x", Location(1, 5, 5)),
                Token(TokenType.Semicolon, ";", Location(1, 6, 6)), // Missing colon and type
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isFailure)
        val error = result.errorOrNull()!!
        assertTrue(error is ParseError.NoValidParser)
    }

    @Test
    fun `should parse multiple declarations`() {
        val tokens =
            listOf(
                Token(TokenType.VariableDeclaration, "let", Location(1, 1, 3)),
                Token(TokenType.Identifier, "x", Location(1, 5, 5)),
                Token(TokenType.Colon, ":", Location(1, 6, 6)),
                Token(TokenType.NumberType, "number", Location(1, 8, 13)),
                Token(TokenType.Semicolon, ";", Location(1, 14, 14)),
                Token(TokenType.VariableDeclaration, "let", Location(2, 1, 3)),
                Token(TokenType.Identifier, "y", Location(2, 5, 5)),
                Token(TokenType.Colon, ":", Location(2, 6, 6)),
                Token(TokenType.StringType, "string", Location(2, 8, 13)),
                Token(TokenType.Semicolon, ";", Location(2, 14, 14)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(2, statements.size)

        val firstDeclaration = statements[0] as DeclarationNode
        assertEquals("x", firstDeclaration.identifier.name)
        assertEquals(TokenType.NumberType, firstDeclaration.type.type)

        val secondDeclaration = statements[1] as DeclarationNode
        assertEquals("y", secondDeclaration.identifier.name)
        assertEquals(TokenType.StringType, secondDeclaration.type.type)
    }
}
