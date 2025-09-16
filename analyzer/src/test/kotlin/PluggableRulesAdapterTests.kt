
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import simple.SimpleArgConfig
import simple.SimpleArgDef
import simple.SimpleArgRule
import utils.RuleOwner
import utils.Type

// New, TCK-like rule defs with custom IDs
object CustomPrintlnTckDef : SimpleArgDef {
    override val id: String = "mandatory-variable-or-literal-in-println"
    override val description: String = "println must receive a variable or a literal"
    override val owner: RuleOwner = RuleOwner.USER
    override val default: SimpleArgConfig = SimpleArgConfig(false)
    override val restrictedCases: Set<String> = setOf("println")
    override val type: Type = Type.WARNING

    override fun parse(configMap: Map<String, String>): SimpleArgConfig =
        SimpleArgConfig(
            configMap[id]?.toBoolean() ?: false,
        )
}

object CustomEchoSimpleArgDef : SimpleArgDef {
    override val id: String = "echoSimpleArg"
    override val description: String = "echo must receive a variable or a literal"
    override val owner: RuleOwner = RuleOwner.USER

    // Intentionally true by default to prove defaults are NOT applied unless key is present
    override val default: SimpleArgConfig = SimpleArgConfig(true)
    override val restrictedCases: Set<String> = setOf("echo")
    override val type: Type = Type.WARNING

    override fun parse(configMap: Map<String, String>): SimpleArgConfig =
        SimpleArgConfig(
            configMap[id]?.toBoolean() ?: default.enabled,
        )
}

class PluggableRulesAdapterTests {
    private fun cfgFrom(
        json: String,
        defs: List<shared.RuleDefinition<shared.RuleConfig>>,
    ): AnalyzerConfig {
        val tmp =
            kotlin.io.path
                .createTempFile("analyzer-", ".json")
                .toFile()
        tmp.writeText(json.trimIndent())
        return AnalyzerConfig.fromPath(tmp.path, defs)
    }

    @Test
    fun `custom println TCK id is honored`() {
        val analyzer = Analyzer(listOf(SimpleArgRule(CustomPrintlnTckDef)))

        val cfg =
            cfgFrom(
                """
                { "mandatory-variable-or-literal-in-println": true }
                """,
                listOf(CustomPrintlnTckDef),
            )

        val ast = FunctionCallNode("println", bin(id("a"), "+", litNumber("1")), false)
        val out = analyzer.analyze(ast, Report.inMemory(), cfg)

        assertEquals(1, out.size())
        val d = out.first()
        assertEquals(CustomPrintlnTckDef.id, d.ruleId)
        assertEquals(Type.WARNING, d.type)
        assertTrue(d.message.contains("println"))
    }

    @Test
    fun `defaults of new rules are not applied when key is absent`() {
        val analyzer = Analyzer(listOf(SimpleArgRule(CustomEchoSimpleArgDef)))

        // No key for echoSimpleArg -> should behave as "not configured" and skip rule
        val cfg = cfgFrom("{}", listOf(CustomEchoSimpleArgDef))

        val ast = FunctionCallNode("echo", bin(id("a"), "+", litNumber("1")), false)
        val out = analyzer.analyze(ast, Report.inMemory(), cfg)

        assertTrue(out.isEmpty(), "With empty JSON, even default=true rules must not run")
    }
}
