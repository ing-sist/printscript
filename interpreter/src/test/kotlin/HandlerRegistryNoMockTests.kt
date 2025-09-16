// src/test/kotlin/interpret/registry/HandlerRegistryNoMockTests.kt

import interpret.registry.ExpressionEvaluator
import interpret.registry.HandlerRegistry
import interpret.registry.StatementExecutor
import language.errors.InterpreterException
import language.errors.NoExpressionEvaluatorError
import language.errors.NoStatementExecutorError
import language.types.PSNumber
import language.types.PSString
import language.types.PSValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import runtime.core.EvaluationContext
import runtime.core.InterpreterRuntime
import runtime.core.InterpreterRuntimeFactory
import runtime.providers.BufferedOutputSink
import runtime.providers.MapEnvProvider
import runtime.providers.ProgrammaticInputProvider

class HandlerRegistryNoMockTests {
    // ---------- Helper: EvaluationContext real por reflexión ----------
    private fun testCtx(): EvaluationContext {
        val runtime =
            InterpreterRuntimeFactory().createRuntime(
                ProgrammaticInputProvider(mutableListOf()),
                MapEnvProvider(emptyMap()),
                BufferedOutputSink(),
            ) as InterpreterRuntime

        val field = InterpreterRuntime::class.java.getDeclaredField("evaluationContext")
        field.isAccessible = true
        return field.get(runtime) as EvaluationContext
    }

    private fun loc() = Location(1, 1, 1)

    // ---------- Sin registro: deben fallar con los errores específicos ----------

    @Test
    @DisplayName("evaluateExpression sin evaluator → NoExpressionEvaluatorError con el nombre del nodo")
    fun evaluateExpression_noEvaluatorRegistered() {
        val registry = HandlerRegistry()
        val node = IdentifierNode(Token(TokenType.Identifier, "x", loc()), "x")

        val res = registry.evaluateExpression(node, testCtx())

        assertTrue(res.isFailure)
        val err = res.errorOrNull()
        assertTrue(err is NoExpressionEvaluatorError)
        assertTrue(err!!.message!!.contains("IdentifierNode"))
    }

    @Test
    @DisplayName("executeStatement sin executor → NoStatementExecutorError con el nombre del nodo")
    fun executeStatement_noExecutorRegistered() {
        val registry = HandlerRegistry()
        val decl =
            DeclarationNode(
                IdentifierNode(Token(TokenType.Identifier, "n", loc()), "n"),
                Token(TokenType.NumberType, "number", loc()),
                true,
            )

        val res = registry.executeStatement(decl, testCtx())

        assertTrue(res.isFailure)
        val err = res.errorOrNull()
        assertTrue(err is NoStatementExecutorError)
        assertTrue(err!!.message!!.contains("DeclarationNode"))
    }

    // ---------- Éxitos: evaluador y ejecutor registrados ----------

    @Test
    @DisplayName("evaluateExpression con evaluator registrado retorna Success(PSValue)")
    fun evaluateExpression_success() {
        val registry = HandlerRegistry()

        // Evaluator simple que devuelve PSString("id:<nombre>")
        val evaluator =
            object : ExpressionEvaluator<IdentifierNode> {
                override fun evaluateExpression(
                    node: IdentifierNode,
                    context: EvaluationContext,
                ): Result<PSValue, InterpreterException> = Result.Success(PSString("id:${node.name}"))
            }
        registry.registerExpressionEvaluator(IdentifierNode::class.java, evaluator)

        val node = IdentifierNode(Token(TokenType.Identifier, "foo", loc()), "foo")
        val res = registry.evaluateExpression(node, testCtx())

        assertTrue(res.isSuccess)
        assertEquals("id:foo", (res.getOrNull() as PSString).value)
    }

    @Test
    @DisplayName("executeStatement con executor registrado retorna Success(Unit)")
    fun executeStatement_success() {
        val registry = HandlerRegistry()

        val executor =
            object : StatementExecutor<DeclarationNode> {
                override fun executeStatement(
                    node: DeclarationNode,
                    context: EvaluationContext,
                ): Result<Unit, InterpreterException> = Result.Success(Unit)
            }
        registry.registerStatementExecutor(DeclarationNode::class.java, executor)

        val decl =
            DeclarationNode(
                IdentifierNode(Token(TokenType.Identifier, "a", loc()), "a"),
                Token(TokenType.StringType, "string", loc()),
                true,
            )
        val res = registry.executeStatement(decl, testCtx())

        assertTrue(res.isSuccess)
        assertNull(res.errorOrNull())
    }

