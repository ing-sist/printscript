package naming

import AstNode
import IdentifierNode
import Report
import shared.AnalyzerRule
import utils.walk

class IdentifierNamingRule(
    override val ruleDef: IdentifierNamingRuleDef,
) : AnalyzerRule<IdentifierNamingConfig> {
    override fun check(
        ast: AstNode,
        report: Report,
        config: IdentifierNamingConfig,
    ): Report {
        var newReport = report
        if (!config.enabled) return report

        val enter: (AstNode) -> Boolean = { node ->
            // enter es una fun que recibe un ast node y devuelve bool
            // hace este codigo x cada nodo del ast
            if (node is IdentifierNode) { // si es un nodo identificador
                val ok = config.namingType.isValid(node.name)
                if (!ok) { // si no esta ok, mando report
                    val expected = config.namingType.description()
                    val message = "Identifiers are expected to be in $expected and got '${node.name}'"
                    newReport = newReport.addDiagnostic(ruleDef.id, message, node.getLocation(), ruleDef.type)
                }
            }
            true // si no es identificador, es true
        }
        walk(ast, enter) // recorro el arbol
        return newReport
    }
}
