package interpreting.unit

import AssignmentNode
import AstNode
import BinaryOperationNode
import DeclarationAssignmentNode
import DeclarationNode
import IdentifierNode
import LiteralNode
import Location
import PrintlnNode
import Token
import TokenType
import UnaryOperationNode
import interpreting.core.InterpreterFactory
import interpreting.modules.CoreModule
import org.junit.jupiter.api.Test
import runtime.Output
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests unitarios puros del intérprete.
 * Construyen AST manualmente sin usar parser/lexer para bajo acoplamiento.
 */
class InterpreterUnitTest {
    private fun createTestOutput(): Pair<Output, MutableList<String>> {
        val outputBuffer = mutableListOf<String>()
        val output = Output { line -> outputBuffer.add(line) }
        return Pair(output, outputBuffer)
    }

    private fun createTestLocation() = Location(1, 1, 1)

    private fun createNumberToken(value: String) = Token(TokenType.NumberLiteral, value, createTestLocation())

    private fun createStringToken(value: String) = Token(TokenType.StringLiteral, value, createTestLocation())

    private fun createIdentifierToken(name: String) = Token(TokenType.Identifier, name, createTestLocation())

    private fun createOperatorToken(type: TokenType) = Token(type, type.toString(), createTestLocation())

    private fun createTypeToken(type: TokenType) = Token(type, type.toString(), createTestLocation())

    @Test
    fun `should evaluate literal numbers correctly`() {
        val (output, buffer) = createTestOutput()
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .withOutput(output)
                .addModule(CoreModule())
                .create()

        // AST: println(42);
        val literal = LiteralNode(createNumberToken("42"))
        val printStatement = PrintlnNode(literal)

        val result = interpreter.runProgram(listOf(printStatement))

        assertTrue(result.isSuccess)
        assertEquals("42", buffer[0])
    }

    @Test
    fun `should evaluate literal strings correctly`() {
        val (output, buffer) = createTestOutput()
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .withOutput(output)
                .addModule(CoreModule())
                .create()

        // AST: println("Hello World");
        val literal = LiteralNode(createStringToken("Hello World"))
        val printStatement = PrintlnNode(literal)

        val result = interpreter.runProgram(listOf(printStatement))

        assertTrue(result.isSuccess)
        assertEquals("Hello World", buffer[0])
    }

    @Test
    fun `should handle binary arithmetic operations`() {
        val (output, buffer) = createTestOutput()
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .withOutput(output)
                .addModule(CoreModule())
                .create()

        // AST: println(10 + 5);
        val left = LiteralNode(createNumberToken("10"))
        val right = LiteralNode(createNumberToken("5"))
        val addition = BinaryOperationNode(left, createOperatorToken(TokenType.Plus), right)
        val printStatement = PrintlnNode(addition)

        val result = interpreter.runProgram(listOf(printStatement))

        assertTrue(result.isSuccess)
        assertEquals("15", buffer[0])
    }

    @Test
    fun `should handle operator precedence in complex expressions`() {
        val (output, buffer) = createTestOutput()
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .withOutput(output)
                .addModule(CoreModule())
                .create()

        // AST: println(2 + 3 * 4); -> 14
        val literal2 = LiteralNode(createNumberToken("2"))
        val literal3 = LiteralNode(createNumberToken("3"))
        val literal4 = LiteralNode(createNumberToken("4"))
        val multiply = BinaryOperationNode(literal3, createOperatorToken(TokenType.Multiply), literal4)
        val addition = BinaryOperationNode(literal2, createOperatorToken(TokenType.Plus), multiply)
        val printStatement = PrintlnNode(addition)

        val result = interpreter.runProgram(listOf(printStatement))

        assertTrue(result.isSuccess)
        assertEquals("14", buffer[0])
    }

    @Test
    fun `should handle variable declaration and access`() {
        val (output, buffer) = createTestOutput()
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .withOutput(output)
                .addModule(CoreModule())
                .create()

        // AST: let x: number = 25; println(x);
        val identifier = IdentifierNode(createIdentifierToken("x"), "x")
        val value = LiteralNode(createNumberToken("25"))
        val declaration =
            DeclarationAssignmentNode(
                identifier,
                createTypeToken(TokenType.NumberType),
                value,
            )

        val variableAccess = IdentifierNode(createIdentifierToken("x"), "x")
        val printStatement = PrintlnNode(variableAccess)

        val result = interpreter.runProgram(listOf(declaration, printStatement))

        assertTrue(result.isSuccess)
        assertEquals("25", buffer[0])
    }

