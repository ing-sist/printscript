import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import shared.AnalyzerRuleDefinitions
import simple.SimpleArgRule
import utils.Type
import kotlin.test.assertTrue

class SimpleArgRuleTests {
    private fun cfgFrom(json: String): AnalyzerConfig {
        val tmp =
            kotlin.io.path
                .createTempFile("analyzer-", ".json")
                .toFile()
        tmp.writeText(json.trimIndent())
        return AnalyzerConfig.fromPath(tmp.path, AnalyzerRuleDefinitions.RULES)
    }

    private fun call(
        name: String,
        arg: AstNode,
    ) = FunctionCallNode(name, arg, false)

    @Test
    fun `println with identifier is OK`() {
        val ast = call("println", id("name"))
        val report = Report.inMemory()
        val rule = SimpleArgRule(PrintlnSimpleArgDef)

        val cfg =
            cfgFrom(
                """
            {
              "printlnSimpleArg": true,
              "readInputSimpleArg": true,
              "namingType": "camel"
            }
            """,
            )

        val diags = Analyzer(listOf(rule)).analyze(ast, report, cfg)
        assertTrue(diags.isEmpty(), "No debería reportar cuando el arg es Identifier")
    }

    @Test
    fun `println with literal is OK`() {
        val ast = call("println", litString("hola"))
        val report = Report.inMemory()
        val rule = SimpleArgRule(PrintlnSimpleArgDef)

        val cfg =
            cfgFrom(
                """
            {
              "printlnSimpleArg": true
            }
            """,
            )

        val diags = Analyzer(listOf(rule)).analyze(ast, report, cfg)
        assertTrue(diags.isEmpty(), "No debería reportar cuando el arg es Literal")
    }

    @Test
    fun `println with binary expression is NOT OK`() {
        val ast = call("println", bin(id("a"), "+", litNumber("1")))
        val report = Report.inMemory()
        val rule = SimpleArgRule(PrintlnSimpleArgDef)

        val cfg =
            cfgFrom(
                """
            {
              "printlnSimpleArg": true
            }
            """,
            )

        val diags = Analyzer(listOf(rule)).analyze(ast, report, cfg)

        assertEquals(1, diags.size(), "Debe reportar 1 diagnóstico")
        val d = diags.first()
        assertEquals(PrintlnSimpleArgDef.id, d.ruleId)
        assertEquals(Type.ERROR, d.type)
        assertTrue(d.message.contains("println"), "El mensaje debería mencionar a println")
    }

    @Test
    fun `println with unary expression is NOT OK`() {
        val ast = call("println", unary("-", id("x")))
        val report = Report.inMemory()
        val rule = SimpleArgRule(PrintlnSimpleArgDef)

        val cfg =
            cfgFrom(
                """
            {
              "printlnSimpleArg": true
            }
            """,
            )

        val diags = Analyzer(listOf(rule)).analyze(ast, report, cfg)
        assertEquals(1, diags.size(), "Debe reportar 1 diagnóstico")
        assertEquals(PrintlnSimpleArgDef.id, diags.first().ruleId)
    }

    @Test
    fun `disabled println rule does nothing`() {
        val ast = call("println", bin(id("a"), "+", litNumber("1")))
        val report = Report.inMemory()
        val rule = SimpleArgRule(PrintlnSimpleArgDef)

        val cfg =
            cfgFrom(
                """
            {
              "printlnSimpleArg": false
            }
            """,
            )

        val diags = Analyzer(listOf(rule)).analyze(ast, report, cfg)
        assertTrue(diags.isEmpty())
    }

    @Test
    fun `readinput with identifier is OK`() {
        val ast = call("readinput", id("target"))
        val report = Report.inMemory()
        val rule = SimpleArgRule(ReadInputSimpleArgDef)

        val cfg =
            cfgFrom(
                """
            {
              "readInputSimpleArg": true
            }
            """,
            )

        val diags = Analyzer(listOf(rule)).analyze(ast, report, cfg)
        assertTrue(diags.isEmpty(), "No debería reportar para Identifier")
    }

    @Test
    fun `disabled readinput rule does nothing`() {
        val ast = call("readinput", unary("-", id("x")))
        val report = Report.inMemory()
        val rule = SimpleArgRule(ReadInputSimpleArgDef)

        val cfg =
            cfgFrom(
                """
            {
              "readInputSimpleArg": false
            }
            """,
            )

        val diags = Analyzer(listOf(rule)).analyze(ast, report, cfg)
        assertTrue(diags.isEmpty(), "Con la regla deshabilitada no debería reportar")
    }
}
