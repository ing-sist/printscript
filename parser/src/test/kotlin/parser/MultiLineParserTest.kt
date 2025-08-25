package parser

import AssignmentNode
import BinaryOperationNode
import DeclarationAssignmentNode
import DeclarationNode
import IdentifierNode
import LiteralNode
import Location
import PrintlnNode
import Token
import TokenType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import validators.DefaultValidatorsProvider

class MultiLineParserTest {
    @Test
    fun `should parse multiple variable declarations`() {
        val tokens =
            listOf(
                // let x: number;
                Token(TokenType.VariableDeclaration, "let", Location(1, 1, 3)),
                Token(TokenType.Identifier, "x", Location(1, 5, 5)),
                Token(TokenType.Colon, ":", Location(1, 6, 6)),
                Token(TokenType.NumberType, "number", Location(1, 8, 13)),
                Token(TokenType.Semicolon, ";", Location(1, 14, 14)),
                // let name: string;
                Token(TokenType.VariableDeclaration, "let", Location(2, 1, 3)),
                Token(TokenType.Identifier, "name", Location(2, 5, 8)),
                Token(TokenType.Colon, ":", Location(2, 9, 9)),
                Token(TokenType.StringType, "string", Location(2, 11, 16)),
                Token(TokenType.Semicolon, ";", Location(2, 17, 17)),
                // let result: number = 42;
                Token(TokenType.VariableDeclaration, "let", Location(3, 1, 3)),
                Token(TokenType.Identifier, "result", Location(3, 5, 10)),
                Token(TokenType.Colon, ":", Location(3, 11, 11)),
                Token(TokenType.NumberType, "number", Location(3, 13, 18)),
                Token(TokenType.Assignment, "=", Location(3, 20, 20)),
                Token(TokenType.NumberLiteral, "42", Location(3, 22, 23)),
                Token(TokenType.Semicolon, ";", Location(3, 24, 24)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(3, statements.size)

        // First statement: let x: number;
        val firstDeclaration = statements[0] as DeclarationNode
        assertEquals("x", firstDeclaration.identifier.name)
        assertEquals(TokenType.NumberType, firstDeclaration.type.type)

        // Second statement: let name: string;
        val secondDeclaration = statements[1] as DeclarationNode
        assertEquals("name", secondDeclaration.identifier.name)
        assertEquals(TokenType.StringType, secondDeclaration.type.type)

        // Third statement: let result: number = 42;
        val thirdDeclaration = statements[2] as DeclarationAssignmentNode
        assertEquals("result", thirdDeclaration.identifier.name)
        assertEquals(TokenType.NumberType, thirdDeclaration.type.type)
        assertTrue(thirdDeclaration.value is LiteralNode)
    }

    @Test
    fun `should parse complex program with declarations, assignments and println`() {
        val tokens =
            listOf(
                // let x: number = 10;
                Token(TokenType.VariableDeclaration, "let", Location(1, 1, 3)),
                Token(TokenType.Identifier, "x", Location(1, 5, 5)),
                Token(TokenType.Colon, ":", Location(1, 6, 6)),
                Token(TokenType.NumberType, "number", Location(1, 8, 13)),
                Token(TokenType.Assignment, "=", Location(1, 15, 15)),
                Token(TokenType.NumberLiteral, "10", Location(1, 17, 18)),
                Token(TokenType.Semicolon, ";", Location(1, 19, 19)),
                // let y: number = 5;
                Token(TokenType.VariableDeclaration, "let", Location(2, 1, 3)),
                Token(TokenType.Identifier, "y", Location(2, 5, 5)),
                Token(TokenType.Colon, ":", Location(2, 6, 6)),
                Token(TokenType.NumberType, "number", Location(2, 8, 13)),
                Token(TokenType.Assignment, "=", Location(2, 15, 15)),
                Token(TokenType.NumberLiteral, "5", Location(2, 17, 17)),
                Token(TokenType.Semicolon, ";", Location(2, 18, 18)),
                // let sum: number = x + y * 2;
                Token(TokenType.VariableDeclaration, "let", Location(3, 1, 3)),
                Token(TokenType.Identifier, "sum", Location(3, 5, 7)),
                Token(TokenType.Colon, ":", Location(3, 8, 8)),
                Token(TokenType.NumberType, "number", Location(3, 10, 15)),
                Token(TokenType.Assignment, "=", Location(3, 17, 17)),
                Token(TokenType.Identifier, "x", Location(3, 19, 19)),
                Token(TokenType.Plus, "+", Location(3, 21, 21)),
                Token(TokenType.Identifier, "y", Location(3, 23, 23)),
                Token(TokenType.Multiply, "*", Location(3, 25, 25)),
                Token(TokenType.NumberLiteral, "2", Location(3, 27, 27)),
                Token(TokenType.Semicolon, ";", Location(3, 28, 28)),
                // println(sum);
                Token(TokenType.FunctionCall, "println", Location(4, 1, 7)),
                Token(TokenType.LeftParen, "(", Location(4, 8, 8)),
                Token(TokenType.Identifier, "sum", Location(4, 9, 11)),
                Token(TokenType.RightParen, ")", Location(4, 12, 12)),
                Token(TokenType.Semicolon, ";", Location(4, 13, 13)),
                // sum = sum + 10;
                Token(TokenType.Identifier, "sum", Location(5, 1, 3)),
                Token(TokenType.Assignment, "=", Location(5, 5, 5)),
                Token(TokenType.Identifier, "sum", Location(5, 7, 9)),
                Token(TokenType.Plus, "+", Location(5, 11, 11)),
                Token(TokenType.NumberLiteral, "10", Location(5, 13, 14)),
                Token(TokenType.Semicolon, ";", Location(5, 15, 15)),
                // println("Final result: ");
                Token(TokenType.FunctionCall, "println", Location(6, 1, 7)),
                Token(TokenType.LeftParen, "(", Location(6, 8, 8)),
                Token(TokenType.StringLiteral, "\"Final result: \"", Location(6, 9, 24)),
                Token(TokenType.RightParen, ")", Location(6, 25, 25)),
                Token(TokenType.Semicolon, ";", Location(6, 26, 26)),
                // println(sum);
                Token(TokenType.FunctionCall, "println", Location(7, 1, 7)),
                Token(TokenType.LeftParen, "(", Location(7, 8, 8)),
                Token(TokenType.Identifier, "sum", Location(7, 9, 11)),
                Token(TokenType.RightParen, ")", Location(7, 12, 12)),
                Token(TokenType.Semicolon, ";", Location(7, 13, 13)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(7, statements.size)

        // Verify each statement type
        assertTrue(statements[0] is DeclarationAssignmentNode) // let x: number = 10;
        assertTrue(statements[1] is DeclarationAssignmentNode) // let y: number = 5;
        assertTrue(statements[2] is DeclarationAssignmentNode) // let sum: number = x + y * 2;
        assertTrue(statements[3] is PrintlnNode) // println(sum);
        assertTrue(statements[4] is AssignmentNode) // sum = sum + 10;
        assertTrue(statements[5] is PrintlnNode) // println("Final result: ");
        assertTrue(statements[6] is PrintlnNode) // println(sum);

        // Verify complex expression parsing (x + y * 2)
        val sumDeclaration = statements[2] as DeclarationAssignmentNode
        assertEquals("sum", sumDeclaration.identifier.name)
        assertTrue(sumDeclaration.value is BinaryOperationNode)

        val sumExpression = sumDeclaration.value as BinaryOperationNode
        assertEquals(TokenType.Plus, sumExpression.operator.type)

        // Left side should be identifier 'x'
        val leftId = sumExpression.left as IdentifierNode
        assertEquals("x", leftId.name)

        // Right side should be 'y * 2'
        val rightOp = sumExpression.right as BinaryOperationNode
        assertEquals(TokenType.Multiply, rightOp.operator.type)

        val yId = rightOp.left as IdentifierNode
        assertEquals("y", yId.name)

        val twoLiteral = rightOp.right as LiteralNode
        assertEquals("2", twoLiteral.value.lexeme)
    }

    @Test
    fun `should parse program with nested expressions and parentheses`() {
        val tokens =
            listOf(
                // let result: number = (10 + 5) * (3 - 1);
                Token(TokenType.VariableDeclaration, "let", Location(1, 1, 3)),
                Token(TokenType.Identifier, "result", Location(1, 5, 10)),
                Token(TokenType.Colon, ":", Location(1, 11, 11)),
                Token(TokenType.NumberType, "number", Location(1, 13, 18)),
                Token(TokenType.Assignment, "=", Location(1, 20, 20)),
                Token(TokenType.LeftParen, "(", Location(1, 22, 22)),
                Token(TokenType.NumberLiteral, "10", Location(1, 23, 24)),
                Token(TokenType.Plus, "+", Location(1, 26, 26)),
                Token(TokenType.NumberLiteral, "5", Location(1, 28, 28)),
                Token(TokenType.RightParen, ")", Location(1, 29, 29)),
                Token(TokenType.Multiply, "*", Location(1, 31, 31)),
                Token(TokenType.LeftParen, "(", Location(1, 33, 33)),
                Token(TokenType.NumberLiteral, "3", Location(1, 34, 34)),
                Token(TokenType.Minus, "-", Location(1, 36, 36)),
                Token(TokenType.NumberLiteral, "1", Location(1, 38, 38)),
                Token(TokenType.RightParen, ")", Location(1, 39, 39)),
                Token(TokenType.Semicolon, ";", Location(1, 40, 40)),
                // println(result);
                Token(TokenType.FunctionCall, "println", Location(2, 1, 7)),
                Token(TokenType.LeftParen, "(", Location(2, 8, 8)),
                Token(TokenType.Identifier, "result", Location(2, 9, 14)),
                Token(TokenType.RightParen, ")", Location(2, 15, 15)),
                Token(TokenType.Semicolon, ";", Location(2, 16, 16)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(2, statements.size)

        // Verify declaration with complex expression
        val declaration = statements[0] as DeclarationAssignmentNode
        assertEquals("result", declaration.identifier.name)
        assertTrue(declaration.value is BinaryOperationNode)

        val multiplyOp = declaration.value as BinaryOperationNode
        assertEquals(TokenType.Multiply, multiplyOp.operator.type)

        // Both sides should be binary operations due to parentheses
        assertTrue(multiplyOp.left is BinaryOperationNode)
        assertTrue(multiplyOp.right is BinaryOperationNode)

        val leftAdd = multiplyOp.left as BinaryOperationNode
        assertEquals(TokenType.Plus, leftAdd.operator.type)

        val rightSub = multiplyOp.right as BinaryOperationNode
        assertEquals(TokenType.Minus, rightSub.operator.type)

        // Verify println
        val println = statements[1] as PrintlnNode
        val printedId = println.content as IdentifierNode
        assertEquals("result", printedId.name)
    }

    @Test
    fun `should handle parsing errors in multi-line programs`() {
        val tokens =
            listOf(
                // let x: number = 10; (valid)
                Token(TokenType.VariableDeclaration, "let", Location(1, 1, 3)),
                Token(TokenType.Identifier, "x", Location(1, 5, 5)),
                Token(TokenType.Colon, ":", Location(1, 6, 6)),
                Token(TokenType.NumberType, "number", Location(1, 8, 13)),
                Token(TokenType.Assignment, "=", Location(1, 15, 15)),
                Token(TokenType.NumberLiteral, "10", Location(1, 17, 18)),
                Token(TokenType.Semicolon, ";", Location(1, 19, 19)),
                // invalid syntax: let y = 5; (missing type)
                Token(TokenType.VariableDeclaration, "let", Location(2, 1, 3)),
                Token(TokenType.Identifier, "y", Location(2, 5, 5)),
                Token(TokenType.Assignment, "=", Location(2, 7, 7)),
                Token(TokenType.NumberLiteral, "5", Location(2, 9, 9)),
                Token(TokenType.Semicolon, ";", Location(2, 10, 10)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isFailure)
        val error = result.errorOrNull()!!
        assertTrue(error is ParseError.NoValidParser)
    }

    @Test
    fun `should parse empty program`() {
        val tokens =
            listOf(
                Token(TokenType.EOF, "", Location(1, 1, 1)),
            )

        val parser = Parser(DefaultValidatorsProvider())
        val result = parser.parse(tokens)

        assertTrue(result.isSuccess)
        val statements = result.getOrNull()!!
        assertEquals(0, statements.size)
    }
}
