package ps.runtime.core

import AssignmentNode
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
import ps.lang.errors.ConstReassignmentError
import ps.lang.errors.TypeMismatchError
import ps.lang.errors.UndeclaredVariableError
import ps.runtime.providers.BufferedOutputSink
import ps.runtime.providers.MapEnvProvider
import ps.runtime.providers.ProgrammaticInputProvider

class InterpreterCoreTests {
    private lateinit var interpreter: Interpreter
    private lateinit var outputSink: BufferedOutputSink
    private lateinit var inputProvider: ProgrammaticInputProvider
    private lateinit var envProvider: MapEnvProvider

    @BeforeEach
    fun setUp() {
        outputSink = BufferedOutputSink()
        inputProvider = ProgrammaticInputProvider(mutableListOf())
        envProvider =
            MapEnvProvider(
                mapOf(
                    "TEST_VAR" to "test_value",
                    "NUMBER_VAR" to "42",
                    "BOOL_VAR" to "true",
                ),
            )

        interpreter =
            InterpreterRuntimeFactory().createRuntime(
                inputProvider,
                envProvider,
                outputSink,
            )
    }

    @Test
    fun `should declare and use string variable`() {
        // let message: string = "Hello World";
        val declaration = createStringDeclaration("message", "Hello World", true)
        val result = interpreter.execute(declaration)

        print(result)

        assertTrue(result.isSuccess, "String declaration should succeed")

        // printLn(message);
        val printCall = createPrintLnCall("message")
        val printResult = interpreter.execute(printCall)

        assertTrue(printResult.isSuccess, "Print should succeed")
        assertEquals(listOf("Hello World"), outputSink.getOutput())
    }

    @Test
    fun `should declare and use number variable`() {
        // let count: number = 42;
        val declaration = createNumberDeclaration("count", 42.0, true)
        val result = interpreter.execute(declaration)

        assertTrue(result.isSuccess, "Number declaration should succeed")

        // printLn(count);
        val printCall = createPrintLnCall("count")
        val printResult = interpreter.execute(printCall)

        assertTrue(printResult.isSuccess, "Print should succeed")
        assertEquals(listOf("42"), outputSink.getOutput())
    }

    @Test
    fun `should declare and use boolean variable`() {
        // let flag: boolean = true;
        val declaration = createBooleanDeclaration("flag", true, true)
        val result = interpreter.execute(declaration)

        assertTrue(result.isSuccess, "Boolean declaration should succeed")

        // printLn(flag);
        val printCall = createPrintLnCall("flag")
        val printResult = interpreter.execute(printCall)

        assertTrue(printResult.isSuccess, "Print should succeed")
        assertEquals(listOf("true"), outputSink.getOutput())
    }

    @Test
    fun `should forbid reassigning const variable`() {
        // const PI: number = 3.14;
        val declaration = createNumberDeclaration("PI", 3.14, false)
        val result = interpreter.execute(declaration)

        assertTrue(result.isSuccess, "Const declaration should succeed")

        // PI = 2.71; // Should fail
        val assignment = createAssignment("PI", createNumberLiteral(2.71))
        val assignResult = interpreter.execute(assignment)

        assertTrue(assignResult.isFailure, "Const reassignment should fail")
        assertTrue(assignResult.errorOrNull() is ConstReassignmentError)
    }

    @Test
    fun `should allow reassigning let variable`() {
        // let value: number = 10;
        val declaration = createNumberDeclaration("value", 10.0, true)
        interpreter.execute(declaration)

        // value = 20;
        val assignment = createAssignment("value", createNumberLiteral(20.0))
        val result = interpreter.execute(assignment)

        assertTrue(result.isSuccess, "Let reassignment should succeed")

        // Verify new value
        val printCall = createPrintLnCall("value")
        interpreter.execute(printCall)
        assertEquals(listOf("20"), outputSink.getOutput())
    }

    @Test
    fun `should reject type mismatch in declaration`() {
        // let number: number = "not a number";
        val identifier = createIdentifier("number")
        val declaration = DeclarationNode(identifier, createToken(TokenType.NumberType, "number"), true)
        val stringValue = createStringLiteral("not a number")
        val declarationNode = DeclarationAssignmentNode(declaration, stringValue)

        val result = interpreter.execute(declarationNode)

        assertTrue(result.isFailure, "Type mismatch should fail")
        assertTrue(result.errorOrNull() is TypeMismatchError)
    }

    @Test
    fun `should reject assignment type mismatch`() {
        // let text: string = "hello";
        val declaration = createStringDeclaration("text", "hello", true)
        interpreter.execute(declaration)

        // text = 42; // Should fail
        val assignment = createAssignment("text", createNumberLiteral(42.0))
        val result = interpreter.execute(assignment)

        assertTrue(result.isFailure, "Type mismatch assignment should fail")
        assertTrue(result.errorOrNull() is TypeMismatchError)
    }

    @Test
    fun `should reject undeclared variable access`() {
        val printCall = createPrintLnCall("undeclaredVar")
        val result = interpreter.execute(printCall)

        assertTrue(result.isFailure, "Undeclared variable should fail")
        assertTrue(result.errorOrNull() is UndeclaredVariableError)
    }

