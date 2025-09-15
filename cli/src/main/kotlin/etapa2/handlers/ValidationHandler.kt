import etapa2.OperationHandler
import etapa2.OperationResult
import viejos.OperationRequest

class ValidationHandler(
    private val lexerFactory: (String) -> Lexer,
    private val parserFactory: () -> Parser,
    private val analyzerFactory: () -> Analyzer
) : OperationHandler {
    override fun run(req: OperationRequest): OperationResult {
        var errors = 0

        // Read
        val text = File(req.sourceFile).readText()

        // Lex
        val lexer = lexerFactory(req.specVersion)
        val tokens = lexer.tokenize(text)

        // Parse
        val parser = parserFactory()
        val ast = parser.parse(tokens) { diag ->
            errors++; req.report.emit(diag)
        }

        // Semantics
        val analyzer = analyzerFactory()
        analyzer.check(ast) { diag ->
            if (diag.severity == Severity.ERROR) errors++
            req.report.emit(diag)
        }

        req.report.end(Summary("Validation", req.specVersion, 1, errors, 0, 0))
        return OperationResult(errors, 0)
    }
}