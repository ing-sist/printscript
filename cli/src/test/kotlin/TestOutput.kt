import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.testing.test
import org.junit.jupiter.api.Test

class TestOutput {
    @Test
    fun testOutputs() {
        val engine = PrintScriptEngine()
        val cli =
            PrintScriptCLI(engine)
                .subcommands(
                    ValidateCommand(engine),
                    ExecuteCommand(engine),
                    FormatCommand(engine),
                    AnalyzeCommand(engine),
                )

        println("=== Testing --help ===")
        val helpResult = cli.test("--help")
        println("Status code: ${helpResult.statusCode}")
        println("Output:")
        println(helpResult.output)
        println()

        println("=== Testing empty (no args) ===")
        val emptyResult = cli.test("")
        println("Status code: ${emptyResult.statusCode}")
        println("Output:")
        println(emptyResult.output)
        println()

        println("=== Testing format with invalid file ===")
        val formatCmd = FormatCommand(engine)
        try {
            val resource = javaClass.classLoader.getResource("invalid.ps")
            val path = resource?.path ?: "invalid.ps"
            val formatResult = formatCmd.test(path)
            println("Status code: ${formatResult.statusCode}")
            println("Output:")
            println(formatResult.output)
            println("Stderr:")
            println(formatResult.stderr)
        } catch (e: Exception) {
            println("Exception: ${e.message}")
        }
    }
}