    // ---------- Propagación de InterpreterException desde evaluator / executor ----------

    @Test
    @DisplayName("evaluateExpression: evaluator lanza InterpreterException → Failure con la misma excepción")
    fun evaluateExpression_propagatesInterpreterException() {
        val registry = HandlerRegistry()

        val evaluator =
            object : ExpressionEvaluator<IdentifierNode> {
                override fun evaluateExpression(
                    node: IdentifierNode,
                    context: EvaluationContext,
                ): Result<PSValue, InterpreterException> = throw InterpreterException("boom expr")
            }
        registry.registerExpressionEvaluator(IdentifierNode::class.java, evaluator)

        val node = IdentifierNode(Token(TokenType.Identifier, "x", loc()), "x")
        val res = registry.evaluateExpression(node, testCtx())

        assertTrue(res.isFailure)
        val err = res.errorOrNull()
        assertTrue(err is InterpreterException)
        assertEquals("boom expr", err!!.message)
    }

    @Test
    @DisplayName("executeStatement: executor lanza InterpreterException → Failure con la misma excepción")
    fun executeStatement_propagatesInterpreterException() {
        val registry = HandlerRegistry()

        val executor =
            object : StatementExecutor<DeclarationNode> {
                override fun executeStatement(
                    node: DeclarationNode,
                    context: EvaluationContext,
                ): Result<Unit, InterpreterException> = throw InterpreterException("boom stmt")
            }
        registry.registerStatementExecutor(DeclarationNode::class.java, executor)

        val decl =
            DeclarationNode(
                IdentifierNode(Token(TokenType.Identifier, "n", loc()), "n"),
                Token(TokenType.NumberType, "number", loc()),
                true,
            )
        val res = registry.executeStatement(decl, testCtx())

        assertTrue(res.isFailure)
        val err = res.errorOrNull()
        assertTrue(err is InterpreterException)
        assertEquals("boom stmt", err!!.message)
    }

    @Test
    @DisplayName(
        "evaluateExpression: registrado para IdentifierNode," +
            " invocado con LiteralNode → NoExpressionEvaluatorError",
    )
    fun evaluateExpression_mismatchedNodeClass() {
        val registry = HandlerRegistry()

        val evaluator =
            object : ExpressionEvaluator<IdentifierNode> {
                override fun evaluateExpression(
                    node: IdentifierNode,
                    context: EvaluationContext,
                ): Result<PSValue, InterpreterException> = Result.Success(PSNumber(1.0))
            }
        registry.registerExpressionEvaluator(
            IdentifierNode::class.java,
            evaluator,
        )

        val lit =
            LiteralNode(
                Token(
                    TokenType.NumberLiteral,
                    "1",
                    loc(),
                ),
            )
        val res = registry.evaluateExpression(lit, testCtx())

        assertTrue(res.isFailure)
        assertTrue(res.errorOrNull() is NoExpressionEvaluatorError)
    }

    @Test
    @DisplayName(
        "executeStatement: registrado " +
            "para DeclarationNode, invocado con IdentifierNode → NoStatementExecutorError",
    )
    fun executeStatement_mismatchedNodeClass() {
        val registry = HandlerRegistry()

        val executor =
            object : StatementExecutor<DeclarationNode> {
                override fun executeStatement(
                    node: DeclarationNode,
                    context: EvaluationContext,
                ): Result<Unit, InterpreterException> = Result.Success(Unit)
            }
        registry.registerStatementExecutor(DeclarationNode::class.java, executor)

        val otherNode = IdentifierNode(Token(TokenType.Identifier, "x", loc()), "x")
        val res = registry.executeStatement(otherNode, testCtx())

        assertTrue(res.isFailure)
        assertTrue(res.errorOrNull() is NoStatementExecutorError)
    }
}
