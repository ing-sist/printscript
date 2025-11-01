import com.github.ajalt.clikt.core.subcommands

fun main(args: Array<String>) {
    val engine = PrintScriptEngine()
    PrintScriptCLI(engine)
        .subcommands(
            ValidateCommand(engine),
            ExecuteCommand(engine),
            FormatCommand(engine),
            AnalyzeCommand(engine),
        ).main(args)
}
