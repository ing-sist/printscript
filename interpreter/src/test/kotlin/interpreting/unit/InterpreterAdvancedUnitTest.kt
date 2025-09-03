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
import interpreting.core.InterpreterFactory
import interpreting.modules.CoreModule
import org.junit.jupiter.api.Test
import runtime.Output
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests unitarios enfocados en casos edge y funcionalidad avanzada del intérprete.
 * Sin dependencias del parser/lexer para máximo bajo acoplamiento.
 */
class InterpreterAdvancedUnitTest {
    private fun createTestOutput(): Pair<Output, MutableList<String>> {
        val outputBuffer = mutableListOf<String>()
        val output = Output { line -> outputBuffer.add(line) }
        return Pair(output, outputBuffer)
    }

    private fun createLocation(
        line: Int = 1,
        startCol: Int = 1,
        endCol: Int = 1,
    ) = Location(line, startCol, endCol)

    private fun createToken(
        type: TokenType,
        lexeme: String,
        line: Int = 1,
    ) = Token(type, lexeme, createLocation(line))

    @Test
    fun `should handle string and number concatenation`() {
        val (output, buffer) = createTestOutput()
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .withOutput(output)
                .addModule(CoreModule())
                .create()

        // AST: let name: string = "Alice"; let age: number = 25; println("Hello " + name + ", age: " + age);
        val nameDecl =
            DeclarationAssignmentNode(
                IdentifierNode(createToken(TokenType.Identifier, "name"), "name"),
                createToken(TokenType.StringType, "string"),
                LiteralNode(createToken(TokenType.StringLiteral, "Alice")),
            )

        val ageDecl =
            DeclarationAssignmentNode(
                IdentifierNode(createToken(TokenType.Identifier, "age"), "age"),
                createToken(TokenType.NumberType, "number"),
                LiteralNode(createToken(TokenType.NumberLiteral, "25")),
            )

        // "Hello " + name + ", age: " + age
        val hello = LiteralNode(createToken(TokenType.StringLiteral, "Hello "))
        val nameVar = IdentifierNode(createToken(TokenType.Identifier, "name"), "name")
        val ageText = LiteralNode(createToken(TokenType.StringLiteral, ", age: "))
        val ageVar = IdentifierNode(createToken(TokenType.Identifier, "age"), "age")

        val concat1 = BinaryOperationNode(hello, createToken(TokenType.Plus, "+"), nameVar)
        val concat2 = BinaryOperationNode(concat1, createToken(TokenType.Plus, "+"), ageText)
        val concat3 = BinaryOperationNode(concat2, createToken(TokenType.Plus, "+"), ageVar)

        val printStmt = PrintlnNode(concat3)

        val result = interpreter.runProgram(listOf(nameDecl, ageDecl, printStmt))

        assertTrue(result.isSuccess)
        assertEquals("Hello Alice, age: 25", buffer[0])
    }

    @Test
    fun `should handle complex nested arithmetic expressions`() {
        val (output, buffer) = createTestOutput()
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .withOutput(output)
                .addModule(CoreModule())
                .create()

        // AST: println((2 + 3) * (4 - 1)); -> 15
        val two = LiteralNode(createToken(TokenType.NumberLiteral, "2"))
        val three = LiteralNode(createToken(TokenType.NumberLiteral, "3"))
        val four = LiteralNode(createToken(TokenType.NumberLiteral, "4"))
        val one = LiteralNode(createToken(TokenType.NumberLiteral, "1"))

        val addition = BinaryOperationNode(two, createToken(TokenType.Plus, "+"), three)
        val subtraction = BinaryOperationNode(four, createToken(TokenType.Minus, "-"), one)
        val multiplication = BinaryOperationNode(addition, createToken(TokenType.Multiply, "*"), subtraction)

        val printStmt = PrintlnNode(multiplication)

        val result = interpreter.runProgram(listOf(printStmt))

        assertTrue(result.isSuccess)
        assertEquals("15", buffer[0])
    }

    @Test
    fun `should handle all comparison operators correctly`() {
        val (output, buffer) = createTestOutput()
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .withOutput(output)
                .addModule(CoreModule())
                .create()

        val statements = mutableListOf<AstNode>()

        // AST: println(5 < 10); println(10 <= 10); println(15 > 10); println(10 >= 10);
        val comparisons =
            listOf(
                Triple("5", TokenType.LessThan, "10"),
                Triple("10", TokenType.LessThanOrEqual, "10"),
                Triple("15", TokenType.GreaterThan, "10"),
                Triple("10", TokenType.GreaterThanOrEqual, "10"),
            )

        comparisons.forEach { (leftVal, op, rightVal) ->
            val left = LiteralNode(createToken(TokenType.NumberLiteral, leftVal))
            val right = LiteralNode(createToken(TokenType.NumberLiteral, rightVal))
            val comparison = BinaryOperationNode(left, createToken(op, op.toString()), right)
            statements.add(PrintlnNode(comparison))
        }

        val result = interpreter.runProgram(statements)

        assertTrue(result.isSuccess)
        assertEquals(listOf("true", "true", "true", "true"), buffer)
    }

