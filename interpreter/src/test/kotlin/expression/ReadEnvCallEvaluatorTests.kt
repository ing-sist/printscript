// src/test/kotlin/interpret/expression/ReadEnvCallEvaluatorTests.kt
package expression

import FunctionCallNode
import LiteralNode
import Location
import Token
import TokenType
import interpret.expression.ReadEnvCallEvaluator
import language.errors.InterpreterException
import language.errors.MissingEnvVarError
import language.types.PSBoolean
import language.types.PSNumber
import language.types.PSString
import language.types.PSType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import runtime.core.EvaluationContext
import runtime.core.InterpreterRuntime
import runtime.core.InterpreterRuntimeFactory
import runtime.providers.BufferedOutputSink
import runtime.providers.MapEnvProvider
import runtime.providers.ProgrammaticInputProvider

class ReadEnvCallEvaluatorTests {
    // ---------- Helpers ----------
    private fun loc() = Location(1, 1, 1)

    private fun runtimeWithEnv(env: Map<String, String>): Pair<InterpreterRuntime, EvaluationContext> {
        val rt =
            InterpreterRuntimeFactory().createRuntime(
                ProgrammaticInputProvider(mutableListOf()),
                MapEnvProvider(env),
                BufferedOutputSink(),
            ) as InterpreterRuntime

        val field = InterpreterRuntime::class.java.getDeclaredField("evaluationContext")
        field.isAccessible = true
        val ctx = field.get(rt) as EvaluationContext
        return rt to ctx
    }

    private fun strLit(value: String) = LiteralNode(Token(TokenType.StringLiteral, "\"$value\"", loc()))

    private fun numLit(value: String) = LiteralNode(Token(TokenType.NumberLiteral, value, loc()))

    // ---------- Tests ----------

    @Test
    @DisplayName("función distinta a readEnv → Failure con mensaje")
    fun notReadEnvFunction() {
        val (rt, ctx) = runtimeWithEnv(emptyMap())
        val evaluator = ReadEnvCallEvaluator(rt)
        val node = FunctionCallNode("println", strLit("ANY"), isVoid = false)

        val res = evaluator.evaluateExpression(node, ctx)

        assertTrue(res.isFailure)
        val err = res.errorOrNull()
        assertTrue(err is InterpreterException)
        assertTrue(err!!.message!!.contains("only handles readEnv"))
    }

    @Test
    @DisplayName("nombre no string (Number) → Failure 'variable name must be a string'")
    fun nameNotString() {
        val (rt, ctx) = runtimeWithEnv(emptyMap())
        val evaluator = ReadEnvCallEvaluator(rt)
        val node = FunctionCallNode("readEnv", numLit("1"), isVoid = false)

        val res = evaluator.evaluateExpression(node, ctx)

        assertTrue(res.isFailure)
        val err = res.errorOrNull()
        assertTrue(err is InterpreterException)
        assertTrue(err!!.message!!.contains("variable name must be a string"))
    }

    @Test
    @DisplayName("variable faltante → MissingEnvVarError")
    fun missingEnvVar() {
        val (rt, ctx) = runtimeWithEnv(mapOf("OTHER" to "x"))
        val evaluator = ReadEnvCallEvaluator(rt)
        val node = FunctionCallNode("readEnv", strLit("MISSING"), isVoid = false)

        val res = evaluator.evaluateExpression(node, ctx)

        assertTrue(res.isFailure)
        assertTrue(res.errorOrNull() is MissingEnvVarError)
    }

    @Test
    @DisplayName("sin expectedType → devuelve PSString con el valor del env")
    fun returnsStringWhenNoExpectedType() {
        val (rt, ctx) = runtimeWithEnv(mapOf("TEST_VAR" to "hello"))
        val evaluator = ReadEnvCallEvaluator(rt)
        val node = FunctionCallNode("readEnv", strLit("TEST_VAR"), isVoid = false)

        val res = evaluator.evaluateExpression(node, ctx)

        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSString
        assertEquals("hello", v.value)
    }

    @Test
    @DisplayName("expectedType=NUMBER → parsea PSNumber")
    fun parsesNumberWhenExpected() {
        val (rt, baseCtx) = runtimeWithEnv(mapOf("NUMBER_VAR" to "42.5"))
        val evaluator = ReadEnvCallEvaluator(rt)
        val node = FunctionCallNode("readEnv", strLit("NUMBER_VAR"), isVoid = false)

        // EvaluationContext tiene copy(expectedType=...)
        val ctx = baseCtx.copy(expectedType = PSType.NUMBER)

        val res = evaluator.evaluateExpression(node, ctx)

        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSNumber
        assertEquals(42.5, v.value)
    }

    @Test
    @DisplayName("expectedType=BOOLEAN → parsea PSBoolean")
    fun parsesBooleanWhenExpected() {
        val (rt, baseCtx) = runtimeWithEnv(mapOf("BOOL_VAR" to "true"))
        val evaluator = ReadEnvCallEvaluator(rt)
        val node = FunctionCallNode("readEnv", strLit("BOOL_VAR"), isVoid = false)

        val ctx = baseCtx.copy(expectedType = PSType.BOOLEAN)

        val res = evaluator.evaluateExpression(node, ctx)

        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSBoolean
        assertTrue(v.value)
    }

    @Test
    @DisplayName("parseRawTo falla (NUMBER esperado pero env='abc') → Failure")
    fun parseRawToFailure() {
        val (rt, baseCtx) = runtimeWithEnv(mapOf("BAD_NUMBER" to "abc"))
        val evaluator = ReadEnvCallEvaluator(rt)
        val node = FunctionCallNode("readEnv", strLit("BAD_NUMBER"), isVoid = false)

        val ctx = baseCtx.copy(expectedType = PSType.NUMBER)

        val res = evaluator.evaluateExpression(node, ctx)

        assertTrue(res.isFailure)
        val err = res.errorOrNull()
        assertNotNull(err) // probablemente InputParseError, pero no dependemos del tipo exacto aquí
    }
}
