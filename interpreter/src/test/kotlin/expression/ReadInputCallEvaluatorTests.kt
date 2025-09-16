// src/test/kotlin/interpret/expression/ReadInputCallEvaluatorTests.kt
package expression

import FunctionCallNode
import LiteralNode
import Location
import Token
import TokenType
import interpret.expression.ReadInputCallEvaluator
import language.errors.InterpreterException
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

class ReadInputCallEvaluatorTests {
    // ---------- Helpers ----------
    private fun loc() = Location(1, 1, 1)

    private fun runtimeWithInputs(inputs: MutableList<String>): Pair<InterpreterRuntime, EvaluationContext> {
        val rt =
            InterpreterRuntimeFactory().createRuntime(
                ProgrammaticInputProvider(inputs),
                MapEnvProvider(emptyMap()),
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
    @DisplayName("función distinta a readInput → Failure con mensaje claro")
    fun notReadInputFunction() {
        val (rt, ctx) = runtimeWithInputs(mutableListOf("whatever"))
        val evaluator = ReadInputCallEvaluator(rt)
        val node = FunctionCallNode("println", strLit("prompt"), isVoid = false)

        val res = evaluator.evaluateExpression(node, ctx)

        assertTrue(res.isFailure)
        val err = res.errorOrNull()
        assertTrue(err is InterpreterException)
        assertTrue(err!!.message!!.contains("only handles readInput"))
    }

    @Test
    @DisplayName("prompt no string (Number) → Failure 'prompt must be a string'")
    fun promptNotString() {
        val (rt, ctx) = runtimeWithInputs(mutableListOf("42"))
        val evaluator = ReadInputCallEvaluator(rt)
        val node = FunctionCallNode("readInput", numLit("1"), isVoid = false)

        val res = evaluator.evaluateExpression(node, ctx)

        assertTrue(res.isFailure)
        val err = res.errorOrNull()
        assertTrue(err is InterpreterException)
        assertTrue(err!!.message!!.contains("prompt must be a string"))
    }

    @Test
    @DisplayName("sin expectedType → devuelve PSString con lo leído")
    fun returnsStringWhenNoExpectedType() {
        val (rt, ctx) = runtimeWithInputs(mutableListOf("user input 1"))
        val evaluator = ReadInputCallEvaluator(rt)
        val node = FunctionCallNode("readInput", strLit("Enter: "), isVoid = false)

        val res = evaluator.evaluateExpression(node, ctx)

        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSString
        assertEquals("user input 1", v.value)
    }

    @Test
    @DisplayName("expectedType=NUMBER → parsea PSNumber")
    fun parsesNumberWhenExpected() {
        val (rt, baseCtx) = runtimeWithInputs(mutableListOf("42.5"))
        val evaluator = ReadInputCallEvaluator(rt)
        val node = FunctionCallNode("readInput", strLit("Enter number: "), isVoid = false)

        val ctx = baseCtx.copy(expectedType = PSType.NUMBER)

        val res = evaluator.evaluateExpression(node, ctx)

        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSNumber
        assertEquals(42.5, v.value)
    }

    @Test
    @DisplayName("expectedType=BOOLEAN → parsea PSBoolean")
    fun parsesBooleanWhenExpected() {
        val (rt, baseCtx) = runtimeWithInputs(mutableListOf("true"))
        val evaluator = ReadInputCallEvaluator(rt)
        val node = FunctionCallNode("readInput", strLit("Enter boolean: "), isVoid = false)

        val ctx = baseCtx.copy(expectedType = PSType.BOOLEAN)

        val res = evaluator.evaluateExpression(node, ctx)

        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSBoolean
        assertTrue(v.value)
    }

    @Test
    @DisplayName("parseRawTo falla (NUMBER esperado pero input='abc') → Failure")
    fun parseRawToFailure() {
        val (rt, baseCtx) = runtimeWithInputs(mutableListOf("abc"))
        val evaluator = ReadInputCallEvaluator(rt)
        val node = FunctionCallNode("readInput", strLit("Enter number: "), isVoid = false)

        val ctx = baseCtx.copy(expectedType = PSType.NUMBER)

        val res = evaluator.evaluateExpression(node, ctx)

        assertTrue(res.isFailure)
        assertNotNull(res.errorOrNull()) // suele ser InputParseError, no dependemos del tipo exacto
    }

    @Test
    @DisplayName("inputProvider vacío → Failure con 'Input provider error: ...'")
    fun inputProviderError() {
        val (rt, ctx) = runtimeWithInputs(mutableListOf()) // vacío provoca IllegalStateException en readInput()
        val evaluator = ReadInputCallEvaluator(rt)
        val node = FunctionCallNode("readInput", strLit("Enter: "), isVoid = false)

        val res = evaluator.evaluateExpression(node, ctx)

        assertTrue(res.isFailure)
        val err = res.errorOrNull()
        assertTrue(err is InterpreterException)
        assertTrue(err!!.message!!.contains("Input provider error"))
    }
}
