package parser

import AssignmentNode
import BinaryOperationNode
import DeclarationNode
import IdentifierNode
import Location
import PrintlnNode
import Token
import TokenType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import validators.DefaultValidatorsProvider

class IdentifierNodeTest {
    @Test
    fun `should parse identifier in assignment`() {
        val tokens =
            listOf(
                Token(TokenType.Identifier, "myVariable", Location(1, 1, 10)),
                Token(TokenType.Assignment, "=", Location(1, 12, 12)),
                Token(TokenType.NumberLiteral, "42", Location(1, 14, 15)),
                Token(TokenType.Semicolon, ";", Location(1, 16, 16)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(1, statements.size)

        val assignment = statements[0] as AssignmentNode
        assertEquals("myVariable", assignment.identifier.name)
        assertEquals("myVariable", assignment.identifier.value.lexeme)
    }

    @Test
    fun `should parse identifier in declaration`() {
        val tokens =
            listOf(
                Token(TokenType.VariableDeclaration, "let", Location(1, 1, 3)),
                Token(TokenType.Identifier, "userName", Location(1, 5, 12)),
                Token(TokenType.Colon, ":", Location(1, 13, 13)),
                Token(TokenType.StringType, "string", Location(1, 15, 20)),
                Token(TokenType.Semicolon, ";", Location(1, 21, 21)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(1, statements.size)

        val declaration = statements[0] as DeclarationNode
        assertEquals("userName", declaration.identifier.name)
        assertEquals("userName", declaration.identifier.value.lexeme)
    }

    @Test
    fun `should parse identifier in expression`() {
        val tokens =
            listOf(
                Token(TokenType.Identifier, "result", Location(1, 1, 6)),
                Token(TokenType.Assignment, "=", Location(1, 8, 8)),
                Token(TokenType.Identifier, "x", Location(1, 10, 10)),
                Token(TokenType.Plus, "+", Location(1, 12, 12)),
                Token(TokenType.Identifier, "y", Location(1, 14, 14)),
                Token(TokenType.Semicolon, ";", Location(1, 15, 15)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(1, statements.size)

        val assignment = statements[0] as AssignmentNode
        assertEquals("result", assignment.identifier.name)

        val binaryOp = assignment.expression as BinaryOperationNode
        val leftIdentifier = binaryOp.left as IdentifierNode
        val rightIdentifier = binaryOp.right as IdentifierNode

        assertEquals("x", leftIdentifier.name)
        assertEquals("y", rightIdentifier.name)
    }

    @Test
    fun `should parse identifier in println`() {
        val tokens =
            listOf(
                Token(TokenType.FunctionCall, "println", Location(1, 1, 7)),
                Token(TokenType.LeftParen, "(", Location(1, 8, 8)),
                Token(TokenType.Identifier, "message", Location(1, 9, 15)),
                Token(TokenType.RightParen, ")", Location(1, 16, 16)),
                Token(TokenType.Semicolon, ";", Location(1, 17, 17)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(1, statements.size)

        val println = statements[0] as PrintlnNode
        val identifier = println.content as IdentifierNode
        assertEquals("message", identifier.name)
    }

    @Test
    fun `should parse multiple identifiers in complex expression`() {
        val tokens =
            listOf(
                Token(TokenType.Identifier, "total", Location(1, 1, 5)),
                Token(TokenType.Assignment, "=", Location(1, 7, 7)),
                Token(TokenType.Identifier, "price", Location(1, 9, 13)),
                Token(TokenType.Multiply, "*", Location(1, 15, 15)),
                Token(TokenType.Identifier, "quantity", Location(1, 17, 24)),
                Token(TokenType.Plus, "+", Location(1, 26, 26)),
                Token(TokenType.Identifier, "tax", Location(1, 28, 30)),
                Token(TokenType.Semicolon, ";", Location(1, 31, 31)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(1, statements.size)

        val assignment = statements[0] as AssignmentNode
        assertEquals("total", assignment.identifier.name)

        // Should parse as: (price * quantity) + tax
        val rootOp = assignment.expression as BinaryOperationNode
        assertEquals(TokenType.Plus, rootOp.operator.type)

        val leftMultiply = rootOp.left as BinaryOperationNode
        assertEquals(TokenType.Multiply, leftMultiply.operator.type)

        val priceId = leftMultiply.left as IdentifierNode
        val quantityId = leftMultiply.right as IdentifierNode
        val taxId = rootOp.right as IdentifierNode

        assertEquals("price", priceId.name)
        assertEquals("quantity", quantityId.name)
        assertEquals("tax", taxId.name)
    }

    @Test
    fun `should parse single character identifiers`() {
        val tokens =
            listOf(
                Token(TokenType.Identifier, "x", Location(1, 1, 1)),
                Token(TokenType.Assignment, "=", Location(1, 3, 3)),
                Token(TokenType.Identifier, "y", Location(1, 5, 5)),
                Token(TokenType.Semicolon, ";", Location(1, 6, 6)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(1, statements.size)

        val assignment = statements[0] as AssignmentNode
        assertEquals("x", assignment.identifier.name)

        val rightIdentifier = assignment.expression as IdentifierNode
        assertEquals("y", rightIdentifier.name)
    }
}