    @Test
    fun `should handle readInput with string parsing`() {
        inputProvider = ProgrammaticInputProvider(mutableListOf("user input"))
        interpreter =
            InterpreterRuntimeFactory().createRuntime(
                inputProvider,
                envProvider,
                outputSink,
            )

        // let input: string = readInput("Enter text: ");
        val readInputCall = createReadInputCall("Enter text: ")
        val declaration = createStringDeclarationWithValue("input", readInputCall, true)
        val result = interpreter.execute(declaration)

        assertTrue(result.isSuccess, "ReadInput should succeed")

        // printLn(input);
        val printCall = createPrintLnCall("input")
        interpreter.execute(printCall)
        assertEquals(listOf("user input"), outputSink.getOutput())
    }

    @Test
    fun `should handle readInput with number parsing`() {
        inputProvider = ProgrammaticInputProvider(mutableListOf("42.5"))
        interpreter =
            InterpreterRuntimeFactory().createRuntime(
                inputProvider,
                envProvider,
                outputSink,
            )

        // let num: number = readInput("Enter number: ");
        val readInputCall = createReadInputCall("Enter number: ")
        val declaration = createNumberDeclarationWithValue("num", readInputCall, true)
        val result = interpreter.execute(declaration)

        assertTrue(result.isSuccess, "ReadInput number should succeed")

        // printLn(num);
        val printCall = createPrintLnCall("num")
        interpreter.execute(printCall)
        assertEquals(listOf("42.5"), outputSink.getOutput())
    }

    @Test
    fun `should handle readEnv successfully`() {
        // let envVar: string = readEnv("TEST_VAR");
        val readEnvCall = createReadEnvCall("TEST_VAR")
        val declaration = createStringDeclarationWithValue("envVar", readEnvCall, true)
        val result = interpreter.execute(declaration)

        assertTrue(result.isSuccess, "ReadEnv should succeed")

        // printLn(envVar);
        val printCall = createPrintLnCall("envVar")
        interpreter.execute(printCall)
        assertEquals(listOf("test_value"), outputSink.getOutput())
    }

    @Test
    fun `should reject readEnv for missing variable`() {
        // let missing: string = readEnv("MISSING_VAR");
        val readEnvCall = createReadEnvCall("MISSING_VAR")
        val declaration = createStringDeclarationWithValue("missing", readEnvCall, true)
        val result = interpreter.execute(declaration)

        assertTrue(result.isFailure, "ReadEnv missing var should fail")
    }

    // Helper methods for creating AST nodes
    private fun createStringDeclaration(
        name: String,
        value: String,
        isMutable: Boolean,
    ): DeclarationAssignmentNode {
        val identifier = createIdentifier(name)
        val declaration = DeclarationNode(identifier, createToken(TokenType.StringType, "string"), isMutable)
        val valueNode = createStringLiteral(value)
        return DeclarationAssignmentNode(declaration, valueNode)
    }

    private fun createNumberDeclaration(
        name: String,
        value: Double,
        isMutable: Boolean,
    ): DeclarationAssignmentNode {
        val identifier = createIdentifier(name)
        val declaration = DeclarationNode(identifier, createToken(TokenType.NumberType, "number"), isMutable)
        val valueNode = createNumberLiteral(value)
        return DeclarationAssignmentNode(declaration, valueNode)
    }

    private fun createBooleanDeclaration(
        name: String,
        value: Boolean,
        isMutable: Boolean,
    ): DeclarationAssignmentNode {
        val identifier = createIdentifier(name)
        val declaration = DeclarationNode(identifier, createToken(TokenType.BooleanType, "boolean"), isMutable)
        val valueNode = createBooleanLiteral(value)
        return DeclarationAssignmentNode(declaration, valueNode)
    }

    private fun createStringDeclarationWithValue(
        name: String,
        value: FunctionCallNode,
        isMutable: Boolean,
    ): DeclarationAssignmentNode {
        val identifier = createIdentifier(name)
        val declaration = DeclarationNode(identifier, createToken(TokenType.StringType, "string"), isMutable)
        return DeclarationAssignmentNode(declaration, value)
    }

    private fun createNumberDeclarationWithValue(
        name: String,
        value: FunctionCallNode,
        isMutable: Boolean,
    ): DeclarationAssignmentNode {
        val identifier = createIdentifier(name)
        val declaration = DeclarationNode(identifier, createToken(TokenType.NumberType, "number"), isMutable)
        return DeclarationAssignmentNode(declaration, value)
    }

    private fun createAssignment(
        name: String,
        value: LiteralNode,
    ): AssignmentNode = AssignmentNode(createIdentifier(name), value)

    private fun createPrintLnCall(variableName: String): FunctionCallNode =
        FunctionCallNode("println", createVariableReference(variableName), true)

    private fun createReadInputCall(prompt: String): FunctionCallNode =
        FunctionCallNode(
            "readInput",
            createStringLiteral(prompt),
            false,
        )

    private fun createReadEnvCall(varName: String): FunctionCallNode =
        FunctionCallNode(
            "readEnv",
            createStringLiteral(varName),
            false,
        )

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
