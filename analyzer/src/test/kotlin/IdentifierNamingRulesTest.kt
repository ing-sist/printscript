import naming.IdentifierCase
import naming.IdentifierNamingRule
import naming.IdentifierNamingRuleDef
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import shared.AnalyzerRuleDefinitions
import utils.Type

class IdentifierNamingRulesTest {
    private fun cfgFrom(json: String): AnalyzerConfig {
        val tmp =
            kotlin.io.path
                .createTempFile("analyzer-", ".json")
                .toFile()
        tmp.writeText(json.trimIndent())
        return AnalyzerConfig.fromPath(tmp.path, AnalyzerRuleDefinitions.RULES)
    }

    private fun analyzerWithNaming(jsonConfig: String) =
        Analyzer(listOf(IdentifierNamingRule(IdentifierNamingRuleDef))) to
            cfgFrom(jsonConfig)

    @Test
    fun `camelCase OK`() {
        val (analyzer, cfg) =
            analyzerWithNaming(
                """
            { "identifierNamingStyle": "camel" }
            """,
            )
        val ast = id("userName")
        val report = Report.inMemory()

        val out = analyzer.analyze(ast, report, cfg)
        assertTrue(out.isEmpty(), "No debería reportar para camelCase válido")
    }

    @Test
    fun `camelCase BAD when snake_case`() {
        val (analyzer, cfg) =
            analyzerWithNaming(
                """
            { "identifierNamingStyle": "camel" }
            """,
            )
        val ast = id("user_name") // snake
        val report = Report.inMemory()

        val out = analyzer.analyze(ast, report, cfg)

        assertEquals(1, out.size())
        val d = out.first()
        assertEquals(IdentifierNamingRuleDef.id, d.ruleId)
        assertEquals(Type.WARNING, d.type)

        assertTrue(
            d.message.contains(IdentifierCase.CAMEL_CASE.description()),
            "El mensaje debe mencionar camelCase",
        )
        assertTrue(
            d.message.contains("user_name"),
            "El mensaje debe mencionar el identificador observado",
        )

        assertEquals(ast.value.location.line, d.location.line)
    }

    @Test
    fun `snake_case OK`() {
        val (analyzer, cfg) =
            analyzerWithNaming(
                """
            { "identifierNamingType": "snake" }
            """,
            )
        val ast = id("user_name")
        val report = Report.inMemory()

        val out = analyzer.analyze(ast, report, cfg)
        assertTrue(out.isEmpty(), "No debería reportar para snake_case válido")
    }

    @Test
    fun `snake_case BAD when camelCase`() {
        val (analyzer, cfg) =
            analyzerWithNaming(
                """
            { "identifierNamingType": "snake" }
            """,
            )
        val ast = id("userName")
        val report = Report.inMemory()

        val out = analyzer.analyze(ast, report, cfg)

        assertEquals(1, out.size())
        val d = out.first()
        assertEquals(IdentifierNamingRuleDef.id, d.ruleId)
        assertEquals(Type.WARNING, d.type)
        assertTrue(
            d.message.contains(IdentifierCase.SNAKE_CASE.description()),
            "El mensaje debe mencionar snake_case",
        )
        assertTrue(
            d.message.contains("userName"),
            "El mensaje debe mencionar el identificador observado",
        )
    }
}
