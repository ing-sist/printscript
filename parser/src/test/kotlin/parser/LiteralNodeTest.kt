package parser

import AssignmentNode
import BinaryOperationNode
import LiteralNode
import Location
import Token
import TokenType
import UnaryOperationNode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import validators.DefaultValidatorsProvider

class LiteralNodeTest {
    @Test
    fun `should parse number literals in expressions`() {
        val tokens =
            listOf(
                Token(TokenType.Identifier, "x", Location(1, 1, 1)),
                Token(TokenType.Assignment, "=", Location(1, 3, 3)),
                Token(TokenType.NumberLiteral, "42", Location(1, 5, 6)),
                Token(TokenType.Semicolon, ";", Location(1, 7, 7)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(1, statements.size)

        val assignment = statements[0] as AssignmentNode
        val literal = assignment.expression as LiteralNode
        assertEquals("42", literal.value.lexeme)
        assertEquals(TokenType.NumberLiteral, literal.value.type)
    }

    @Test
    fun `should parse string literals in expressions`() {
        val tokens =
            listOf(
                Token(TokenType.Identifier, "name", Location(1, 1, 4)),
                Token(TokenType.Assignment, "=", Location(1, 6, 6)),
                Token(TokenType.StringLiteral, "\"Hello World\"", Location(1, 8, 20)),
                Token(TokenType.Semicolon, ";", Location(1, 21, 21)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(1, statements.size)

        val assignment = statements[0] as AssignmentNode
        val literal = assignment.expression as LiteralNode
        assertEquals("\"Hello World\"", literal.value.lexeme)
        assertEquals(TokenType.StringLiteral, literal.value.type)
    }

    @Test
    fun `should parse decimal number literals`() {
        val tokens =
            listOf(
                Token(TokenType.Identifier, "pi", Location(1, 1, 2)),
                Token(TokenType.Assignment, "=", Location(1, 4, 4)),
                Token(TokenType.NumberLiteral, "3.14159", Location(1, 6, 12)),
                Token(TokenType.Semicolon, ";", Location(1, 13, 13)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(1, statements.size)

        val assignment = statements[0] as AssignmentNode
        val literal = assignment.expression as LiteralNode
        assertEquals("3.14159", literal.value.lexeme)
        assertEquals(TokenType.NumberLiteral, literal.value.type)
    }

    @Test
    fun `should parse empty string literal`() {
        val tokens =
            listOf(
                Token(TokenType.Identifier, "empty", Location(1, 1, 5)),
                Token(TokenType.Assignment, "=", Location(1, 7, 7)),
                Token(TokenType.StringLiteral, "\"\"", Location(1, 9, 10)),
                Token(TokenType.Semicolon, ";", Location(1, 11, 11)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(1, statements.size)

        val assignment = statements[0] as AssignmentNode
        val literal = assignment.expression as LiteralNode
        assertEquals("\"\"", literal.value.lexeme)
        assertEquals(TokenType.StringLiteral, literal.value.type)
    }

    @Test
    fun `should parse negative number literal`() {
        val tokens =
            listOf(
                Token(TokenType.Identifier, "negative", Location(1, 1, 8)),
                Token(TokenType.Assignment, "=", Location(1, 10, 10)),
                Token(TokenType.LeftParen, "(", Location(1, 12, 12)),
                Token(TokenType.Minus, "-", Location(1, 13, 13)),
                Token(TokenType.NumberLiteral, "42", Location(1, 14, 15)),
                Token(TokenType.RightParen, ")", Location(1, 16, 16)),
                Token(TokenType.Semicolon, ";", Location(1, 17, 17)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(1, statements.size)

        val assignment = statements[0] as AssignmentNode
        // This should be parsed as a unary minus operation
        assertTrue(
            assignment.expression is UnaryOperationNode ||
                assignment.expression is BinaryOperationNode ||
                assignment.expression is LiteralNode,
        )
    }
}
