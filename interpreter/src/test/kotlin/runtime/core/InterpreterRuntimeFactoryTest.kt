package runtime.core

import DeclarationAssignmentNode
import DeclarationNode
import IdentifierNode
import LiteralNode
import Location
import Token
import TokenType
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import runtime.providers.BufferedOutputSink
import runtime.providers.MapEnvProvider
import runtime.providers.ProgrammaticInputProvider

class InterpreterRuntimeFactoryTest {
    @Test
    fun `createRuntime with default providers creates valid interpreter`() {
        val factory = InterpreterRuntimeFactory()

        val runtime = factory.createRuntime()

        assertNotNull(runtime)
        assertTrue(runtime is InterpreterRuntime)
    }

    @Test
    fun `createRuntime with custom providers creates valid interpreter`() {
        val factory = InterpreterRuntimeFactory()
        val inputProvider = ProgrammaticInputProvider(mutableListOf("test"))
        val envProvider = MapEnvProvider(mapOf("TEST" to "value"))
        val outputSink = BufferedOutputSink()

        val runtime = factory.createRuntime(inputProvider, envProvider, outputSink)

        assertNotNull(runtime)
        assertTrue(runtime is InterpreterRuntime)
    }

    @Test
    fun `factory registers all required expression evaluators`() {
        val factory = InterpreterRuntimeFactory()
        val outputSink = BufferedOutputSink()

        val runtime =
            factory.createRuntime(
                ProgrammaticInputProvider(mutableListOf()),
                MapEnvProvider(emptyMap()),
                outputSink,
            )

        // Test that the runtime can handle basic expressions within a statement
        // Create a declaration that uses a literal expression: let x: number = 42;
        val declaration =
            DeclarationAssignmentNode(
                DeclarationNode(
                    IdentifierNode(Token(TokenType.Identifier, "x", Location(0, 0, 0)), "x"),
                    Token(TokenType.NumberType, "number", Location(0, 0, 0)),
                    true,
                ),
                createNumberLiteral(42.0), // Esta expresión será evaluada internamente
            )

        val result = runtime.execute(declaration)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `factory registers all required statement executors`() {
        val factory = InterpreterRuntimeFactory()
        val outputSink = BufferedOutputSink()

        val runtime =
            factory.createRuntime(
                ProgrammaticInputProvider(mutableListOf()),
                MapEnvProvider(emptyMap()),
                outputSink,
            )

        // Test that the runtime can handle basic statements
        val declaration = createStringDeclaration("test", "value", true)
        val result = runtime.execute(declaration)
        assertTrue(result.isSuccess)
    }

    private fun createNumberLiteral(value: Double) =
        LiteralNode(
            Token(
                TokenType.NumberLiteral,
                value.toString(),
                Location(0, 0, 0),
            ),
        )

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
}
