import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import language.errors.InterpreterException
import java.io.File
import java.io.IOException

class ExecuteCommand(
    private val engine: PrintScriptEngine,
) : CliktCommand(
        name = "execute",
        help = "Execute a PrintScript program\n\nExample: printscript execute -v 1.1 src/hello.ps",
    ) {
    private val source by argument(help = "Source file to execute").file(mustExist = true, canBeDir = false)
    private val version by option("-v", "--version", help = "PrintScript version").default("1.1")
    private val inputFile by option(
        "--input",
        help = "Input file for the program",
    ).file(mustExist = true, canBeDir = false)
    private val envVars by option("--env", help = "Environment variables (KEY=VAL)").multiple()
    private val showProgress by option("--show-progress", help = "Show token parsing progress").flag()
    private val silent by option("--silent", help = "Suppress output").flag()

    override fun run() {
        try {
            engine.setVersion(version)
            if (showProgress && !silent) {
                val count = engine.countTokens(source.absolutePath)
                echo("Tokens: $count")
                echo("Parsing...")
                echo("Executing...")
            }
            val inputs = readInputs()
            val env = parseEnv()
            val output = engine.execute(source.absolutePath, inputs, env)
            if (!silent && output.isNotBlank()) echo(output)
        } catch (e: LexerException) {
            echo("Error: ${e.message}")
        } catch (e: IllegalStateException) {
            echo("Error: ${e.message}")
        } catch (e: InterpreterException) {
            echo("Error: ${e.message}")
        } catch (e: IOException) {
            echo("Error: ${e.message}")
        }
    }

    private fun readInputs(): List<String> = inputFile?.let { File(it.absolutePath).readLines() } ?: emptyList()

    private fun parseEnv(): Map<String, String> =
        envVars.associate { pair ->
            val idx = pair.indexOf('=')
            if (idx <= 0) pair to "" else pair.take(idx) to pair.substring(idx + 1)
        }
}
