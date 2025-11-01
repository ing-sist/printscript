// src/test/kotlin/interpret/expression/BinaryOperationEvaluatorTests.kt
package expression

import BinaryOperationNode
import LiteralNode
import Location
import Result
import Token
import TokenType
import interpret.expression.BinaryOperationEvaluator
import language.errors.InterpreterException
import language.errors.TypeMismatchError
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

class BinaryOperationEvaluatorTests {
    // ---------- Helpers ----------
    private fun loc() = Location(1, 1, 1)

    private fun runtimeAndCtx(): Pair<InterpreterRuntime, EvaluationContext> {
        val rt =
            InterpreterRuntimeFactory().createRuntime(
                ProgrammaticInputProvider(mutableListOf()),
                MapEnvProvider(emptyMap()),
                BufferedOutputSink(),
            ) as InterpreterRuntime
        val field = InterpreterRuntime::class.java.getDeclaredField("evaluationContext")
        field.isAccessible = true
        val ctx = field.get(rt) as EvaluationContext
        return rt to ctx
    }

    private fun num(n: String) = LiteralNode(Token(TokenType.NumberLiteral, n, loc()))

    private fun str(s: String) = LiteralNode(Token(TokenType.StringLiteral, "\"$s\"", loc()))

    private fun bool(b: Boolean) = LiteralNode(Token(TokenType.BooleanLiteral, b.toString(), loc()))

    private fun op(t: TokenType) = Token(t, t.toString(), loc())

    private fun eval(node: BinaryOperationNode): Result<*, InterpreterException> {
        val (rt, ctx) = runtimeAndCtx()
        val evaluator = BinaryOperationEvaluator(rt)
        return evaluator.evaluateExpression(node, ctx)
    }

    // ---------- Sumas ----------

    @Test
    @DisplayName("NUMBER + NUMBER → PSNumber( suma )")
    fun addition_numbers() {
        val node = BinaryOperationNode(num("2"), op(TokenType.Operator.Plus), num("3"))
        val res = eval(node)
        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSNumber
        assertEquals(5.0, v.value)
    }

    @Test
    @DisplayName("STRING + NUMBER → PSString concatenado")
    fun addition_string_number_concatenation() {
        val node = BinaryOperationNode(str("A"), op(TokenType.Operator.Plus), num("7"))
        val res = eval(node)
        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSString
        assertEquals("A7", v.value)
    }

    @Test
    @DisplayName("STRING + STRING → PSString concatenado")
    fun addition_string_string_concatenation() {
        val node = BinaryOperationNode(str("Hello "), op(TokenType.Operator.Plus), str("World"))
        val res = eval(node)
        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSString
        assertEquals("Hello World", v.value)
    }

    @Test
    @DisplayName("BOOLEAN + NUMBER → TypeMismatchError (no concat ni suma)")
    fun addition_type_mismatch_boolean_number() {
        val node = BinaryOperationNode(bool(true), op(TokenType.Operator.Plus), num("1"))
        val res = eval(node)
        assertTrue(res.isFailure)
        assertTrue(res.errorOrNull() is TypeMismatchError)
    }

    // ---------- Aritmética: -, *, / ----------

    @Test
    @DisplayName("NUMBER - NUMBER → PSNumber")
    fun subtraction_numbers() {
        val node = BinaryOperationNode(num("10"), op(TokenType.Operator.Minus), num("3"))
        val res = eval(node)
        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSNumber
        assertEquals(7.0, v.value)
    }

    @Test
    @DisplayName("NUMBER * NUMBER → PSNumber")
    fun multiply_numbers() {
        val node = BinaryOperationNode(num("3"), op(TokenType.Operator.Multiply), num("4"))
        val res = eval(node)
        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSNumber
        assertEquals(12.0, v.value)
    }

    @Test
    @DisplayName("NUMBER / NUMBER → PSNumber (double)")
    fun divide_numbers() {
        val node = BinaryOperationNode(num("9"), op(TokenType.Operator.Divide), num("2"))
        val res = eval(node)
        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSNumber
        assertEquals(4.5, v.value)
    }

    @Test
    @DisplayName("aritmética con STRING → TypeMismatchError")
    fun arithmetic_type_mismatch() {
        val node = BinaryOperationNode(str("1"), op(TokenType.Operator.Minus), num("1"))
        val res = eval(node)
        assertTrue(res.isFailure)
        assertTrue(res.errorOrNull() is TypeMismatchError)
    }

    // ---------- Igualdad ----------

    @Test
    @DisplayName("3 == 3 → true")
    fun equals_numbers_true() {
        val node = BinaryOperationNode(num("3"), op(TokenType.Operator.Equals), num("3"))
        val res = eval(node)
        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSBoolean
        assertTrue(v.value)
    }

    @Test
    @DisplayName("true != false → true")
    fun not_equals_booleans_true() {
        val node = BinaryOperationNode(bool(true), op(TokenType.Operator.NotEquals), bool(false))
        val res = eval(node)
        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSBoolean
        assertTrue(v.value)
    }

    @Test
    @DisplayName("\"1\" == 1 (tipos distintos) → false")
    fun equals_cross_type_false() {
        val node = BinaryOperationNode(str("1"), op(TokenType.Operator.Equals), num("1"))
        val res = eval(node)
        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSBoolean
        assertFalse(v.value)
    }

    // ---------- Comparaciones ----------

    @Test
    @DisplayName("2 < 3 → true")
    fun less_than_true() {
        val node = BinaryOperationNode(num("2"), op(TokenType.Operator.LessThan), num("3"))
        val res = eval(node)
        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSBoolean
        assertTrue(v.value)
    }

    @Test
    @DisplayName("3 <= 3 → true")
    fun less_than_equal_true() {
        val node = BinaryOperationNode(num("3"), op(TokenType.Operator.LessThanOrEqual), num("3"))
        val res = eval(node)
        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSBoolean
        assertTrue(v.value)
    }

    @Test
    @DisplayName("5 > 2 → true")
    fun greater_than_true() {
        val node = BinaryOperationNode(num("5"), op(TokenType.Operator.GreaterThan), num("2"))
        val res = eval(node)
        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSBoolean
        assertTrue(v.value)
    }

    @Test
    @DisplayName("5 >= 5 → true")
    fun greater_equal_true() {
        val node = BinaryOperationNode(num("5"), op(TokenType.Operator.GreaterThanOrEqual), num("5"))
        val res = eval(node)
        assertTrue(res.isSuccess)
        val v = res.getOrNull() as PSBoolean
        assertTrue(v.value)
    }

    @Test
    @DisplayName("comparación con STRING → TypeMismatchError")
    fun comparison_type_mismatch() {
        val node = BinaryOperationNode(str("a"), op(TokenType.Operator.LessThan), num("1"))
        val res = eval(node)
        assertTrue(res.isFailure)
        assertTrue(res.errorOrNull() is TypeMismatchError)
    }

    // ---------- Operador no soportado ----------

    @Test
    @DisplayName("operador no soportado → Failure con mensaje")
    fun unsupported_operator_failure() {
        val node = BinaryOperationNode(num("1"), op(TokenType.Semicolon), num("2"))
        val res = eval(node)
        assertTrue(res.isFailure)
        val err = res.errorOrNull()
        assertTrue(err is InterpreterException)
        assertTrue(err!!.message!!.contains("Unsupported binary operator"))
    }
}
