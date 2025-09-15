import etapa1.Summary
import etapa2.OperationHandler
import etapa2.OperationResult
import utils.Type
import validators.provider.ValidatorsProvider
import viejos.OperationRequest

class AnalyzingHandler(
    private val readLexParse: ReadLexParseHelper,
    private val analyzerFactory: () -> Analyzer,
    private val validatorsProvider: ValidatorsProvider
) : OperationHandler {

    override fun run(req: OperationRequest): OperationResult {
        var errors = 0
        var warnings = 0

        val parsed = readLexParse.run(req) ?: run {
            // read falló: ya se emitió el diag; cerramos summary aquí
            req.report.end(Summary("Analyzing", req.specVersion, 0, 1, 0, 0))
            return OperationResult(errors = 1, warnings = 0)
        }
        errors += parsed.errors
        warnings += parsed.warnings

        // Reglas estáticas (lint/análisis)
        req.progress.stageStart("analyzing")
        val analyzer = analyzerFactory()
        analyzer.onDiagnostic {
            if (it.severity == Severity.ERROR) errors++ else if (it.severity == Type.WARNING) warnings++
            req.report.emit(it)
        }
        analyzer.runSemanticChecks(parsed.ast, validatorsProvider.forSpec(req.specVersion))
        req.progress.stageEnd("analyzing")

        req.report.end(Summary("Analyzing", req.specVersion, 1, errors, warnings, 0))
        return OperationResult(errors, warnings)
    }
}