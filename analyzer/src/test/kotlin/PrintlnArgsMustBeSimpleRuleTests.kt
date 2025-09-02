import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PrintlnArgsMustBeSimpleRuleTests {
    @Test
    fun `println with identifier is OK`() {
        val ast = printlnOf(id("name"))
        val rule = PrintlnArgsMustBeSimpleRule(enabled = true)
        val diags = Linter(listOf(rule)).lint(ast)
        assertTrue(diags.isEmpty())
    }

    @Test
    fun `println with literal is OK`() {
        val ast = printlnOf(litString("hola"))
        val rule = PrintlnArgsMustBeSimpleRule(enabled = true)
        val diags = Linter(listOf(rule)).lint(ast)
        assertTrue(diags.isEmpty())
    }

    @Test
    fun `println with binary expression is NOT OK`() {
        val expr = bin(id("a"), "+", litNumber("1"))
        val ast = printlnOf(expr)
        val rule = PrintlnArgsMustBeSimpleRule(enabled = true)
        val diags = Linter(listOf(rule)).lint(ast)

        assertEquals(1, diags.size)
        val d = diags.first()
        assertEquals("Println.ArgSimple", d.ruleId)
        assertEquals(Type.WARNING, d.severity)
        assertTrue(d.message.contains("println"))
    }

    @Test
    fun `println with unary expression is NOT OK`() {
        val expr = unary("-", id("x"))
        val ast = printlnOf(expr)
        val rule = PrintlnArgsMustBeSimpleRule(enabled = true)
        val diags = Linter(listOf(rule)).lint(ast)
        assertEquals(1, diags.size)
    }

    @Test
    fun `disabled rule does nothing`() {
        val ast = printlnOf(bin(id("a"), "+", litNumber("1")))
        val rule = PrintlnArgsMustBeSimpleRule(enabled = false)
        val diags = Linter(listOf(rule)).lint(ast)
        assertTrue(diags.isEmpty())
    }
}
