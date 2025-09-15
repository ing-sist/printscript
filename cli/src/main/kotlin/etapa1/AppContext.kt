package etapa1

import Diagnostic

data class AppContext(
    val version: String,
    val reportSink: ReportSink,
    val progressSink: ProgressSink
)

interface ReportSink {
    fun emit(d: Diagnostic)
    fun end(s: Summary)
}

data class Summary(
    val operation: String,
    val specVersion: String,
    val filesProcessed: Int,
    val errors: Int,
    val warnings: Int,
    val timeMs: Long
)

interface ProgressSink {
    fun stageStart(name: String, totalHint: Long? = null)
    fun stageAdvance(name: String, current: Long)
    fun stageEnd(name: String)
}

class ConsoleProgressSink(private val out: Appendable) : ProgressSink {
    override fun stageStart(name: String, totalHint: Long?) {
        out.appendLine("Starting $name...")
    }

    override fun stageAdvance(name: String, current: Long) {
        out.appendLine("  $name progress: $current")
    }

    override fun stageEnd(name: String) {
        out.appendLine("Finished $name")
    }
}

