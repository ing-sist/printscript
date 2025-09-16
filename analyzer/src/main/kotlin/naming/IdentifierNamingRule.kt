package naming

import AstNode
import IdentifierNode
import Report
import shared.AnalyzerRule
import shared.RuleDefinition
import utils.walk

class IdentifierNamingRule(
    override val ruleDef: RuleDefinition<IdentifierNamingConfig>,
) : AnalyzerRule<IdentifierNamingConfig> {
    override fun check(
        ast: AstNode,
        report: Report,
        config: IdentifierNamingConfig,
    ): Report {
        var newReport = report
        if (!config.enabled) return report

        val enter: (AstNode) -> Boolean = { node ->
            if (node is IdentifierNode) {
                val ok = config.namingType.isValid(node.name)
                if (!ok) {
                    val expected = config.namingType.description()
                    val message = "Identifiers are expected to be in $expected and got '${node.name}'"
                    newReport = newReport.addDiagnostic(ruleDef.id, message, node.getLocation(), ruleDef.type)
                }
            }
            true
        }
        walk(ast, enter)
        return newReport
    }
}
