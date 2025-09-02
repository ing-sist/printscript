import rules.lookForLocation

data class PrintlnArgsMustBeSimpleRule(
    val enabled: Boolean,
) : AnalyzerRule {
    override val id = "Println.ArgSimple"
    override val description = "println must receive an identifier or a literal (no expressions)"
    override val severity = Type.WARNING

    override fun check(
        ast: AstNode,
        report: (Diagnostic) -> Unit,
    ) {
        if (!enabled) return // si no esta activado, no verifico
        val enter: (AstNode) -> Boolean = walk@{ node ->
            if (node is PrintlnNode) { // si es un nodo printer
                val ok =
                    when (node.content) {
                        is IdentifierNode, is LiteralNode -> true // me fijo lo que tiene
                        else -> false // si no cumple no lo camino
                    }
                if (!ok) {
                    report(Diagnostic(id, description, lookForLocation(node.content), severity))
                    return@walk false // ya no me interesa seguir bajando xq ya revise el nodo que m eimporya
                }
                false
            }
            true // sigo recorriendo con walk
        }
        walk(ast, enter)
    }
}
