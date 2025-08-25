package parser

import AssignmentNode
import BinaryOperationNode
import DeclarationAssignmentNode
import IdentifierNode
import PrintlnNode
import TokenType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import parser.dsl.testProgram

/**
 * Tests de programas complejos usando DSL.
 * Muestra cómo el DSL hace que los tests de múltiples líneas sean muy legibles.
 */
class ImprovedMultiLineParserTest {
    @Test
    fun `should parse a complete program with declarations, assignments and println`() {
        // Un programa completo que es fácil de entender
        val result =
            testProgram(
                "let x: number = 10;",
                "let y: number = 5;",
                "let sum: number = x + y * 2;",
                "println(sum);",
                "sum = sum + 10;",
                "println(\"Final result: \");",
                "println(sum);",
            ).shouldParseSuccessfully()
                .withStatementCount(7)

        // Verificar tipos de statements
        result
            .withStatementType(0, DeclarationAssignmentNode::class.java) // let x: number = 10;
            .withStatementType(1, DeclarationAssignmentNode::class.java) // let y: number = 5;
            .withStatementType(2, DeclarationAssignmentNode::class.java) // let sum: number = x + y * 2;
            .withStatementType(3, PrintlnNode::class.java) // println(sum);
            .withStatementType(4, AssignmentNode::class.java) // sum = sum + 10;
            .withStatementType(5, PrintlnNode::class.java) // println("Final result: ");
            .withStatementType(6, PrintlnNode::class.java) // println(sum);

        // Verificar que la expresión compleja se parsea correctamente
        val sumDeclaration = result.getStatement(2) as DeclarationAssignmentNode
        val expression = sumDeclaration.value as BinaryOperationNode
        assertEquals(TokenType.Plus, expression.operator.type)

        val leftId = expression.left as IdentifierNode
        assertEquals("x", leftId.name)

        val rightOp = expression.right as BinaryOperationNode
        assertEquals(TokenType.Multiply, rightOp.operator.type)
    }

    @Test
    fun `should parse program with complex nested expressions and parentheses`() {
        // Expresiones complejas con paréntesis
        val result =
            testProgram(
                "let result: number = (10 + 5) * (3 - 1);",
                "println(result);",
            ).shouldParseSuccessfully()
                .withStatementCount(2)

        val declaration = result.getStatement(0) as DeclarationAssignmentNode
        val multiplyOp = declaration.value as BinaryOperationNode
        assertEquals(TokenType.Multiply, multiplyOp.operator.type)
    }

    @Test
    fun `should handle parsing errors with clear context`() {
        // Error: falta el tipo en la declaración
        testProgram(
            "let x: number = 10;", // línea válida
            "let y = 5;", // línea inválida - falta tipo
        ).shouldFailToParse()
    }

    @Test
    fun `should parse calculator-like expressions`() {
        // Un programa que simula una calculadora
        testProgram(
            "let a: number = 10;",
            "let b: number = 5;",
            "let sum: number = a + b;",
            "let difference: number = a - b;",
            "let product: number = a * b;",
            "let quotient: number = a / b;",
            "println(sum);",
            "println(difference);",
            "println(product);",
            "println(quotient);",
        ).shouldParseSuccessfully()
            .withStatementCount(10)
    }

    @Test
    fun `should parse empty program`() {
        testProgram()
            .shouldParseSuccessfully()
            .withStatementCount(0)
    }
}
