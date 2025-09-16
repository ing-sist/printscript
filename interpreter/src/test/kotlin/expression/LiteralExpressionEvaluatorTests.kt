// src/test/kotlin/interpret/expression/LiteralExpressionEvaluatorTests.kt
package expression

import LiteralNode
import Location
import Token
import TokenType
import interpret.expression.LiteralExpressionEvaluator
import language.errors.InterpreterException
import language.types.PSBoolean
import language.types.PSNumber
import language.types.PSString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import runtime.core.EvaluationContext
import runtime.core.InterpreterRuntime
import runtime.core.InterpreterRuntimeFactory
import runtime.providers.BufferedOutputSink
import runtime.providers.MapEnvProvider
import runtime.providers.ProgrammaticInputProvider

class LiteralExpressionEvaluatorTests {
    // ------- Helper: EvaluationContext real por reflexión -------
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

    @Test
    @DisplayName("NumberLiteral válido → PSNumber con el valor correcto")
    fun numberLiteral_ok() {
        val evaluator = LiteralExpressionEvaluator()
        val node = LiteralNode(Token(TokenType.NumberLiteral, "42.5", loc()))

        val res = evaluator.evaluateExpression(node, testCtx())

        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSNumber
        assertEquals(42.5, v.value)
    }

    @Test
    @DisplayName("StringLiteral con comillas → PSString sin comillas")
    fun stringLiteral_stripsQuotes() {
        val evaluator = LiteralExpressionEvaluator()
        val node = LiteralNode(Token(TokenType.StringLiteral, "\"Hello\"", loc()))

        val res = evaluator.evaluateExpression(node, testCtx())

        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSString
        assertEquals("Hello", v.value)
    }

    @Test
    @DisplayName("BooleanLiteral 'true' → PSBoolean(true)")
    fun booleanLiteral_true_tokenTypeBooleanLiteral() {
        val evaluator = LiteralExpressionEvaluator()
        val node = LiteralNode(Token(TokenType.BooleanLiteral, "true", loc()))

        val res = evaluator.evaluateExpression(node, testCtx())

        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSBoolean
        assertTrue(v.value)
    }

    @Test
    @DisplayName("Identifier 'FALSE' (mayúsculas) → PSBoolean(false)")
    fun booleanLiteral_false_tokenTypeIdentifier_uppercase() {
        val evaluator = LiteralExpressionEvaluator()
        // Forzamos la rama 'else' + lowercase() con IDENTIFIER en mayúsculas
        val node = LiteralNode(Token(TokenType.Identifier, "FALSE", loc()))

        val res = evaluator.evaluateExpression(node, testCtx())

        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSBoolean
        assertFalse(v.value)
    }

    @Test
    @DisplayName("Literal desconocido (Identifier 'maybe') → Failure Unknown literal type")
    fun unknownLiteral_failure() {
        val evaluator = LiteralExpressionEvaluator()
        val node = LiteralNode(Token(TokenType.Identifier, "maybe", loc()))

        val res = evaluator.evaluateExpression(node, testCtx())

        assertTrue(res.isFailure)
        val err = res.errorOrNull()
        assertTrue(err is InterpreterException)
        assertTrue(err!!.message!!.contains("Unknown literal type: maybe"))
    }

    @Test
    @DisplayName("NumberLiteral con formato inválido → Failure Number format error")
    fun numberLiteral_badFormat_failure() {
        val evaluator = LiteralExpressionEvaluator()
        // '12a' provoca NumberFormatException en toDouble()
        val node = LiteralNode(Token(TokenType.NumberLiteral, "12a", loc()))

        val res = evaluator.evaluateExpression(node, testCtx())

        assertTrue(res.isFailure)
        val err = res.errorOrNull()
        assertTrue(err is InterpreterException)
        assertTrue(err!!.message!!.contains("Number format error in literal"))
    }
}
