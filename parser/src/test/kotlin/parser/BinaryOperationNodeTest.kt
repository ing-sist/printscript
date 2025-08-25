package parser

import AssignmentNode
import BinaryOperationNode
import LiteralNode
import Location
import Token
import TokenType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import validators.DefaultValidatorsProvider

class BinaryOperationNodeTest {
    @Test
    fun `should parse simple addition`() {
        val tokens =
            listOf(
                Token(TokenType.Identifier, "result", Location(1, 1, 6)),
                Token(TokenType.Assignment, "=", Location(1, 8, 8)),
                Token(TokenType.NumberLiteral, "5", Location(1, 10, 10)),
                Token(TokenType.Plus, "+", Location(1, 12, 12)),
                Token(TokenType.NumberLiteral, "3", Location(1, 14, 14)),
                Token(TokenType.Semicolon, ";", Location(1, 15, 15)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(1, statements.size)

        val assignment = statements[0] as AssignmentNode
        val binaryOp = assignment.expression as BinaryOperationNode
        assertEquals(TokenType.Plus, binaryOp.operator.type)
        assertTrue(binaryOp.left is LiteralNode)
        assertTrue(binaryOp.right is LiteralNode)
    }

    @Test
    fun `should parse multiplication with correct precedence`() {
        val tokens =
            listOf(
                Token(TokenType.Identifier, "result", Location(1, 1, 6)),
                Token(TokenType.Assignment, "=", Location(1, 8, 8)),
                Token(TokenType.NumberLiteral, "2", Location(1, 10, 10)),
                Token(TokenType.Plus, "+", Location(1, 12, 12)),
                Token(TokenType.NumberLiteral, "3", Location(1, 14, 14)),
                Token(TokenType.Multiply, "*", Location(1, 16, 16)),
                Token(TokenType.NumberLiteral, "4", Location(1, 18, 18)),
                Token(TokenType.Semicolon, ";", Location(1, 19, 19)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(1, statements.size)

        val assignment = statements[0] as AssignmentNode
        val binaryOp = assignment.expression as BinaryOperationNode
        assertEquals(TokenType.Plus, binaryOp.operator.type)

        // Left side should be a literal (2)
        assertTrue(binaryOp.left is LiteralNode)

        // Right side should be a binary operation (3 * 4) due to precedence
        assertTrue(binaryOp.right is BinaryOperationNode)
        val rightOp = binaryOp.right as BinaryOperationNode
        assertEquals(TokenType.Multiply, rightOp.operator.type)
    }

    @Test
    fun `should parse subtraction and division`() {
        val tokens =
            listOf(
                Token(TokenType.Identifier, "calc", Location(1, 1, 4)),
                Token(TokenType.Assignment, "=", Location(1, 6, 6)),
                Token(TokenType.NumberLiteral, "20", Location(1, 8, 9)),
                Token(TokenType.Minus, "-", Location(1, 11, 11)),
                Token(TokenType.NumberLiteral, "8", Location(1, 13, 13)),
                Token(TokenType.Divide, "/", Location(1, 15, 15)),
                Token(TokenType.NumberLiteral, "2", Location(1, 17, 17)),
                Token(TokenType.Semicolon, ";", Location(1, 18, 18)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(1, statements.size)

        val assignment = statements[0] as AssignmentNode
        val binaryOp = assignment.expression as BinaryOperationNode
        assertEquals(TokenType.Minus, binaryOp.operator.type)

        // Right side should be division (8 / 2) due to precedence
        assertTrue(binaryOp.right is BinaryOperationNode)
        val rightOp = binaryOp.right as BinaryOperationNode
        assertEquals(TokenType.Divide, rightOp.operator.type)
    }

    @Test
    fun `should parse expressions with parentheses`() {
        val tokens =
            listOf(
                Token(TokenType.Identifier, "calc", Location(1, 1, 4)),
                Token(TokenType.Assignment, "=", Location(1, 6, 6)),
                Token(TokenType.LeftParen, "(", Location(1, 8, 8)),
                Token(TokenType.NumberLiteral, "2", Location(1, 9, 9)),
                Token(TokenType.Plus, "+", Location(1, 11, 11)),
                Token(TokenType.NumberLiteral, "3", Location(1, 13, 13)),
                Token(TokenType.RightParen, ")", Location(1, 14, 14)),
                Token(TokenType.Multiply, "*", Location(1, 16, 16)),
                Token(TokenType.NumberLiteral, "4", Location(1, 18, 18)),
                Token(TokenType.Semicolon, ";", Location(1, 19, 19)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(1, statements.size)

        val assignment = statements[0] as AssignmentNode
        val binaryOp = assignment.expression as BinaryOperationNode
        assertEquals(TokenType.Multiply, binaryOp.operator.type)

        // Left side should be addition (2 + 3) due to parentheses
        assertTrue(binaryOp.left is BinaryOperationNode)
        val leftOp = binaryOp.left as BinaryOperationNode
        assertEquals(TokenType.Plus, leftOp.operator.type)
    }

    @Test
    fun `should parse complex nested expressions`() {
        val tokens =
            listOf(
                Token(TokenType.Identifier, "complex", Location(1, 1, 7)),
                Token(TokenType.Assignment, "=", Location(1, 9, 9)),
                Token(TokenType.NumberLiteral, "1", Location(1, 11, 11)),
                Token(TokenType.Plus, "+", Location(1, 13, 13)),
                Token(TokenType.NumberLiteral, "2", Location(1, 15, 15)),
                Token(TokenType.Multiply, "*", Location(1, 17, 17)),
                Token(TokenType.NumberLiteral, "3", Location(1, 19, 19)),
                Token(TokenType.Minus, "-", Location(1, 21, 21)),
                Token(TokenType.NumberLiteral, "4", Location(1, 23, 23)),
                Token(TokenType.Divide, "/", Location(1, 25, 25)),
                Token(TokenType.NumberLiteral, "2", Location(1, 27, 27)),
                Token(TokenType.Semicolon, ";", Location(1, 28, 28)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(1, statements.size)

        val assignment = statements[0] as AssignmentNode
        assertTrue(assignment.expression is BinaryOperationNode)

        // The expression should respect operator precedence:
        // 1 + 2 * 3 - 4 / 2 = 1 + (2 * 3) - (4 / 2)
        val rootOp = assignment.expression as BinaryOperationNode
        // Should be structured according to left-to-right and precedence rules
        assertTrue(rootOp.operator.type in listOf(TokenType.Plus, TokenType.Minus))
    }
}
