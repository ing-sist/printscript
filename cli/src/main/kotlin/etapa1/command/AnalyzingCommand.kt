package etapa1.command

import Orchestrator
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import etapa1.AppContext
import etapa1.Operation
import viejos.OperationRequest

class AnalyzingCommand(private val orchestrator: Orchestrator) :
    CliktCommand(name = "Analyzing", help = "Aplica reglas estáticas (lint/análisis)") {

    private val file by argument(help = "Archivo fuente a analizar")
    private val ctx by requireObject<AppContext>()

    override fun run() {
        val req = OperationRequest(
            operation = Operation.Analyzing,
            sourceFile = file,
            specVersion = ctx.version,
            report = ctx.reportSink,
            progress = ctx.progressSink,
        )

        val res = orchestrator.run(req)

        // Política de salida:
        // - errores → 1
        // - si definís política de warnings en el orquestador (ej. maxWarnings), también 1 si excede
        if (res.errors > 0) currentContext.exit(1)
    }
}