    @Test
    fun `should handle variable assignment`() {
        val (output, buffer) = createTestOutput()
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .withOutput(output)
                .addModule(CoreModule())
                .create()

        // AST: let x: number = 10; x = 20; println(x);
        val identifier = IdentifierNode(createIdentifierToken("x"), "x")
        val initialValue = LiteralNode(createNumberToken("10"))
        val declaration =
            DeclarationAssignmentNode(
                identifier,
                createTypeToken(TokenType.NumberType),
                initialValue,
            )

        val newValue = LiteralNode(createNumberToken("20"))
        val assignment =
            AssignmentNode(
                IdentifierNode(createIdentifierToken("x"), "x"),
                newValue,
            )

        val variableAccess = IdentifierNode(createIdentifierToken("x"), "x")
        val printStatement = PrintlnNode(variableAccess)

        val result = interpreter.runProgram(listOf(declaration, assignment, printStatement))

        assertTrue(result.isSuccess)
        assertEquals("20", buffer[0])
    }

    @Test
    fun `should handle string concatenation`() {
        val (output, buffer) = createTestOutput()
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .withOutput(output)
                .addModule(CoreModule())
                .create()

        // AST: println("Hello" + " " + "World");
        val hello = LiteralNode(createStringToken("Hello"))
        val space = LiteralNode(createStringToken(" "))
        val world = LiteralNode(createStringToken("World"))

        val firstConcat = BinaryOperationNode(hello, createOperatorToken(TokenType.Plus), space)
        val secondConcat = BinaryOperationNode(firstConcat, createOperatorToken(TokenType.Plus), world)
        val printStatement = PrintlnNode(secondConcat)

        val result = interpreter.runProgram(listOf(printStatement))

        assertTrue(result.isSuccess)
        assertEquals("Hello World", buffer[0])
    }

    @Test
    fun `should handle comparison operators`() {
        val (output, buffer) = createTestOutput()
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .withOutput(output)
                .addModule(CoreModule())
                .create()

        // AST: println(5 > 3);
        val five = LiteralNode(createNumberToken("5"))
        val three = LiteralNode(createNumberToken("3"))
        val comparison = BinaryOperationNode(five, createOperatorToken(TokenType.GreaterThan), three)
        val printStatement = PrintlnNode(comparison)

        val result = interpreter.runProgram(listOf(printStatement))

        assertTrue(result.isSuccess)
        assertEquals("true", buffer[0])
    }

    @Test
    fun `should handle unary minus operation`() {
        val (output, buffer) = createTestOutput()
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .withOutput(output)
                .addModule(CoreModule())
                .create()

        // AST: println(-42);
        val number = LiteralNode(createNumberToken("42"))
        val unaryMinus = UnaryOperationNode(createOperatorToken(TokenType.Minus), number)
        val printStatement = PrintlnNode(unaryMinus)

        val result = interpreter.runProgram(listOf(printStatement))

        assertTrue(result.isSuccess)
        assertEquals("-42", buffer[0])
    }

    @Test
    fun `should handle variable declaration without initialization`() {
        val (output, buffer) = createTestOutput()
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .withOutput(output)
                .addModule(CoreModule())
                .create()

        // AST: let x: number; println(x);
        val identifier = IdentifierNode(createIdentifierToken("x"), "x")
        val declaration = DeclarationNode(identifier, createTypeToken(TokenType.NumberType))

        val variableAccess = IdentifierNode(createIdentifierToken("x"), "x")
        val printStatement = PrintlnNode(variableAccess)

        val result = interpreter.runProgram(listOf(declaration, printStatement))

        assertTrue(result.isSuccess)
        assertEquals("nil", buffer[0])
    }

    @Test
    fun `should format numbers without trailing zero`() {
        val (output, buffer) = createTestOutput()
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .withOutput(output)
                .addModule(CoreModule())
                .create()

        // AST: println(5.0);
        val number = LiteralNode(createNumberToken("5.0"))
        val printStatement = PrintlnNode(number)

        val result = interpreter.runProgram(listOf(printStatement))

        assertTrue(result.isSuccess)
        assertEquals("5", buffer[0])
    }

