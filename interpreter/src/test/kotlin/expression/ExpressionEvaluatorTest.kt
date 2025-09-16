package expression

import DeclarationAssignmentNode
import DeclarationNode
import IdentifierNode
import LiteralNode
import Location
import Token
import TokenType
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import runtime.core.Interpreter
import runtime.core.InterpreterRuntime
import runtime.core.InterpreterRuntimeFactory
import runtime.providers.BufferedOutputSink
import runtime.providers.MapEnvProvider
import runtime.providers.ProgrammaticInputProvider

class ExpressionEvaluatorTest {
    private lateinit var interpreter: Interpreter
    private lateinit var outputSink: BufferedOutputSink

    @BeforeEach
    fun setUp() {
        outputSink = BufferedOutputSink()
        interpreter =
            InterpreterRuntimeFactory().createRuntime(
                ProgrammaticInputProvider(mutableListOf("test_input", "42")),
                MapEnvProvider(mapOf("TEST_VAR" to "environment_value")),
                outputSink,
            )
    }

    @Test
    fun `interpreter can execute simple string declaration`() {
        val declaration = createStringDeclaration("message", "Hello World", true)
        val result = interpreter.execute(declaration)

        assertTrue(result.isSuccess, "String declaration should succeed")
    }

    @Test
    fun `interpreter can execute simple number declaration`() {
        val declaration = createNumberDeclaration("count", 42.0, true)
        val result = interpreter.execute(declaration)

        assertTrue(result.isSuccess, "Number declaration should succeed")
    }

    @Test
    fun `interpreter can execute simple boolean declaration`() {
        val declaration = createBooleanDeclaration("flag", true, true)
        val result = interpreter.execute(declaration)

        assertTrue(result.isSuccess, "Boolean declaration should succeed")
    }

    @Test
    fun `interpreter handles const declarations correctly`() {
        val declaration = createStringDeclaration("constant", "value", false)
        val result = interpreter.execute(declaration)

        assertTrue(result.isSuccess, "Const declaration should succeed")
    }

    @Test
    fun `interpreter factory creates valid runtime`() {
        val factory = InterpreterRuntimeFactory()
        val runtime = factory.createRuntime()

        assertNotNull(runtime)
        assertTrue(runtime is InterpreterRuntime)
    }

    @Test
    fun `interpreter factory creates runtime with custom providers`() {
        val factory = InterpreterRuntimeFactory()
        val customOutputSink = BufferedOutputSink()

        val runtime =
            factory.createRuntime(
                ProgrammaticInputProvider(mutableListOf()),
                MapEnvProvider(emptyMap()),
                customOutputSink,
            )

        assertNotNull(runtime)
        assertTrue(runtime is InterpreterRuntime)
    }

    private fun createStringDeclaration(
        name: String,
        value: String,
        isMutable: Boolean,
    ) = DeclarationAssignmentNode(
        DeclarationNode(
            IdentifierNode(Token(TokenType.Identifier, name, Location(0, 0, 0)), name),
            Token(TokenType.StringType, "string", Location(0, 0, 0)),
            isMutable,
        ),
        LiteralNode(Token(TokenType.StringLiteral, "\"$value\"", Location(0, 0, 0))),
    )

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

    private fun createBooleanDeclaration(
        name: String,
        value: Boolean,
        isMutable: Boolean,
    ) = DeclarationAssignmentNode(
        DeclarationNode(
            IdentifierNode(Token(TokenType.Identifier, name, Location(0, 0, 0)), name),
            Token(TokenType.BooleanType, "boolean", Location(0, 0, 0)),
            isMutable,
        ),
        LiteralNode(Token(TokenType.BooleanLiteral, value.toString(), Location(0, 0, 0))),
    )
}
