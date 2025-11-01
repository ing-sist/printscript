package runtime.core

import BinaryOperationNode
import ConditionalNode
import DeclarationAssignmentNode
import DeclarationNode
import FunctionCallNode
import IdentifierNode
import LiteralNode
import Location
import Token
import TokenType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import runtime.providers.BufferedOutputSink
import runtime.providers.MapEnvProvider
import runtime.providers.ProgrammaticInputProvider

class InterpreterOperationsTests {
    private lateinit var interpreter: Interpreter
    private lateinit var outputSink: BufferedOutputSink

    @BeforeEach
    fun setUp() {
        outputSink = BufferedOutputSink()
        interpreter =
            InterpreterRuntimeFactory().createRuntime(
                ProgrammaticInputProvider(mutableListOf()),
                MapEnvProvider(emptyMap()),
                outputSink,
            )
    }

    @Test
    fun `should perform number addition`() {
        // let result: number = 5 + 3;
        val addition =
            createBinaryOperation(
                createNumberLiteral(5.0),
                TokenType.Operator.Plus,
                createNumberLiteral(3.0),
            )
        val declaration = createNumberDeclarationWithExpression("result", addition)
        interpreter.execute(declaration)

        // printLn(result);
        val printCall = createPrintLnCall("result")
        interpreter.execute(printCall)

        assertEquals(listOf("8"), outputSink.getOutput())
    }

    @Test
    fun `should perform number subtraction`() {
        // let result: number = 10 - 4;
        val subtraction =
            createBinaryOperation(
                createNumberLiteral(10.0),
                TokenType.Operator.Minus,
                createNumberLiteral(4.0),
            )
        val declaration = createNumberDeclarationWithExpression("result", subtraction)
        interpreter.execute(declaration)

        val printCall = createPrintLnCall("result")
        interpreter.execute(printCall)

        assertEquals(listOf("6"), outputSink.getOutput())
    }

    @Test
    fun `should perform number multiplication`() {
        // let result: number = 6 * 7;
        val multiplication =
            createBinaryOperation(
                createNumberLiteral(6.0),
                TokenType.Operator.Multiply,
                createNumberLiteral(7.0),
            )
        val declaration = createNumberDeclarationWithExpression("result", multiplication)
        interpreter.execute(declaration)

        val printCall = createPrintLnCall("result")
        interpreter.execute(printCall)

        assertEquals(listOf("42"), outputSink.getOutput())
    }

    @Test
    fun `should perform number division`() {
        // let result: number = 15 / 3;
        val division =
            createBinaryOperation(
                createNumberLiteral(15.0),
                TokenType.Operator.Divide,
                createNumberLiteral(3.0),
            )
        val declaration = createNumberDeclarationWithExpression("result", division)
        interpreter.execute(declaration)

        val printCall = createPrintLnCall("result")
        interpreter.execute(printCall)

        assertEquals(listOf("5"), outputSink.getOutput())
    }

    @Test
    fun `should perform string concatenation with plus`() {
        // let result: string = "Hello" + " World";
        val concatenation =
            createBinaryOperation(
                createStringLiteral("Hello"),
                TokenType.Operator.Plus,
                createStringLiteral(" World"),
            )
        val declaration = createStringDeclarationWithExpression("result", concatenation)
        interpreter.execute(declaration)

        val printCall = createPrintLnCall("result")
        interpreter.execute(printCall)

        assertEquals(listOf("Hello World"), outputSink.getOutput())
    }

    @Test
    fun `should perform number comparison - equals`() {
        // let result: boolean = 5 == 5;
        val comparison =
            createBinaryOperation(
                createNumberLiteral(5.0),
                TokenType.Operator.Equals,
                createNumberLiteral(5.0),
            )
        val declaration = createBooleanDeclarationWithExpression("result", comparison)
        interpreter.execute(declaration)

        val printCall = createPrintLnCall("result")
        interpreter.execute(printCall)

        assertEquals(listOf("true"), outputSink.getOutput())
    }

    @Test
    fun `should perform number comparison - not equals`() {
        // let result: boolean = 5 != 3;
        val comparison =
            createBinaryOperation(
                createNumberLiteral(5.0),
                TokenType.Operator.NotEquals,
                createNumberLiteral(3.0),
            )
        val declaration = createBooleanDeclarationWithExpression("result", comparison)
        interpreter.execute(declaration)

        val printCall = createPrintLnCall("result")
        interpreter.execute(printCall)

        assertEquals(listOf("true"), outputSink.getOutput())
    }

    @Test
    fun `should perform number comparison - less than`() {
        // let result: boolean = 3 < 5;
        val comparison =
            createBinaryOperation(
                createNumberLiteral(3.0),
                TokenType.Operator.LessThan,
                createNumberLiteral(5.0),
            )
        val declaration = createBooleanDeclarationWithExpression("result", comparison)
        interpreter.execute(declaration)

        val printCall = createPrintLnCall("result")
        interpreter.execute(printCall)

        assertEquals(listOf("true"), outputSink.getOutput())
    }

    @Test
    fun `should execute if statement when condition is true`() {
        // if (true) { printLn("Then executed"); } else { printLn("Else executed"); }
        val conditional =
            ConditionalNode(
                createBooleanLiteral(true),
                listOf(createPrintLnCallWithLiteral("Then executed")),
                listOf(createPrintLnCallWithLiteral("Else executed")),
            )

        val result = interpreter.execute(conditional)

        assertTrue(result.isSuccess, "Conditional should succeed")
        assertEquals(listOf("Then executed"), outputSink.getOutput())
    }

