import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import commands.AnalyzeCommand
import commands.ExecuteCommand
import commands.FormatCommand
import commands.ValidateCommand

fun main(args: Array<String>) {
    PrintScriptCLI()
        .subcommands(
            ValidateCommand(),
            ExecuteCommand(),
            FormatCommand(),
            AnalyzeCommand(),
        ).main(args)
}

class PrintScriptCLI :
    CliktCommand(
        name = "printscript",
        help = "PrintScript CLI - A command-line interface for PrintScript language operations",
    ) {
    override fun run() {
        echo("Welcome to PrintScript CLI. Use --help to see available commands.")
    }
}
