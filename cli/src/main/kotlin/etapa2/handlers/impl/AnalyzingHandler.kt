package etapa2.handlers.impl

import etapa2.OperationHandler
import etapa2.OperationResult
import viejos.OperationRequest

import Analyzer                 // tu clase real (ajustá el paquete)
import AnalyzerConfig           // tu config real
import Report                   // tu report real
import Diagnostic               // tu tipo de diagnóstico
import utils.Type               // ERROR / WARNING / INFO, etc.

class AnalyzingHandler(
    private val analyzer: Analyzer,
    private val config: AnalyzerConfig,
    private val makeReport: () -> Report         // cómo crear un Report vacío
) : OperationHandler {

    override fun run(req: OperationRequest): OperationResult {
        // 1) Validación (read → lex → parse); sin semántica
        return when (val out = ValidationCore.run(req.sourceFile, req.specVersion)) {
            is ValidationOutcome.Failure -> {
                // Hubo errores antes de analizar → devolver contadores tal cual
                OperationResult(out.errors, out.warnings)
            }
            is ValidationOutcome.Success -> {
                var errors = out.errors
                var warnings = out.warnings

                // 2) Analyzer directo
                val initialReport = makeReport()
                val finalReport = try {
                    analyzer.analyze(out.ast, initialReport, config)
                } catch (t: Throwable) {
                    // Si tu analyzer lanza algo inesperado, contalo como error y cortá
                    errors++
                    println("Error en analyzer: ${t.message}")
                    return OperationResult(errors, warnings)
                }

                // 3) Extraer diagnósticos del Report final y contarlos
                //    Ajustá esta línea si tu API es distinta (p.ej. finalReport.all(), finalReport.items, etc.)
                val diagnostics: List<Diagnostic> = finalReport.getAllDiagnostics()

                for (d in diagnostics) {
                    when (d.type) {
                        Type.ERROR   -> { errors++ }
                        Type.WARNING -> { warnings++ }
                        else         -> { /* INFO/NOTE: no cuenta */ }
                    }
                    // (opcional) mostrar en “modo mínimo”
                    // println(renderDiag(d))
                }

                // 4) Devolver el total (lex/sintaxis + análisis)
                OperationResult(errors, warnings)
            }
        }
    }

    // (opcional) pretty print provisorio
    @Suppress("unused")
    private fun renderDiag(d: Diagnostic): String {
        val loc = d.location
        val where = if (loc != null) " (línea ${loc.line}, col ${loc.startCol})" else ""
        return "${d.type}: ${d.ruleId}: ${d.message}$where"
    }
}