    @Test
    fun `should execute else statement when condition is false`() {
        // if (false) { printLn("Then executed"); } else { printLn("Else executed"); }
        val conditional =
            ConditionalNode(
                createBooleanLiteral(false),
                listOf(createPrintLnCallWithLiteral("Then executed")),
                listOf(createPrintLnCallWithLiteral("Else executed")),
            )

        val result = interpreter.execute(conditional)

        assertTrue(result.isSuccess, "Conditional should succeed")
        assertEquals(listOf("Else executed"), outputSink.getOutput())
    }

    @Test
    fun `should execute if without else when condition is true`() {
        // if (true) { printLn("Executed"); }
        val conditional =
            ConditionalNode(
                createBooleanLiteral(true),
                listOf(createPrintLnCallWithLiteral("Executed")),
                null,
            )

        val result = interpreter.execute(conditional)

        assertTrue(result.isSuccess, "Conditional should succeed")
        assertEquals(listOf("Executed"), outputSink.getOutput())
    }

    @Test
    fun `should skip if without else when condition is false`() {
        // if (false) { printLn("Should not execute"); }
        val conditional =
            ConditionalNode(
                createBooleanLiteral(false),
                listOf(createPrintLnCallWithLiteral("Should not execute")),
                null,
            )

        val result = interpreter.execute(conditional)

        assertTrue(result.isSuccess, "Conditional should succeed")
        assertEquals(emptyList<String>(), outputSink.getOutput())
    }

    @Test
    fun `should handle complex condition with variables`() {
        // let x: number = 10;
        val xDeclaration = createNumberDeclaration("x", 10.0)
        interpreter.execute(xDeclaration)

        // let y: number = 5;
        val yDeclaration = createNumberDeclaration("y", 5.0)
        interpreter.execute(yDeclaration)

        // if (x > y) { printLn("x is greater"); }
        val comparison =
            createBinaryOperation(
                createVariableReference("x"),
                TokenType.Operator.GreaterThan,
                createVariableReference("y"),
            )
        val conditional =
            ConditionalNode(
                comparison,
                listOf(createPrintLnCallWithLiteral("x is greater")),
                null,
            )

        val result = interpreter.execute(conditional)

        assertTrue(result.isSuccess, "Complex conditional should succeed")
        assertEquals(listOf("x is greater"), outputSink.getOutput())
    }

    // Helper methods
    private fun createBinaryOperation(
        left: LiteralNode,
        operator: TokenType,
        right: LiteralNode,
    ): BinaryOperationNode = BinaryOperationNode(left, createToken(operator, operator.toString()), right)

    private fun createBinaryOperation(
        left: IdentifierNode,
        operator: TokenType,
        right: IdentifierNode,
    ): BinaryOperationNode = BinaryOperationNode(left, createToken(operator, operator.toString()), right)

    private fun createNumberDeclaration(
        name: String,
        value: Double,
    ): DeclarationAssignmentNode {
        val identifier = createIdentifier(name)
        val declaration = DeclarationNode(identifier, createToken(TokenType.NumberType, "number"), true)
        val valueNode = createNumberLiteral(value)
        return DeclarationAssignmentNode(declaration, valueNode)
    }

    private fun createNumberDeclarationWithExpression(
        name: String,
        expression: BinaryOperationNode,
    ): DeclarationAssignmentNode {
        val identifier = createIdentifier(name)
        val declaration = DeclarationNode(identifier, createToken(TokenType.NumberType, "number"), true)
        return DeclarationAssignmentNode(declaration, expression)
    }

    private fun createStringDeclarationWithExpression(
        name: String,
        expression: BinaryOperationNode,
    ): DeclarationAssignmentNode {
        val identifier = createIdentifier(name)
        val declaration = DeclarationNode(identifier, createToken(TokenType.StringType, "string"), true)
        return DeclarationAssignmentNode(declaration, expression)
    }

    private fun createBooleanDeclarationWithExpression(
        name: String,
        expression: BinaryOperationNode,
    ): DeclarationAssignmentNode {
        val identifier = createIdentifier(name)
        val declaration = DeclarationNode(identifier, createToken(TokenType.BooleanType, "boolean"), true)
        return DeclarationAssignmentNode(declaration, expression)
    }

    private fun createPrintLnCall(variableName: String): FunctionCallNode =
        FunctionCallNode("println", createVariableReference(variableName), true)

    private fun createPrintLnCallWithLiteral(message: String): FunctionCallNode =
        FunctionCallNode("println", createStringLiteral(message), true)

    private fun createIdentifier(name: String): IdentifierNode =
        IdentifierNode(
            createToken(TokenType.Identifier, name),
            name,
        )

    private fun createVariableReference(name: String): IdentifierNode = createIdentifier(name)

    private fun createStringLiteral(value: String): LiteralNode =
        LiteralNode(
            createToken(TokenType.StringLiteral, "\"$value\""),
        )

    private fun createNumberLiteral(value: Double): LiteralNode =
        LiteralNode(
            createToken(TokenType.NumberLiteral, value.toString()),
        )

    private fun createBooleanLiteral(value: Boolean): LiteralNode =
        LiteralNode(
            createToken(TokenType.Identifier, value.toString()),
        )

    private fun createToken(
        type: TokenType,
        lexeme: String,
    ): Token = Token(type, lexeme, Location(1, 1, 1))
}
