import dsl.testCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Tests mejorados usando DSL para mayor legibilidad.
 * Ahora es evidente qué código fuente está siendo testeado.
 */
class BinaryOperationNodeTest {
    @Test
    fun `should parse simple addition`() {
        val result =
            testCode("result = 5 + 3;")
                .shouldParseSuccessfully()
                .withStatementCount(1)
                .withStatementType(0, AssignmentNode::class.java)

        val assignment = result.getStatement(0) as AssignmentNode
        val binaryOp = assignment.expression as BinaryOperationNode

        assertEquals(TokenType.Plus, binaryOp.operator.type)
        assertEquals("5", (binaryOp.left as LiteralNode).value.lexeme)
        assertEquals("3", (binaryOp.right as LiteralNode).value.lexeme)
    }

    @Test
    fun `should parse multiplication with correct precedence`() {
        // Testea: 2 + 3 * 4
        // Debe parsear como: 2 + (3 * 4) debido a precedencia
        val result =
            testCode("result = 2 + 3 * 4;")
                .shouldParseSuccessfully()
                .withStatementCount(1)

        val assignment = result.getStatement(0) as AssignmentNode
        val rootOp = assignment.expression as BinaryOperationNode

        // La raíz debe ser suma
        assertEquals(TokenType.Plus, rootOp.operator.type)

        // El lado derecho debe ser multiplicación (3 * 4)
        val rightOp = rootOp.right as BinaryOperationNode
        assertEquals(TokenType.Multiply, rightOp.operator.type)
    }

    @Test
    fun `should parse expressions with parentheses changing precedence`() {
        // Testea: (2 + 3) * 4
        // Los paréntesis cambian la precedencia
        val result =
            testCode("calc = (2 + 3) * 4;")
                .shouldParseSuccessfully()
                .withStatementCount(1)

        val assignment = result.getStatement(0) as AssignmentNode
        val rootOp = assignment.expression as BinaryOperationNode

        // La raíz debe ser multiplicación
        assertEquals(TokenType.Multiply, rootOp.operator.type)

        // El lado izquierdo debe ser suma (2 + 3)
        val leftOp = rootOp.left as BinaryOperationNode
        assertEquals(TokenType.Plus, leftOp.operator.type)
    }

    @Test
    fun `should parse complex nested expressions`() {
        // Testea una expresión compleja con múltiples operadores
        // 1 + 2 * 3 - 4 / 2
        testCode("complex = 1 + 2 * 3 - 4 / 2;")
            .shouldParseSuccessfully()
            .withStatementCount(1)
            .withStatementType(0, AssignmentNode::class.java)
    }
}
