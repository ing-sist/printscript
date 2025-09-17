import com.github.ajalt.clikt.core.CliktCommand

class PrintScriptCLI(
    val engine: PrintScriptEngine,
) : CliktCommand() {
    override fun run() {
        echo("Welcome to PrintScript CLI. Use --help to see available commands.")
    }
}
