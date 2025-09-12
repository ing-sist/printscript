import shared.AnalyzerRule

class Analyzer(
    private val rules: List<AnalyzerRule>,
) {
    fun analyze(
        ast: AstNode,
        report: Report,
    ): Report {
        var newReport = report
        for (rule in rules) {
            newReport = rule.check(ast, newReport)
        }
        return newReport
    }
}
