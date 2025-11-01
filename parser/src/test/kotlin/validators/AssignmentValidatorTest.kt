import AssignmentNode
import BinaryOperationNode
import IdentifierNode
import LiteralNode
import Result
import TokenType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import util.TestTokenProvider
import util.tok
import validators.AssignmentValidator

class AssignmentValidatorTest {
    @Test
    @DisplayName("validates and builds simple assignment with expression")
    fun testAssignment() {
        val tokens =
            listOf(
                tok(TokenType.Identifier, "x"),
                tok(TokenType.Assignment, "="),
                tok(TokenType.NumberLiteral, "1"),
                tok(TokenType.Operator.Plus, "+"),
                tok(TokenType.NumberLiteral, "2"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.EOF, "EOF"),
            )
        val result = AssignmentValidator().validateAndBuild(TestTokenProvider(tokens))
        assertTrue(result is Result.Success)
        val node = (result as Result.Success).value as AssignmentNode
        val id = assertInstanceOf(IdentifierNode::class.java, node.identifier) as IdentifierNode
        assertEquals("x", id.name)
        val expr = assertInstanceOf(BinaryOperationNode::class.java, node.expression) as BinaryOperationNode
        assertEquals(TokenType.Operator.Plus, expr.operator.type)
        assertInstanceOf(LiteralNode::class.java, expr.left)
        assertInstanceOf(LiteralNode::class.java, expr.right)
    }

    @Test
    @DisplayName("returns Failure(null) when pattern doesn't match")
    fun testNotAssignment() {
        val tokens =
            listOf(
                tok(TokenType.FunctionCall, "println"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.RightParen, ")"),
                tok(TokenType.Semicolon, ";"),
                tok(TokenType.EOF, "EOF"),
            )
        val result = AssignmentValidator().validateAndBuild(TestTokenProvider(tokens))
        assertTrue(result is Result.Failure && result.errorOrNull() == null)
    }
}
