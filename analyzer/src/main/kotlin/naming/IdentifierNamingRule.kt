package naming

import AstNode
import IdentifierNode
import Report
import shared.AnalyzerRule
import walk

class IdentifierNamingRule(
    override val config: IdentifierNamingConfig, // aca la rule config tiene que ser especifica
    override val ruleDef: IdentifierNamingRuleDef,
) : AnalyzerRule {
    override fun check(
        ast: AstNode,
        report: Report,
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
                    newReport = newReport.addDiagnostic(ruleDef.id, message, node.getLocation(), config.type)
                }
            }
            true // si no es identificador, es true
        }
        walk(ast, enter) // recorro el arbol
        return newReport
    }
}
