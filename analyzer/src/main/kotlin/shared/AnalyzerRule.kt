package shared

import AstNode
import Report

interface AnalyzerRule {
    val config: RuleConfig
    val ruleDef: RuleDefinition

    fun check(
        ast: AstNode,
        report: Report,
    ): Report
}
