import naming.IdentifierNamingRule
import naming.IdentifierNamingRuleDef
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import shared.AnalyzerRuleDefinitions
import simple.SimpleArgRule

class AnalyzerConfigLoadingTests {
    private fun cfgFrom(
        json: String,
        defs: List<shared.RuleDefinition<out shared.RuleConfig>>,
    ): AnalyzerConfig {
        val tmp =
            kotlin.io.path
                .createTempFile("analyzer-", ".json")
                .toFile()
        tmp.writeText(json.trimIndent())
        return AnalyzerConfig.fromPath(tmp.path, defs)
    }

    @Test
    fun `empty JSON disables all rules`() {
        val analyzer =
            Analyzer(
                listOf(
                    IdentifierNamingRule(IdentifierNamingRuleDef),
                    SimpleArgRule(PrintlnSimpleArgDef),
                ),
            )

        // Would violate both if rules were applied:
        val idAst = id("user_name") // not camelCase
        val printlnAst = FunctionCallNode("println", bin(id("a"), "+", litNumber("1")), false)

        val cfg = cfgFrom("{}", AnalyzerRuleDefinitions.RULES)

        val report = Report.inMemory()
        val r1 = analyzer.analyze(idAst, report, cfg)
        val r2 = analyzer.analyze(printlnAst, r1, cfg)

        assertTrue(r2.isEmpty(), "Empty JSON must apply no rules at all")
    }

    @Test
    fun `unknown keys are ignored and valid keys still work`() {
        val analyzer = Analyzer(listOf(IdentifierNamingRule(IdentifierNamingRuleDef)))
        val cfg =
            cfgFrom(
                """
                {
                  "identifierNamingStyle": "snake",
                  "___unknown___": "whatever"
                }
                """,
                AnalyzerRuleDefinitions.RULES,
            )

        // camelCase identifier should violate when snake is configured
        val ast = id("userName")
        val report = Report.inMemory()

        val out = analyzer.analyze(ast, report, cfg)

        assertEquals(1, out.size(), "Must report one diagnostic for camelCase under snake")
        val d = out.first()
        assertEquals(IdentifierNamingRuleDef.id, d.ruleId)
    }
}
