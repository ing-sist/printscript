// src/test/kotlin/language/errors/ErrorClassesMessageTest.kt
package language.errors

import language.types.PSType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ErrorClassesMessageTest {
    @Test
    fun `TypeMismatchError message format`() {
        val e = TypeMismatchError(PSType.NUMBER, PSType.STRING, "assignment")
        assertEquals("Type mismatch in assignment: expected NUMBER but got STRING", e.message)
    }

    @Test
    fun `ConstReassignmentError message format`() {
        val e = ConstReassignmentError("PI")
        assertEquals("Cannot reassign const variable 'PI'", e.message)
    }

    @Test
    fun `UndeclaredVariableError message format`() {
        val e = UndeclaredVariableError("x")
        assertEquals("Undeclared variable 'x'", e.message)
    }

    @Test
    fun `MissingEnvVarError message format`() {
        val e = MissingEnvVarError("PATH")
        assertEquals("Environment variable 'PATH' not found", e.message)
    }

    @Test
    fun `InputParseError message format`() {
        val e = InputParseError("abc", PSType.NUMBER)
        assertEquals("Cannot parse input 'abc' as NUMBER", e.message)
    }

    @Test
    fun `NoExpressionEvaluatorError message format`() {
        val e = NoExpressionEvaluatorError("WeirdExprNode")
        assertEquals("No expression evaluator registered for node type: WeirdExprNode", e.message)
    }

    @Test
    fun `NoStatementExecutorError message format`() {
        val e = NoStatementExecutorError("WeirdStmtNode")
        assertEquals("No statement executor registered for node type: WeirdStmtNode", e.message)
    }
}
