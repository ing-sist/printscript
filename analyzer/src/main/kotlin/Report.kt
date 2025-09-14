class Report private constructor(
    private val sink: DiagnosticSender,
    private val memory: InMemoryReport? = null,
) {
    companion object {
        fun inMemory(): Report {
            val mem = InMemoryReport()
            return Report(mem, mem)
        }

        fun to(target: Appendable): Report = Report(StreamingTextReport(target), null)
    }

    fun addDiagnostic(
        ruleId: String,
        message: String,
        location: Location,
        type: Type,
    ): Report {
        sink.emit(Diagnostic(ruleId, message, location, type))
        return this
    }

    fun isEmpty(): Boolean = memory?.isEmpty() ?: true

    fun size(): Int = memory?.size() ?: 0

    fun first(): Diagnostic = memory?.first() ?: error("Streaming report has no in-memory diagnostics")
}

class InMemoryReport : DiagnosticSender {
    private val list = mutableListOf<Diagnostic>()

    override fun emit(d: Diagnostic) {
        list += d
    }

    fun isEmpty() = list.isEmpty()

    fun size() = list.size

    fun first(): Diagnostic = list.first()
}

class StreamingTextReport(
    private val out: Appendable,
) : DiagnosticSender {
    override fun emit(d: Diagnostic) {
        out.appendLine("${d.type}\t${d.ruleId}\t${d.location.line}:${d.location.startCol}\t${d.message}")
    }
}
