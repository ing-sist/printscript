// src/test/kotlin/runtime/core/DeclarationExecutorIntegrationTests.kt
package runtime.core

import AssignmentNode
import DeclarationNode
import FunctionCallNode
import IdentifierNode
import LiteralNode
import Location
import Token
import TokenType
import language.errors.ConstReassignmentError
import language.errors.InterpreterException
import language.errors.UndeclaredVariableError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import runtime.providers.BufferedOutputSink
import runtime.providers.MapEnvProvider
import runtime.providers.ProgrammaticInputProvider

class DeclarationExecutorTest {
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

    // -------------------
    // Declaraciones sin inicializador -> valores por defecto
    // -------------------

    @Test
    fun `should declare string with default empty and print it`() {
        // let s: string;
        val decl = createDeclarationOnly("s", TokenType.StringType, isMutable = true)
        val res = interpreter.execute(decl)
        assertTrue(res.isSuccess, "String declaration without initializer should succeed")

        // println(s) -> ""
        val printCall = createPrintLnCall("s")
        val printRes = interpreter.execute(printCall)
        assertTrue(printRes.isSuccess, "Print should succeed")
        assertEquals(listOf(""), outputSink.getOutput())
    }

    @Test
    fun `should declare number with default zero and print it`() {
        // let n: number;
        val decl = createDeclarationOnly("n", TokenType.NumberType, isMutable = true)
        val res = interpreter.execute(decl)
        assertTrue(res.isSuccess, "Number declaration without initializer should succeed")

        // println(n) -> "0"
        val printCall = createPrintLnCall("n")
        val printRes = interpreter.execute(printCall)
        assertTrue(printRes.isSuccess, "Print should succeed")
        assertEquals(listOf("0"), outputSink.getOutput()) // PSNumber(0.0) suele imprimirse como "0"
    }

    @Test
    fun `should declare boolean with default false and print it`() {
        // let b: boolean;
        val decl = createDeclarationOnly("b", TokenType.BooleanType, isMutable = true)
        val res = interpreter.execute(decl)
        assertTrue(res.isSuccess, "Boolean declaration without initializer should succeed")

        // println(b) -> "false"
        val printCall = createPrintLnCall("b")
        val printRes = interpreter.execute(printCall)
        assertTrue(printRes.isSuccess, "Print should succeed")
        assertEquals(listOf("false"), outputSink.getOutput())
    }

    // -------------------
    // Mutabilidad y const sin inicializador
    // -------------------

    @Test
    fun `should forbid reassigning const declared without initializer`() {
        // const PI: number;
        val decl = createDeclarationOnly("PI", TokenType.NumberType, isMutable = false)
        val res = interpreter.execute(decl)
        assertTrue(res.isSuccess, "Const declaration without initializer should succeed")

        // PI = 3.14 -> debe fallar por const
        val assign = createAssignment("PI", createNumberLiteral(3.14))
        val assignRes = interpreter.execute(assign)
        assertTrue(assignRes.isFailure, "Const reassignment should fail")
        assertTrue(assignRes.errorOrNull() is ConstReassignmentError)
    }

    @Test
    fun `should allow reassigning let declared without initializer`() {
        // let name: string;
        val decl = createDeclarationOnly("name", TokenType.StringType, isMutable = true)
        assertTrue(interpreter.execute(decl).isSuccess)

        // name = "Ceci"
        val assign = createAssignment("name", createStringLiteral("Ceci"))
        val assignRes = interpreter.execute(assign)
        assertTrue(assignRes.isSuccess, "Let reassignment should succeed")

        // println(name) -> "Ceci"
        val printCall = createPrintLnCall("name")
        assertTrue(interpreter.execute(printCall).isSuccess)
        assertEquals(listOf("Ceci"), outputSink.getOutput())
    }

    // -------------------
    // Tipo desconocido en declaración
    // -------------------

    @Test
    fun `should fail on unknown type in declaration without initializer`() {
        // let x: WeirdType;  (simulamos un token no soportado)
        val identifier = createIdentifier("x")
        val weirdTypeToken = createToken(TokenType.Identifier, "WeirdType")
        val decl = DeclarationNode(identifier, weirdTypeToken, true)

        val res = interpreter.execute(decl)
        assertTrue(res.isFailure, "Unknown type should fail")
        val err = res.errorOrNull()
        assertTrue(
            err is InterpreterException && err.message!!.contains("Unknown type"),
            "Must be InterpreterException('Unknown type: ...')",
        )
    }

    // -------------------
    // Acceso a variable no declarada, por robustez
    // -------------------

    @Test
    fun `should reject undeclared variable print even after unrelated declaration`() {
        // let ok: number;
        val decl = createDeclarationOnly("ok", TokenType.NumberType, isMutable = true)
        assertTrue(interpreter.execute(decl).isSuccess)

        // println(missing) -> debe fallar con UndeclaredVariableError
        val printCall = createPrintLnCall("missing")
        val res = interpreter.execute(printCall)
        assertTrue(res.isFailure, "Undeclared variable should fail")
        assertTrue(res.errorOrNull() is UndeclaredVariableError)
    }

    // -------------------------------------------------
    // Helpers (idénticos en estilo a tu test existente)
    // -------------------------------------------------

    private fun createDeclarationOnly(
        name: String,
        typeToken: TokenType,
        isMutable: Boolean,
    ): DeclarationNode {
        val identifier = createIdentifier(name)
        return DeclarationNode(identifier, createToken(typeToken, typeToken.toString()), isMutable)
    }

    private fun createAssignment(
        name: String,
        value: LiteralNode,
    ): AssignmentNode = AssignmentNode(createIdentifier(name), value)

    private fun createPrintLnCall(variableName: String): FunctionCallNode =
        FunctionCallNode("println", createVariableReference(variableName), true)

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

    private fun createToken(
        type: TokenType,
        lexeme: String,
    ): Token = Token(type, lexeme, Location(1, 1, 1))
}
