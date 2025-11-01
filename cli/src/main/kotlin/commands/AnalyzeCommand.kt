import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file

class AnalyzeCommand(
    private val engine: PrintScriptEngine,
) : BaseCliCommand(
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

    override fun executeLogic() {
        engine.setAnalyzerConfig(rulesFile?.absolutePath)
        val report = engine.analyze(source.absolutePath, reporter)

        // Clear the progress line before printing the output
        reporter.clearProgressLine()

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
    }
}
