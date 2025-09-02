data class IdentifierNamingRule(
    val expected: IdentifierCase,
) : AnalyzerRule {
    override val id = "Naming.IdentifierStyle"
    override val description =
        when (expected) {
            IdentifierCase.CAMEL -> "Expected identifiers in camelCase"
            IdentifierCase.SNAKE -> "Expected identifiers in snake_case"
        }
    override val severity = Type.WARNING

    override fun check(
        ast: AstNode,
        report: (Diagnostic) -> Unit,
    ) {
        val enter: (AstNode) -> Boolean = { node ->
            if (node is IdentifierNode) { // si es un nodo identificador
                val ok =
                    when (expected) {
                        // me fijo que este bien segun la config
                        IdentifierCase.CAMEL -> Naming.isCamelCase(node.name)
                        IdentifierCase.SNAKE -> Naming.isSnakeCase(node.name)
                    } // si esya ok devuevlo true
                if (!ok) { // si no esta ok, mando report
                    report(
                        Diagnostic(
                            ruleId = id,
                            message = "$description (got '${node.name}')",
                            location = node.value.location,
                            severity = severity,
                        ),
                    )
                }
            }
            true // si no es identificador, es true
        }
        walk(ast, enter) // recorro el arbol
    }
}
