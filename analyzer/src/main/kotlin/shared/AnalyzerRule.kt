package shared

import AstNode
import Report

interface AnalyzerRule<C : RuleConfig> {
    val ruleDef: RuleDefinition<C>

    fun check(
        ast: AstNode,
        report: Report,
        config: C,
    ): Report

    // Localize the only cast here; Analyzer won’t need any.
    fun apply(
        ast: AstNode,
        report: Report,
        configAny: RuleConfig,
    ): Report {
        @Suppress("UNCHECKED_CAST")
        val cfg = configAny as C
        return check(ast, report, cfg)
    }
}
