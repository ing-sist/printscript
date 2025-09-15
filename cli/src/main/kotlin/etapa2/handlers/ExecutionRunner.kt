package etapa2.handlers

import Diagnostic
import etapa1.ProgressSink
import etapa1.ReportSink

object ExecutionRunner {
    fun run(ast: Any, report: ReportSink, progress: ProgressSink) {
        progress.stageStart("execution", null)
        val interpreter = /* tu intérprete */ object {
            fun onRuntimeDiagnostic(cb: (Diagnostic) -> Unit) {}
            fun run(ast: Any) { /* emitir stdout directo */ }
        }
        // interpreter.onRuntimeDiagnostic { report.emit(it) }
        interpreter.run(ast)
        progress.stageEnd("execution")
    }
}