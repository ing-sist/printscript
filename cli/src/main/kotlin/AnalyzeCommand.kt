import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file

class AnalyzeCommand(
    private val engine: PrintScriptEngine,
) : CliktCommand(
        name = "analyze",
        help =
            "Run analyzer rules on a PrintScript program\n\n" +
                "Example: printscript analyze src/hello.ps --rules analyzer.json",
    ) {
    private val source by argument(help = "Source file to analyze")
        .file(mustExist = true, canBeDir = false)
    private val rulesFile by option(
        "--rules",
        help = "Analyzer rules configuration file",
    ).file(mustExist = true, canBeDir = false)

    override fun run() {
        try {
            engine.setAnalyzerConfig(rulesFile?.absolutePath)
            val report = engine.analyze(source.absolutePath)
            if (report.isEmpty()) {
                echo("No issues found")
            } else {
                echo("Found ${report.size()} issue(s):")
                report.forEach { diagnostic: Diagnostic ->
                    val loc = diagnostic.location
                    echo(
                        "${diagnostic.type}: ${diagnostic.message} (" +
                            "${loc.line}:${loc.startCol}-${loc.endCol})",
                    )
                }
            }
        } catch (e: LexerException) {
            echo("Error: ${e.message}", err = true)
        } catch (e: IllegalStateException) {
            echo("Error: ${e.message}", err = true)
        }
    }
}
