import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file

class ExecuteCommand(
    private val engine: PrintScriptEngine,
) : BaseCliCommand(
        name = "execute",
        help = "Execute a PrintScript program\n\nExample: printscript execute -v 1.1 src/hello.ps",
    ) {
    private val source by argument(help = "Source file to execute").file(mustExist = true, canBeDir = false)

    /**
     * This is the main logic that executes inside the try-catch of the base class.
     */
    override fun executeLogic() {
        engine.setVersion(version) // 'version' comes from BaseCliCommand

        val output = engine.execute(source.absolutePath, reporter)

        // Clear the progress line before printing the output
        reporter.clearProgressLine()

        // Print the program output (if any)
        if (output.isNotBlank()) {
            echo(output)
        } else {
            // Optional: report success if there was no output
            reporter.reportSuccess("Execution complete. No output.")
        }
    }
}
