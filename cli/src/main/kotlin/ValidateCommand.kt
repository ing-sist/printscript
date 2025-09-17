import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file

class ValidateCommand(
    private val engine: PrintScriptEngine,
) : CliktCommand(
        name = "validate",
        help =
            "Validate a PrintScript program by lexing and parsing it.\n\n" +
                "Example: printscript validate src/hello.ps --version 1.0",
    ) {
    private val source by argument(help = "Source file to validate")
        .file(mustExist = true, canBeDir = false)
    private val version by option(
        "-v",
        "--version",
        help = "PrintScript version",
    ).default("1.0")
    private val showProgress by option(
        "--show-progress",
        help = "Show validation progress",
    ).flag()
    private val silent by option(
        "--silent",
        help = "Suppress output",
    ).flag()

    override fun run() {
        try {
            engine.setVersion(version)
            if (showProgress && !silent) {
                val count = engine.countTokens(source.absolutePath)
                echo("Tokens: $count")
                echo("Parsing...")
            }
            engine.validateSyntax(source.absolutePath)
            if (!silent) echo("File is valid")
        } catch (e: LexerException) {
            echo("Error: ${e.message}")
        } catch (e: IllegalStateException) {
            echo("Error: ${e.message}")
        }
    }
}
