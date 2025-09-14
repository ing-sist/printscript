package simple

import AstNode
import FunctionCallNode
import IdentifierNode
import LiteralNode
import Report
import shared.AnalyzerRule
import utils.walk

class SimpleArgRule(
    override val ruleDef: SimpleArgDef,
) : AnalyzerRule<SimpleArgConfig> {
    override fun check(
        ast: AstNode,
        report: Report,
        config: SimpleArgConfig,
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
                                ruleDef.type,
                            )
                    }
                }
            }
            true // sigo recorriendo con utils.walk
        }
        walk(ast, enter)
        return newReport
    }
}
