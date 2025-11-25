import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file

class FormatCommand(
    private val engine: PrintScriptEngine,
) : BaseCliCommand(
        name = "format",
        help =
            "Format a PrintScript program\n\n" +
                "Example: printscript format src/ugly.ps --style style.json --output formatted.ps",
    ) {
    private val source by argument(help = "Source file to format")
        .file(mustExist = true, canBeDir = false)
    private val styleFile by option(
        "--style",
        help = "Style configuration file",
    ).file(mustExist = true, canBeDir = false)
    private val outputFile by option(
        "--output",
        help = "Output file (default: stdout)",
    ).file()

    override fun executeLogic() {
        engine.setVersion(version) // 'version' comes from BaseCliCommand
        // Set the formatter config in the engine
        engine.setFormatterConfig(styleFile?.absolutePath)

        // Format using the engine
        val formattedCode = engine.format(source.absolutePath, reporter)

        // Clear the progress line before printing the output
        reporter.clearProgressLine()

        if (outputFile != null) {
            outputFile!!.writeText(formattedCode)
            reporter.reportSuccess("Formatted code written to ${outputFile!!.absolutePath}")
        } else {
            echo(formattedCode)
        }
    }
}