    @Test
    fun `should format numbers with decimals correctly`() {
        val (output, buffer) = createTestOutput()
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .withOutput(output)
                .addModule(CoreModule())
                .create()

        // AST: println(3.14);
        val number = LiteralNode(createNumberToken("3.14"))
        val printStatement = PrintlnNode(number)

        val result = interpreter.runProgram(listOf(printStatement))

        assertTrue(result.isSuccess)
        assertEquals("3.14", buffer[0])
    }

    @Test
    fun `should handle undefined variable error with location`() {
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .addModule(CoreModule())
                .create()

        // AST: undefined variable access
        val undefinedVariable = IdentifierNode(createIdentifierToken("undefined"), "undefined")

        val result = interpreter.evaluateExpression(undefinedVariable)

        assertTrue(result.isFailure)
        val error = result.errorOrNull()!!
        assertTrue(error.message.contains("Undefined variable"))
        assertEquals(1, error.location?.line)
    }

    @Test
    fun `should handle division by zero error with location`() {
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .addModule(CoreModule())
                .create()

        // AST: 5 / 0
        val five = LiteralNode(createNumberToken("5"))
        val zero = LiteralNode(createNumberToken("0"))
        val division = BinaryOperationNode(five, createOperatorToken(TokenType.Divide), zero)

        val result = interpreter.evaluateExpression(division)

        assertTrue(result.isFailure)
        val error = result.errorOrNull()!!
        assertTrue(error.message.contains("Division by zero"))
        assertEquals(1, error.location?.line)
    }

    @Test
    fun `should handle type error with descriptive message`() {
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .addModule(CoreModule())
                .create()

        // AST: 5 - "hello"
        val number = LiteralNode(createNumberToken("5"))
        val string = LiteralNode(createStringToken("hello"))
        val subtraction = BinaryOperationNode(number, createOperatorToken(TokenType.Minus), string)

        val result = interpreter.evaluateExpression(subtraction)

        assertTrue(result.isFailure)
        val error = result.errorOrNull()!!
        assertTrue(error.message.contains("must be numbers"))
    }

    @Test
    fun `should handle equality operations correctly`() {
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .addModule(CoreModule())
                .create()

        // AST: 5 == 5
        val left = LiteralNode(createNumberToken("5"))
        val right = LiteralNode(createNumberToken("5"))
        val equality = BinaryOperationNode(left, createOperatorToken(TokenType.Equals), right)

        val result = interpreter.evaluateExpression(equality)

        assertTrue(result.isSuccess)
        assertEquals("true", result.getOrNull()!!.stringify())
    }

    @Test
    fun `should handle inequality operations correctly`() {
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .addModule(CoreModule())
                .create()

        // AST: 5 != 3
        val left = LiteralNode(createNumberToken("5"))
        val right = LiteralNode(createNumberToken("3"))
        val inequality = BinaryOperationNode(left, createOperatorToken(TokenType.NotEquals), right)

        val result = interpreter.evaluateExpression(inequality)

        assertTrue(result.isSuccess)
        assertEquals("true", result.getOrNull()!!.stringify())
    }

    @Test
    fun `should handle multiple variable assignments in sequence`() {
        val (output, buffer) = createTestOutput()
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .withOutput(output)
                .addModule(CoreModule())
                .create()

        // AST: let counter: number = 0; counter = 1; counter = 2; counter = 3; println(counter);
        val identifier = IdentifierNode(createIdentifierToken("counter"), "counter")
        val initialValue = LiteralNode(createNumberToken("0"))
        val declaration =
            DeclarationAssignmentNode(
                identifier,
                createTypeToken(TokenType.NumberType),
                initialValue,
            )

        val statements = mutableListOf<AstNode>(declaration)

        // Assignments: counter = 1, counter = 2, counter = 3
        for (i in 1..3) {
            val assignment =
                AssignmentNode(
                    IdentifierNode(createIdentifierToken("counter"), "counter"),
                    LiteralNode(createNumberToken(i.toString())),
                )
            statements.add(assignment)
        }

        val printStatement = PrintlnNode(IdentifierNode(createIdentifierToken("counter"), "counter"))
        statements.add(printStatement)

        val result = interpreter.runProgram(statements)

        assertTrue(result.isSuccess)
        assertEquals("3", buffer[0])
    }
}
