import shared.AnalyzerRule
import shared.RuleConfig

class Analyzer(
    private val rules: List<AnalyzerRule<out RuleConfig>>,
) {
    fun analyze(
        ast: AstNode,
        report: Report,
        config: AnalyzerConfig,
    ): Report {
        var r = report
        for (rule in rules) {
            @Suppress("UNCHECKED_CAST")
            rule as AnalyzerRule<RuleConfig> // tengo que castear para que la config sea de esa regla
            val def = rule.ruleDef
            val analyzerConfig = config.get(def)
            if (!analyzerConfig.enabled) continue

            r = rule.check(ast, r, analyzerConfig)
        }
        return r
    }
}
