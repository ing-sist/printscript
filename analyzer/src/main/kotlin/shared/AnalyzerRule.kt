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
}
