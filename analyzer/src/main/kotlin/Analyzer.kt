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
            val def = rule.ruleDef
            val ruleCfg = config.tryGet(def)
            if (ruleCfg != null && ruleCfg.enabled) {
                r = rule.apply(ast, r, ruleCfg)
            }
        }
        return r
    }
}
