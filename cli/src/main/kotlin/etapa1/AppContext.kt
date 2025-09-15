package etapa1

data class AppContext(
    val version: String,
    val reportSink: ReportSink,
    val progressSink: ProgressSink
)


