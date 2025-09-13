package simple

import AstNode
import FunctionCallNode
import IdentifierNode
import LiteralNode
import Report
import shared.AnalyzerRule
import walk

class SimpleArgRule(
    override val config: SimpleArgConfig,
    override val ruleDef: SimpleArgDef,
) : AnalyzerRule {
    override fun check(
        ast: AstNode,
        report: Report,
    ): Report {
        var newReport = report
        if (!config.enabled) return report // si no esta activado, no verifico

        val enter: (AstNode) -> Boolean = walk@{ node ->
            if (node is FunctionCallNode) {
                if (node.functionName in ruleDef.restrictedCases) {
                    val arg = node.content
                    if (arg !is IdentifierNode && arg !is LiteralNode) {
                        newReport =
                            newReport.addDiagnostic(
                                ruleDef.id,
                                ruleDef.description,
                                node.getLocation(),
                                config.type,
                            )
                    }
                }
            }
            true // sigo recorriendo con walk
        }
        walk(ast, enter)
        return newReport
    }
}
