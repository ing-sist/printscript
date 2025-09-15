package etapa2.handlers

import utils.Type
import viejos.OperationRequest

// Valida semántica SIN cerrar summary. Devuelve null si hubo error de lectura.
class ValidationCore(
    private val readLexParse: ReadLexParseHelper,
    private val analyzerFactory: () -> Analyzer,
    private val validatorsProvider: ValidatorsProvider
) {
    fun run(req: OperationRequest): ParseOutcome {
        val parsed = readLexParse.run(req) ?: return ParseOutcome(ast = Unit, errors = 1, warnings = 0, content = "")
        var errors = parsed.errors
        var warnings = parsed.warnings

        // Semántica
        req.progress.stageStart("semantics")
        val analyzer = analyzerFactory()
        analyzer.onDiagnostic {
            if (it.severity == Severity.ERROR) errors++ else if (it.severity == Type.WARNING) warnings++
            req.report.emit(it)
        }
        analyzer.runSemanticChecks(parsed.ast, validatorsProvider.forSpec(req.specVersion))
        req.progress.stageEnd("semantics")

        return parsed.copy(errors = errors, warnings = warnings)
    }
}