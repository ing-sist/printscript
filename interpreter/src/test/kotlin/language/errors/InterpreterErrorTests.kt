// src/test/kotlin/runtime/core/InterpreterErrorTests.kt
package language.errors

import AssignmentNode
import DeclarationAssignmentNode
import DeclarationNode
import FunctionCallNode
import IdentifierNode
import LiteralNode
import Location
import Token
import TokenType
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import runtime.core.Interpreter
import runtime.core.InterpreterRuntimeFactory
import runtime.providers.BufferedOutputSink
import runtime.providers.MapEnvProvider
import runtime.providers.ProgrammaticInputProvider

class InterpreterErrorTests {
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
                    "TEST_VAR" to "ok",
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

    // --- TypeMismatchError: declaración con tipo NUMBER y literal STRING ---
    @Test
    fun `type mismatch en declaracion falla y el mensaje menciona expected y got`() {
        // let n: number = "hola";
        val identifier = createIdentifier("n")
        val decl = DeclarationNode(identifier, createToken(TokenType.NumberType, "number"), true)
        val strLit = createStringLiteral("hola")
        val declAssign = DeclarationAssignmentNode(decl, strLit)

        val res = interpreter.execute(declAssign)

        assertTrue(res.isFailure)
        val err = res.errorOrNull()
        assertTrue(err is TypeMismatchError)
        // Mensaje: "Type mismatch in ...: expected NUMBER but got STRING"
        val msg = err!!.message ?: ""
        assertTrue(msg.contains("Type mismatch"), msg)
        assertTrue(msg.contains("expected NUMBER"), msg)
        assertTrue(msg.contains("got STRING"), msg)
    }

    // --- TypeMismatchError: reasignación string <- number ---
    @Test
    fun `type mismatch en asignacion falla y el mensaje indica NUMBER y STRING`() {
        // let s: string = "a";
        val decl = createStringDeclaration("s", "a", true)
        assertTrue(interpreter.execute(decl).isSuccess)

        // s = 10;
        val assign = createAssignment("s", createNumberLiteral(10.0))
        val res = interpreter.execute(assign)

        assertTrue(res.isFailure)
        val err = res.errorOrNull()
        assertTrue(err is TypeMismatchError)
        val msg = err!!.message ?: ""
        assertTrue(msg.contains("Type mismatch"), msg)
        assertTrue(
            msg.contains("expected STRING") ||
                msg.contains("expected NUMBER"),
            msg,
        ) // por si el mensaje se arma desde el chequeo
    }

    // --- ConstReassignmentError ---
    @Test
    fun `reassign const produce error con nombre de variable`() {
        // const PI: number = 3.14;
        val decl = createNumberDeclaration("PI", 3.14, isMutable = false)
        assertTrue(interpreter.execute(decl).isSuccess)

        // PI = 2.71;
        val assign = createAssignment("PI", createNumberLiteral(2.71))
        val res = interpreter.execute(assign)

        assertTrue(res.isFailure)
        val err = res.errorOrNull()
        assertTrue(err is ConstReassignmentError)
        val msg = err!!.message ?: ""
        assertTrue(msg.contains("Cannot reassign const variable 'PI'"), msg)
    }

    // --- UndeclaredVariableError ---
    @Test
    fun `usar variable no declarada produce UndeclaredVariableError con el nombre`() {
        val printCall = createPrintLnCall("noExiste")
        val res = interpreter.execute(printCall)

        assertTrue(res.isFailure)
        val err = res.errorOrNull()
        assertTrue(err is UndeclaredVariableError)
        val msg = err!!.message ?: ""
        assertTrue(msg.contains("Undeclared variable 'noExiste'"), msg)
    }

    // --- MissingEnvVarError en readEnv ---
    @Test
    fun `readEnv de variable inexistente produce MissingEnvVarError`() {
        // let v: string = readEnv("MISSING_VAR");
        val readEnv = createReadEnvCall("MISSING_VAR")
        val decl = createStringDeclarationWithValue("v", readEnv, isMutable = true)

        val res = interpreter.execute(decl)

        assertTrue(res.isFailure)
        val err = res.errorOrNull()
        assertTrue(err is MissingEnvVarError)
        val msg = err!!.message ?: ""
        assertTrue(msg.contains("Environment variable 'MISSING_VAR' not found"), msg)
    }

    // --- InputParseError al intentar parsear número desde input no numérico ---
    @Test
    fun `readInput que no parsea a number produce InputParseError con el input`() {
        // re-creamos runtime con inputProvider = ["abc"]
        inputProvider = ProgrammaticInputProvider(mutableListOf("abc"))
        interpreter =
            InterpreterRuntimeFactory().createRuntime(
                inputProvider,
                envProvider,
                outputSink,
            )

        // let n: number = readInput("Ingrese numero");
        val readInput = createReadInputCall("Ingrese numero")
        val decl = createNumberDeclarationWithValue("n", readInput, isMutable = true)

        val res = interpreter.execute(decl)

        assertTrue(res.isFailure)
        val err = res.errorOrNull()
        assertTrue(err is InputParseError)
        val msg = err!!.message ?: ""
        assertTrue(msg.contains("Cannot parse input 'abc' as NUMBER"), msg)
    }

    // -------------------- Helpers (mismo estilo que tus tests) --------------------

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
        FunctionCallNode(
            "println",
            createIdentifier(variableName),
            true,
        )

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
            Token(
                TokenType.Identifier,
                name,
                loc(),
            ),
            name,
        )

    private fun createStringLiteral(value: String): LiteralNode =
        LiteralNode(
            Token(
                TokenType.StringLiteral,
                "\"$value\"",
                loc(),
            ),
        )

    private fun createNumberLiteral(value: Double): LiteralNode =
        LiteralNode(
            Token(
                TokenType.NumberLiteral,
                value.toString(),
                loc(),
            ),
        )

    private fun createToken(
        type: TokenType,
        lexeme: String,
    ): Token = Token(type, lexeme, loc())

    private fun loc() = Location(1, 1, 1)
}
