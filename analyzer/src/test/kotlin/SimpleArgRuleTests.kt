import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import simple.SimpleArgConfig
import simple.SimpleArgRule
import kotlin.test.assertTrue

class SimpleArgRuleTests {
    private fun printlnRule(enabled: Boolean = false) =
        SimpleArgRule(
            SimpleArgConfig(
                enabled,
                Type.WARNING,
            ),
            PrintlnSimpleArgDef,
        )

    private fun readInputRule(enabled: Boolean = true) =
        SimpleArgRule(
            SimpleArgConfig(
                enabled,
                Type.WARNING,
            ),
            ReadInputSimpleArgDef,
        )

    fun call(
        name: String,
        arg: AstNode,
    ) = FunctionCallNode(name, arg, false)

    @Test
    fun `println with identifier is OK`() {
        val ast = call("println", id("name"))
        val report = Report()
        val rule = printlnRule(enabled = true)

        val diags = Analyzer(listOf(rule)).analyze(ast, report)
        assertTrue(diags.isEmpty(), "No debería reportar cuando el arg es Identifier")
    }

    @Test
    fun `println with literal is OK`() {
        val ast = call("println", litString("hola"))
        val report = Report()
        val rule = printlnRule(enabled = true)

        val diags = Analyzer(listOf(rule)).analyze(ast, report)
        assertTrue(diags.isEmpty(), "No debería reportar cuando el arg es Literal")
    }

    @Test
    fun `println with binary expression is NOT OK`() {
        val expr = bin(id("a"), "+", litNumber("1"))
        val ast = call("println", expr)
        val report = Report()
        val rule = printlnRule(enabled = true)

        val diags = Analyzer(listOf(rule)).analyze(ast, report)

        assertEquals(1, diags.size())
        val d = diags.first()

        // más robusto que hardcodear el string del id:
        assertEquals(PrintlnSimpleArgDef.id, d.ruleId)
        assertEquals(Type.WARNING, d.type)
        assertTrue(d.message.contains("println"), "El mensaje debería mencionar a println")
    }

    @Test
    fun `println with unary expression is NOT OK`() {
        val expr = unary("-", id("x"))
        val ast = call("println", expr)
        val rule = printlnRule(enabled = true)
        val report = Report()

        val diags = Analyzer(listOf(rule)).analyze(ast, report)
        assertEquals(1, diags.size())
        assertEquals(PrintlnSimpleArgDef.id, diags.first().ruleId)
    }

    @Test
    fun `disabled println rule does nothing`() {
        val ast = call("println", bin(id("a"), "+", litNumber("1")))
        val rule = printlnRule(enabled = false)
        val report = Report()

        val diags = Analyzer(listOf(rule)).analyze(ast, report)
        assertTrue(diags.isEmpty(), "Con la regla deshabilitada no debería reportar")
    }

    @Test
    fun `readinput with identifier is OK`() {
        val ast = call("readinput", id("target"))
        val rule = readInputRule()
        val report = Report()

        val diags = Analyzer(listOf(rule)).analyze(ast, report)
        assertTrue(diags.isEmpty())
    }

//    @Test
//    fun `readinput with expression is NOT OK`() {
//        val ast = call("readinput", bin(id("a"), "+", id("b")))
//        val rule = readInputRule(enabled = true)
//        val report = Report()
//
//        val diags = Analyzer(listOf(rule)).analyze(ast, report)
//        assertEquals(1, diags.size())
//        assertEquals(ReadInputSimpleArgDef.id, diags.first().ruleId)
//    }

    @Test
    fun `disabled readinput rule does nothing`() {
        val report = Report()
        val ast = call("readinput", unary("-", id("x")))
        val rule = readInputRule()

        val diags = Analyzer(listOf(rule)).analyze(ast, report)
        assertTrue(diags.isEmpty())
    }
}
