package etapa2.handlers.impl

import Diagnostic


import etapa2.OperationHandler
import etapa2.OperationResult
import viejos.OperationRequest

// Tu Analyzer real:
import Analyzer   // ← ajustá el paquete real

// Opcional: un adapter si tu Analyzer no devuelve List<Diagnostic>
class AnalyzerAdapter(private val analyzer: Analyzer) {
    fun analyze(ast: Any): List<Diagnostic> {
        // Adaptá este cuerpo a la firma real de tu Analyzer:
        // Ejemplos:
        //   return analyzer.analyze(ast)                      // si ya devuelve List<Diagnostic>
        //   return analyzer.run(ast).diagnostics              // si devuelve un Report/Result con .diagnostics
        //   analyzer.onDiagnostic { collect += it }; analyzer.run(ast); return collect
        return analyzer.analyze(ast) // Cambiá este renglón si tu método se llama distinto
    }
}

class AnalyzingHandler(
    private val analyzer: Analyzer
) : OperationHandler {

    override fun run(req: OperationRequest): OperationResult {
        val adapter = AnalyzerAdapter(analyzer)

        val out = AnalyzingHelper.run(
            sourceFile   = req.sourceFile,
            specVersion  = req.specVersion,
            analyze      = { ast -> adapter.analyze(ast) }
        )

        return OperationResult(errors = out.errors, warnings = out.warnings)
    }
}