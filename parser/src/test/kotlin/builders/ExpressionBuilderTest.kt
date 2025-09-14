import AstNode
import BinaryOperationNode
import IdentifierNode
import LiteralNode
import TokenType
import UnaryOperationNode
import builders.ExpressionBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import util.tok

class ExpressionBuilderTest {
    @Test
    @DisplayName("builds binary operations with precedence and parentheses")
    fun testBinaryPrecedence() {
        val tokens =
            listOf(
                tok(TokenType.NumberLiteral, "1"),
                tok(TokenType.Plus, "+"),
                tok(TokenType.NumberLiteral, "2"),
                tok(TokenType.Multiply, "*"),
                tok(TokenType.LeftParen, "("),
                tok(TokenType.NumberLiteral, "3"),
                tok(TokenType.Plus, "+"),
                tok(TokenType.NumberLiteral, "4"),
                tok(TokenType.RightParen, ")"),
            )
        val node = ExpressionBuilder().build(tokens)
        // Expect: 1 + 2 * (3 + 4)
        val plus1 = assertInstanceOf(BinaryOperationNode::class.java, node) as BinaryOperationNode
        assertEquals(TokenType.Plus, plus1.operator.type)
        val left = assertInstanceOf(LiteralNode::class.java, plus1.left) as LiteralNode
        assertEquals("1", left.value.lexeme)
        val right = assertInstanceOf(BinaryOperationNode::class.java, plus1.right) as BinaryOperationNode
        assertEquals(TokenType.Multiply, right.operator.type)
        val rLeft = assertInstanceOf(LiteralNode::class.java, right.left) as LiteralNode
        assertEquals("2", rLeft.value.lexeme)
        val rRight = assertInstanceOf(BinaryOperationNode::class.java, right.right) as BinaryOperationNode
        assertEquals(TokenType.Plus, rRight.operator.type)
    }

    @Test
    @DisplayName("builds unary operations when only one operand on stack")
    fun testUnaryOperation() {
        val tokens =
            listOf(
                tok(TokenType.Minus, "-"),
                tok(TokenType.Identifier, "x"),
            )
        val node = ExpressionBuilder().build(tokens)
        val unary = assertInstanceOf(UnaryOperationNode::class.java, node) as UnaryOperationNode
        assertEquals(TokenType.Minus, unary.operator.type)
        assertInstanceOf(IdentifierNode::class.java, unary.operand)
    }

    @Test
    @DisplayName("supports boolean literals as leaf nodes")
    fun testBooleanLiteral() {
        val tokens = listOf(tok(TokenType.BooleanLiteral, "true"))
        val node: AstNode = ExpressionBuilder().build(tokens)
        val lit = assertInstanceOf(LiteralNode::class.java, node) as LiteralNode
        assertEquals("true", lit.value.lexeme)
    }
}
