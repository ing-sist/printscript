import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class IdentifierNamingRulesTest {
    @Test
    fun `camelCase OK`() {
        val ast = id("userName") // camel válido
        val rule = IdentifierNamingRule(IdentifierCase.CAMEL)
        val diags = Linter(listOf(rule)).lint(ast)
        assertTrue(diags.isEmpty(), "No debería reportar para camelCase válido")
    }

    @Test
    fun `camelCase BAD when snake_case`() {
        val ast = id("user_name") // snake
        val rule = IdentifierNamingRule(IdentifierCase.CAMEL)
        val diags = Linter(listOf(rule)).lint(ast)

        assertEquals(1, diags.size)
        val d = diags.first()
        assertEquals("Naming.IdentifierStyle", d.ruleId)
        assertEquals(Type.WARNING, d.severity)
        assertTrue(d.message.contains("camelCase") && d.message.contains("user_name"))
        // ubicación: debería ser la del identificador
        assertEquals(ast.value.location.line, d.location.line)
    }

    @Test
    fun `snake_case OK`() {
        val ast = id("user_name")
        val rule = IdentifierNamingRule(IdentifierCase.SNAKE)
        val diags = Linter(listOf(rule)).lint(ast)
        assertTrue(diags.isEmpty())
    }

    @Test
    fun `snake_case BAD when camelCase`() {
        val ast = id("userName")
        val rule = IdentifierNamingRule(IdentifierCase.SNAKE)
        val diags = Linter(listOf(rule)).lint(ast)

        assertEquals(1, diags.size)
        val d = diags.first()
        assertEquals("Naming.IdentifierStyle", d.ruleId)
        assertTrue(d.message.contains("snake_case") && d.message.contains("userName"))
    }
}
