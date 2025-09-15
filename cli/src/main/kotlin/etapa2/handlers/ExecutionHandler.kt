import etapa1.Summary
import etapa2.OperationResult
import viejos.OperationRequest

class ExecutionHandler(
    private val validationCore: ValidationCore,
    private val interpreterFactory: () -> Interpreter
) : OperationHandler {

    override fun run(req: OperationRequest): OperationResult {
        // 1) Validación completa (sin cerrar summary)
        val validated = validationCore.run(req)
        if (validated.errors > 0) {
            // Errores de validación: cerramos summary como "Execution" para esta operación
            req.report.end(Summary("Execution", req.specVersion, 0, validated.errors, validated.warnings, 0))
            return OperationResult(validated.errors, validated.warnings)
        }

        // 2) Ejecución
        var errors = validated.errors
        var warnings = validated.warnings

        req.progress.stageStart("execution")
        val interpreter = interpreterFactory()
        interpreter.onRuntimeDiagnostic {
            if (it.severity == Severity.ERROR) errors++ else if (it.severity == Severity.WARNING) warnings++
            req.report.emit(it)
        }
        interpreter.run(validated.ast) // la salida del programa va a stdout; los errores por reportSink
        req.progress.stageEnd("execution")

        // 3) Cierre
        req.report.end(Summary("Execution", req.specVersion, 1, errors, warnings, 0))
        return OperationResult(errors, warnings)
    }
}