import etapa1.command.AnalyzingCommand
import etapa1.command.ExecutionCommand
import etapa1.command.FormattingCommand
import etapa1.command.ValidationCommand

fun main(args: Array<String>) {
    val validationHandler = ValidationHandler(/* deps */)
    val executionHandler  = ExecutionHandler(/* deps */)
    val formattingHandler = FormattingHandler(/* deps */)
    val analyzingHandler  = AnalyzingHandler(/* deps */)

    val orchestrator = Orchestrator(
        validationHandler,
        executionHandler,
        formattingHandler,
        analyzingHandler
    )

    PrintScriptCli()
        .subcommands(
            ValidationCommand(orchestrator),
            ExecutionCommand(orchestrator),
            FormattingCommand(orchestrator),
            AnalyzingCommand(orchestrator)
        )
        .main(args)
}