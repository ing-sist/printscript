package runtime.core

import language.types.DefaultTypeRules
import language.types.PSBoolean
import language.types.PSNumber
import language.types.PSString
import language.types.PSType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import runtime.providers.BufferedOutputSink
import runtime.providers.MapEnvProvider
import runtime.providers.ProgrammaticInputProvider

class EvaluationContextTest {
    private lateinit var evaluationContext: EvaluationContext
    private lateinit var variableStore: ScopedVariableStore
    private lateinit var outputSink: BufferedOutputSink

    @BeforeEach
    fun setUp() {
        variableStore = ScopedVariableStore()
        outputSink = BufferedOutputSink()
        evaluationContext =
            EvaluationContext(
                variableStore,
                ProgrammaticInputProvider(mutableListOf("test_input")),
                MapEnvProvider(mapOf("TEST_ENV" to "env_value")),
                outputSink,
                DefaultTypeRules(),
            )
    }

    @Test
    fun `context provides access to all components`() {
        assertEquals(variableStore, evaluationContext.variableStore)
        assertTrue(evaluationContext.inputProvider is ProgrammaticInputProvider)
        assertTrue(evaluationContext.envProvider is MapEnvProvider)
        assertEquals(outputSink, evaluationContext.outputSink)
        assertTrue(evaluationContext.typeRules is DefaultTypeRules)
    }

    @Test
    fun `context can be used for variable operations`() {
        val stringValue = PSString("test")

        // Declare a variable
        val declareResult = evaluationContext.variableStore.declareVariable("testVar", PSType.STRING, stringValue, true)
        assertTrue(declareResult.isSuccess)

        // Retrieve the variable
        val retrievedResult = evaluationContext.variableStore.getVariable("testVar")
        assertTrue(retrievedResult.isSuccess)
        assertEquals(stringValue, retrievedResult.getOrNull()?.value)
    }

    @Test
    fun `context integrates with input provider`() {
        val input = evaluationContext.inputProvider.readInput("Enter value: ")
        assertEquals("test_input", input)
    }

    @Test
    fun `context integrates with environment provider`() {
        val envValue = evaluationContext.envProvider.getEnvVariable("TEST_ENV")
        assertEquals("env_value", envValue)
    }

    @Test
    fun `context integrates with output sink`() {
        evaluationContext.outputSink.print("test output")
        assertEquals(listOf("test output"), outputSink.getOutput())
    }
}

class ScopedVariableStoreTest {
    private lateinit var store: ScopedVariableStore

    @BeforeEach
    fun setUp() {
        store = ScopedVariableStore()
    }

    @Test
    fun `can declare and retrieve variables in global scope`() {
        val value = PSNumber(42.0)

        val declareResult = store.declareVariable("number", PSType.NUMBER, value, true)
        assertTrue(declareResult.isSuccess)

        val retrieveResult = store.getVariable("number")
        assertTrue(retrieveResult.isSuccess)
        assertEquals(value, retrieveResult.getOrNull()?.value)
    }

    @Test
    fun `can check variable mutability`() {
        val value = PSBoolean(true)

        // Declare mutable variable
        store.declareVariable("mutable", PSType.BOOLEAN, value, true)
        val mutableBinding = store.getVariable("mutable").getOrNull()
        assertTrue(mutableBinding?.isMutable ?: false)

        // Declare immutable variable
        store.declareVariable("immutable", PSType.BOOLEAN, value, false)
        val immutableBinding = store.getVariable("immutable").getOrNull()
        assertFalse(immutableBinding?.isMutable ?: true)
    }

    @Test
    fun `can update mutable variables`() {
        val originalValue = PSNumber(10.0)
        val newValue = PSNumber(20.0)

        store.declareVariable("variable", PSType.NUMBER, originalValue, true)
        val updateResult = store.assignVariable("variable", newValue)

        assertTrue(updateResult.isSuccess)

        val retrievedBinding = store.getVariable("variable").getOrNull()
        assertEquals(newValue, retrievedBinding?.value)
    }

    @Test
    fun `cannot update immutable variables`() {
        val originalValue = PSString("original")
        val newValue = PSString("new")

        store.declareVariable("constant", PSType.STRING, originalValue, false)
        val updateResult = store.assignVariable("constant", newValue)

        assertTrue(updateResult.isFailure)
    }

    @Test
    fun `returns error for non-existent variables`() {
        val result = store.getVariable("nonexistent")
        assertTrue(result.isFailure)
    }

    @Test
    fun `can work with new scopes using withNewScope`() {
        val globalValue = PSString("global")
        val localValue = PSString("local")

        // Declare in global scope
        store.declareVariable("var", PSType.STRING, globalValue, true)

        // Test within new scope
        val scopeResult =
            store.withNewScope {
                // Declare in local scope with same name
                store.declareVariable("var", PSType.STRING, localValue, true)
                val localResult = store.getVariable("var")
                assertEquals(localValue, localResult.getOrNull()?.value)
                "scope_executed"
            }

        assertEquals("scope_executed", scopeResult)

        // After scope - should see global value again
        val globalResult = store.getVariable("var")
        assertEquals(globalValue, globalResult.getOrNull()?.value)
    }
}
