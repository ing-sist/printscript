class Linter(
    private val rules: List<AnalyzerRule>,
) {
    // cuando llamo al linter, leo el file de Style config para pasarle
    fun lint(ast: AstNode): List<Diagnostic> {
        val out = mutableListOf<Diagnostic>()
        for (rule in rules) rule.check(ast) { d -> out += d }
        return out
    }
}
