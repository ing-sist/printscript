package etapa2.handlers

// Reutilizable en varios handlers: hace read→lex→parse y emite progreso/diags de sintaxis.
// No cierra el summary. Si falla el read, emite el diag y devuelve null.
data class ParseOutcome(
    val ast: Any,          // tu tipo de AST
    val errors: Int,
    val warnings: Int,
    val content: String    // útil para formatting (--check/--write)
)

class ReadLexParseHelper(
    private val lexerFactory: (String) -> Lexer,
    private val tokenProviderFactory: (Lexer) -> LexerTokenProvider,
    private val parserFactory: () -> Parser,
    private val ruleGenerator: RuleGenerator
) {
    fun run(req: OperationRequest): ParseOutcome? {
        var errors = 0
        var warnings = 0

        // READ
        req.progress.stageStart("read")
        val content = when (val r = FileInputReader.readFile(req.sourceFile)) {
            is Result.Success -> r.value
            is Result.Failure -> {
                req.report.emit(
                    Diagnostic("CLI.FileNotFound","No se pudo leer el archivo",
                        Location(req.sourceFile,1,1), Severity.ERROR)
                )
                req.progress.stageEnd("read")
                return null
            }
        }
        req.progress.stageAdvance("read", content.length.toLong())
        req.progress.stageEnd("read")

        // LEX
        req.progress.stageStart("lex")
        val lexer = lexerFactory(req.specVersion)
        lexer.setSource(req.sourceFile, content, ruleGenerator.rulesFor(req.specVersion))
        val provider = tokenProviderFactory(lexer)
        req.progress.stageEnd("lex")

        // PARSE
        req.progress.stageStart("parse")
        val parser = parserFactory()
        parser.onProgress { n -> req.progress.stageAdvance("parse", n.toLong()) }
        parser.onSyntaxError {
            if (it.severity == Severity.ERROR) errors++ else if (it.severity == Severity.WARNING) warnings++
            req.report.emit(it)
        }
        val ast = parser.parse(provider)
        req.progress.stageEnd("parse")

        return ParseOutcome(ast = ast, errors = errors, warnings = warnings, content = content)
    }
}