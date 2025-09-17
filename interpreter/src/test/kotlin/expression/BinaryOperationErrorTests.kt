// src/test/kotlin/expression/BinaryOperationErrorTests.kt
package expression

import AstNode
import BinaryOperationNode
import DeclarationAssignmentNode
import DeclarationNode
import IdentifierNode
import LiteralNode
import Location
import Token
import TokenType
import language.errors.TypeMismatchError
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import runtime.core.Interpreter
import runtime.core.InterpreterRuntime
import runtime.core.InterpreterRuntimeFactory
import runtime.providers.BufferedOutputSink
import runtime.providers.MapEnvProvider
import runtime.providers.ProgrammaticInputProvider

class BinaryOperationErrorTests {
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
        // sanity
        assertTrue(interpreter is InterpreterRuntime)
    }

    @Test
    fun `plus with invalid types (boolean + number) fails with TypeMismatchError`() {
        // let x: number = true + 2;
        val expr =
            createBinaryOp(
                left = createBooleanLiteral(true),
                op = TokenType.Plus,
                right = createNumberLiteral(2.0),
            )
        val decl = createDeclarationWithExpr("x", TokenType.NumberType, isMutable = true, expr = expr)

        val res = interpreter.execute(decl)

        assertTrue(res.isFailure, "Expected failure for boolean + number")
        assertTrue(res.errorOrNull() is TypeMismatchError)
    }

    @Test
    fun `multiply with invalid types (string * number) fails with TypeMismatchError`() {
        // let y: number = "3" * 2;
        val expr =
            createBinaryOp(
                left = createStringLiteral("3"),
                op = TokenType.Multiply,
                right = createNumberLiteral(2.0),
            )
        val decl = createDeclarationWithExpr("y", TokenType.NumberType, isMutable = true, expr = expr)

        val res = interpreter.execute(decl)

        assertTrue(res.isFailure, "Expected failure for string * number")
        assertTrue(res.errorOrNull() is TypeMismatchError)
    }

    @Test
    fun `comparison with invalid types (string less-than number) fails with TypeMismatchError`() {
        // let b: boolean = "a" < 1;
        val expr =
            createBinaryOp(
                left = createStringLiteral("a"),
                op = TokenType.LessThan,
                right = createNumberLiteral(1.0),
            )
        val decl = createDeclarationWithExpr("b", TokenType.BooleanType, isMutable = true, expr = expr)

        val res = interpreter.execute(decl)

        assertTrue(res.isFailure, "Expected failure for string < number")
        assertTrue(res.errorOrNull() is TypeMismatchError)
    }

    // ----------------- Helpers (idéntico estilo a tus tests) -----------------

    private fun createDeclarationWithExpr(
        name: String,
        typeToken: TokenType,
        isMutable: Boolean,
        expr: AstNode, // Expression node
    ): DeclarationAssignmentNode {
        val id = IdentifierNode(Token(TokenType.Identifier, name, loc()), name)
        val type = Token(typeToken, typeToken.toString(), loc())
        val decl = DeclarationNode(id, type, isMutable)
        return DeclarationAssignmentNode(decl, expr)
    }

    private fun createBinaryOp(
        left: AstNode,
        op: TokenType,
        right: AstNode,
    ): BinaryOperationNode {
        val opTok = Token(op, op.toString(), loc())
        // Ajustá el orden de parámetros si tu BinaryOperationNode difiere
        return BinaryOperationNode(left, opTok, right)
    }

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

    private fun createBooleanLiteral(value: Boolean): LiteralNode =
        // usa BooleanLiteral si tu AST lo define así; si no, ajustá al que uses
        LiteralNode(Token(TokenType.BooleanLiteral, value.toString(), loc()))

    private fun loc() = Location(1, 1, 1)
}
