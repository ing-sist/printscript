package etapa1

class ProgressSink(private val out: Appendable) {
    fun stageStart(name: String, totalHint: Long?) {
        out.appendLine("Starting $name...")
    }

    fun stageAdvance(name: String, current: Long) {
        out.appendLine("  $name progress: $current")
    }

    fun stageEnd(name: String) {
        out.appendLine("Finished $name")
    }
}