
import naming.IdentifierCase
import naming.IdentifierNamingConfig
import naming.IdentifierNamingRule
import naming.IdentifierNamingRuleDef
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class IdentifierNamingRulesTest {
    private fun rule(expected: IdentifierCase) =
        IdentifierNamingRule(
            IdentifierNamingConfig(namingType = expected),
            IdentifierNamingRuleDef(),
        )

    @Test
    fun `camelCase OK`() {
        val ast = id("userName") // camel válido
        val report = Report.inMemory()
        val finalReport: Report = Analyzer(listOf(rule(IdentifierCase.CAMEL_CASE))).analyze(ast, report)
        assertTrue(finalReport.isEmpty(), "No debería reportar para camelCase válido")
    }

    @Test
    fun `camelCase BAD when snake_case`() {
        val ast = id("user_name") // snake
        val report = Report.inMemory()
        val finalReport: Report = Analyzer(listOf(rule(IdentifierCase.CAMEL_CASE))).analyze(ast, report)

        assertEquals(1, finalReport.size())
        val d = finalReport.first()
        assertEquals("Naming.IdentifierStyle", d.ruleId)
        assertEquals(Type.WARNING, d.type)

        // Mensaje debe mencionar expectativa (camelCase) y el nombre observado:
        assertTrue(
            d.message.contains(IdentifierCase.CAMEL_CASE.description()),
            "Identifiers are expected in CamelCase",
        )
        assertTrue(
            d.message.contains("user_name"),
            "Identifiers must follow the configured naming style",
        )

        // Ubicación: la del identificador
        assertEquals(ast.value.location.line, d.location.line)
    }

    @Test
    fun `snake_case OK`() {
        val report = Report.inMemory()
        val ast = id("user_name")
        val finalReport: Report = Analyzer(listOf(rule(IdentifierCase.SNAKE_CASE))).analyze(ast, report)
        assertTrue(finalReport.isEmpty(), "No debería reportar para snake_case válido")
    }

    @Test
    fun `snake_case BAD when camelCase`() {
        val ast = id("userName") // camel
        val report = Report.inMemory()
        val finalReport: Report = Analyzer(listOf(rule(IdentifierCase.SNAKE_CASE))).analyze(ast, report)

        assertEquals(1, finalReport.size())
        val d = finalReport.first()
        assertEquals("Naming.IdentifierStyle", d.ruleId)
        assertEquals(Type.WARNING, d.type)
        assertTrue(
            d.message.contains(IdentifierCase.SNAKE_CASE.description()),
            "El mensaje debe mencionar la expectativa snake_case",
        )
        assertTrue(
            d.message.contains("userName"),
            "Identifiers must follow the configured naming style",
        )
    }
}
