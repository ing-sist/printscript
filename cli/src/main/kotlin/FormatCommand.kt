import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import config.FormatterRuleImplementations
import config.FormatterStyleConfig
import java.io.FileReader
import java.io.IOException

class FormatCommand :
    CliktCommand(
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

    override fun run() {
        try {
            val tokenRule = RuleGenerator.createDefaultTokenRule()
            val reader = FileReader(source)
            val lexer = Lexer(reader, tokenRule)
            val tokenProvider = LexerTokenProvider(lexer, readSpace = true, readNewline = true)

            val styleConfig =
                styleFile?.let { FormatterStyleConfig.fromPath(it.absolutePath) }
                    ?: FormatterStyleConfig.default()

            val formatter = Formatter(FormatterRuleImplementations.IMPLEMENTATIONS)
            val docBuilder = DocBuilder.inMemory()

            val result = formatter.format(tokenProvider, styleConfig, docBuilder)
            val formattedCode = result.build()

            if (outputFile != null) {
                outputFile!!.writeText(formattedCode)
                echo("Formatted code written to ${outputFile!!.absolutePath}")
            } else {
                echo(formattedCode)
            }
        } catch (e: LexerException) {
            echo("Error: ${e.message}", err = true)
        } catch (e: IOException) {
            echo("Error: ${e.message}", err = true)
        }
    }
}
