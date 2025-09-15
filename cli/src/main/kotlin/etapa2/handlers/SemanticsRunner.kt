package etapa2.handlers

import Analyzer
import etapa1.ProgressSink
import etapa1.ReportSink

object SemanticsRunner {
    fun check(
        ast: Any, // tu tipo de AST
        specVersion: String,
        report: ReportSink,
        progress: ProgressSink
    ) {
        progress.stageStart("semantics", null)
        val analyzer = Analyzer() // tu Analyzer real
        val validators = validators.provider.DefaultValidatorsProvider().forSpec(specVersion)
        analyzer.onDiagnostic { d -> report.emit(d) }
        analyzer.runSemanticChecks(ast, validators)
        progress.stageEnd("semantics")
    }
}