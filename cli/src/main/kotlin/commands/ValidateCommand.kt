import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file

class ValidateCommand(
    private val engine: PrintScriptEngine,
) : BaseCliCommand(
        name = "validate",
        help =
            "Validate a PrintScript program by lexing and parsing it.\n\n" +
                "Example: printscript validate src/hello.ps --version 1.0",
    ) {
    private val source by argument(help = "Source file to validate")
        .file(mustExist = true, canBeDir = false)

    override fun executeLogic() {
        engine.setVersion(version)
        // validateSyntax will use the 'reporter' from the base class
        engine.validateSyntax(source.absolutePath, reporter)
    }
}
