interface AnalyzerRule {
    val id: String
    val description: String
    val severity: Type

    fun check(
        ast: AstNode,
        report: (Diagnostic) -> Unit,
    )
}