    @Test
    fun `should handle variable scoping in nested assignments`() {
        val (output, buffer) = createTestOutput()
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .withOutput(output)
                .addModule(CoreModule())
                .create()

        // AST: let x: number = 5; let y: number = x * 2; x = y + 1; println(x); println(y);
        val xDecl =
            DeclarationAssignmentNode(
                IdentifierNode(createToken(TokenType.Identifier, "x"), "x"),
                createToken(TokenType.NumberType, "number"),
                LiteralNode(createToken(TokenType.NumberLiteral, "5")),
            )

        val yDecl =
            DeclarationAssignmentNode(
                IdentifierNode(createToken(TokenType.Identifier, "y"), "y"),
                createToken(TokenType.NumberType, "number"),
                BinaryOperationNode(
                    IdentifierNode(createToken(TokenType.Identifier, "x"), "x"),
                    createToken(TokenType.Multiply, "*"),
                    LiteralNode(createToken(TokenType.NumberLiteral, "2")),
                ),
            )

        val xAssign =
            AssignmentNode(
                IdentifierNode(createToken(TokenType.Identifier, "x"), "x"),
                BinaryOperationNode(
                    IdentifierNode(createToken(TokenType.Identifier, "y"), "y"),
                    createToken(TokenType.Plus, "+"),
                    LiteralNode(createToken(TokenType.NumberLiteral, "1")),
                ),
            )

        val printX = PrintlnNode(IdentifierNode(createToken(TokenType.Identifier, "x"), "x"))
        val printY = PrintlnNode(IdentifierNode(createToken(TokenType.Identifier, "y"), "y"))

        val result = interpreter.runProgram(listOf(xDecl, yDecl, xAssign, printX, printY))

        assertTrue(result.isSuccess)
        assertEquals(listOf("11", "10"), buffer)
    }

    @Test
    fun `should handle division operations correctly`() {
        val (output, buffer) = createTestOutput()
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .withOutput(output)
                .addModule(CoreModule())
                .create()

        // AST: println(10 / 2); println(7 / 2);
        val statements =
            listOf(
                PrintlnNode(
                    BinaryOperationNode(
                        LiteralNode(createToken(TokenType.NumberLiteral, "10")),
                        createToken(TokenType.Divide, "/"),
                        LiteralNode(createToken(TokenType.NumberLiteral, "2")),
                    ),
                ),
                PrintlnNode(
                    BinaryOperationNode(
                        LiteralNode(createToken(TokenType.NumberLiteral, "7")),
                        createToken(TokenType.Divide, "/"),
                        LiteralNode(createToken(TokenType.NumberLiteral, "2")),
                    ),
                ),
            )

        val result = interpreter.runProgram(statements)

        assertTrue(result.isSuccess)
        assertEquals(listOf("5", "3.5"), buffer)
    }

    @Test
    fun `should propagate errors correctly in complex expressions`() {
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .addModule(CoreModule())
                .create()

        // AST: (5 + undefined) * 2 - should fail at undefined variable
        val five = LiteralNode(createToken(TokenType.NumberLiteral, "5"))
        val undefined = IdentifierNode(createToken(TokenType.Identifier, "undefined", 2), "undefined")
        val two = LiteralNode(createToken(TokenType.NumberLiteral, "2"))

        val addition = BinaryOperationNode(five, createToken(TokenType.Plus, "+"), undefined)
        val multiplication = BinaryOperationNode(addition, createToken(TokenType.Multiply, "*"), two)

        // log para ver erro
        System.out.println(undefined.value.location.line)

        val result = interpreter.evaluateExpression(multiplication)

        assertTrue(result.isFailure)
        val error = result.errorOrNull()!!
        assertTrue(error.message.contains("Undefined variable"))
    }

    @Test
    fun `should handle empty program correctly`() {
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .addModule(CoreModule())
                .create()

        val result = interpreter.runProgram(emptyList())

        assertTrue(result.isSuccess)
    }

    @Test
    fun `should handle nil equality correctly`() {
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .addModule(CoreModule())
                .create()

        // Crear dos variables no inicializadas y compararlas
        val xDecl =
            DeclarationNode(
                IdentifierNode(createToken(TokenType.Identifier, "x"), "x"),
                createToken(TokenType.NumberType, "number"),
            )

        val yDecl =
            DeclarationNode(
                IdentifierNode(createToken(TokenType.Identifier, "y"), "y"),
                createToken(TokenType.NumberType, "number"),
            )

        interpreter.runProgram(listOf(xDecl, yDecl))

        // AST: x == y (both nil)
        val xVar = IdentifierNode(createToken(TokenType.Identifier, "x"), "x")
        val yVar = IdentifierNode(createToken(TokenType.Identifier, "y"), "y")
        val equality = BinaryOperationNode(xVar, createToken(TokenType.Equals, "=="), yVar)

        val result = interpreter.evaluateExpression(equality)

        assertTrue(result.isSuccess)
        assertEquals("true", result.getOrNull()!!.stringify())
    }

    @Test
    fun `should handle type errors with specific error messages`() {
        val interpreter =
            InterpreterFactory
                .newInterpreter()
                .addModule(CoreModule())
                .create()

        // Test different type error scenarios
        val testCases =
            listOf(
                Triple("5", TokenType.Minus, "\"hello\"") to "must be numbers",
                Triple("5", TokenType.Multiply, "\"world\"") to "must be numbers",
                Triple("5", TokenType.Divide, "\"test\"") to "must be numbers",
            )

        testCases.forEach { (case, expectedError) ->
            val (leftVal, op, rightVal) = case
            val left = LiteralNode(createToken(TokenType.NumberLiteral, leftVal))
            val right = LiteralNode(createToken(TokenType.StringLiteral, rightVal.removeSurrounding("\"")))
            val operation = BinaryOperationNode(left, createToken(op, op.toString()), right)

            val result = interpreter.evaluateExpression(operation)

            assertTrue(result.isFailure, "Operation $leftVal $op $rightVal should fail")
            assertTrue(result.errorOrNull()!!.message.contains(expectedError))
        }
    }
}
