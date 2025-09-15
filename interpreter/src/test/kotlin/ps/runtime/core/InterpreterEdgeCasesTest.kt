package ps.runtime.core

import AssignmentNode
import AstNode
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
import ps.lang.errors.UndeclaredVariableError
import ps.runtime.providers.BufferedOutputSink
import ps.runtime.providers.MapEnvProvider
import ps.runtime.providers.ProgrammaticInputProvider

class InterpreterEdgeCasesTest {
    private lateinit var interpreter: Interpreter
    private lateinit var outputSink: BufferedOutputSink

    @BeforeEach
    fun setUp() {
        outputSink = BufferedOutputSink()
        interpreter =
            InterpreterRuntimeFactory().createRuntime(
                ProgrammaticInputProvider(mutableListOf("user_input", "123")),
                MapEnvProvider(mapOf("TEST_ENV" to "env_value")),
                outputSink,
            )
    }

    @Test
    fun `println function call works`() {
        // println("Hello World");
        val printCall =
            FunctionCallNode(
                "printLn",
                LiteralNode(Token(TokenType.StringLiteral, "\"Hello World\"", Location(0, 0, 0))),
                isVoid = true,
            )

        val result = interpreter.execute(printCall)
        assertTrue(result.isSuccess)
        assertEquals(listOf("Hello World"), outputSink.getOutput())
    }

    @Test
    fun `readInput function with declaration`() {
        // let input: string = readInput("Enter: ");
        val readInputCall =
            FunctionCallNode(
                "readInput",
                LiteralNode(Token(TokenType.StringLiteral, "\"Enter: \"", Location(0, 0, 0))),
            )
        val declaration = createDeclarationWithExpression("input", TokenType.StringType, readInputCall, true)

        val result = interpreter.execute(declaration)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `readEnv function works`() {
        // let env: string = readEnv("TEST_ENV");
        val readEnvCall =
            FunctionCallNode(
                "readEnv",
                LiteralNode(Token(TokenType.StringLiteral, "\"TEST_ENV\"", Location(0, 0, 0))),
            )
        val declaration = createDeclarationWithExpression("env", TokenType.StringType, readEnvCall, true)

        val result = interpreter.execute(declaration)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `binary operations work correctly`() {
        // let result: number = 5 + 3;
        val addition =
            BinaryOperationNode(
                LiteralNode(Token(TokenType.NumberLiteral, "5", Location(0, 0, 0))),
                Token(TokenType.Plus, "+", Location(0, 0, 0)),
                LiteralNode(Token(TokenType.NumberLiteral, "3", Location(0, 0, 0))),
            )
        val declaration = createDeclarationWithExpression("result", TokenType.NumberType, addition, true)

        val result = interpreter.execute(declaration)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `variable assignment after declaration`() {
        // let x: number = 10; x = 20;
        val declaration = createNumberDeclaration("x", 10.0, true)
        interpreter.execute(declaration)

        val assignment =
            AssignmentNode(
                IdentifierNode(Token(TokenType.Identifier, "x", Location(0, 0, 0)), "x"),
                LiteralNode(Token(TokenType.NumberLiteral, "20", Location(0, 0, 0))),
            )

        val result = interpreter.execute(assignment)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `undeclared variable access fails`() {
        // x = 5; (sin declarar x antes)
        val assignment =
            AssignmentNode(
                IdentifierNode(Token(TokenType.Identifier, "undeclared", Location(0, 0, 0)), "undeclared"),
                LiteralNode(Token(TokenType.NumberLiteral, "5", Location(0, 0, 0))),
            )

        val result = interpreter.execute(assignment)
        assertTrue(result.isFailure)
        assertTrue(result.errorOrNull() is UndeclaredVariableError)
    }

    @Test
    fun `if statement execution`() {
        // if (true) { println("true branch"); }
        val condition = LiteralNode(Token(TokenType.BooleanLiteral, "true", Location(0, 0, 0)))
        val thenBranch =
            listOf(
                FunctionCallNode(
                    "printLn",
                    LiteralNode(Token(TokenType.StringLiteral, "\"true branch\"", Location(0, 0, 0))),
                ),
            )
        val ifStatement = ConditionalNode(condition, thenBranch, null)

        val result = interpreter.execute(ifStatement)
        assertTrue(result.isSuccess)
        assertEquals(listOf("true branch"), outputSink.getOutput())
    }

    private fun createNumberDeclaration(
        name: String,
        value: Double,
        isMutable: Boolean,
    ) = DeclarationAssignmentNode(
        DeclarationNode(
            IdentifierNode(Token(TokenType.Identifier, name, Location(0, 0, 0)), name),
            Token(TokenType.NumberType, "number", Location(0, 0, 0)),
            isMutable,
        ),
        LiteralNode(Token(TokenType.NumberLiteral, value.toString(), Location(0, 0, 0))),
    )

    private fun createDeclarationWithExpression(
        name: String,
        type: TokenType,
        expression: AstNode,
        isMutable: Boolean,
    ) = DeclarationAssignmentNode(
        DeclarationNode(
            IdentifierNode(Token(TokenType.Identifier, name, Location(0, 0, 0)), name),
            Token(type, type.toString().lowercase(), Location(0, 0, 0)),
            isMutable,
        ),
        expression,
    )
}
