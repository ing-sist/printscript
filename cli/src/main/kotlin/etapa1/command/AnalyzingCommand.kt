package etapa1.command

import Orchestrator
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import viejos.AppContext
import etapa1.Operation
import viejos.OperationRequest

class AnalyzingCommand(private val orchestrator: Orchestrator) :
    CliktCommand(name = "Analyzing", help = "Aplica reglas estáticas (lint/análisis)") {

    private val file by argument(help = "Archivo fuente a analizar")
    private val rulesPath by option("--rules", help = "Ruta al archivo de reglas (opcional)")
    private val maxWarnings by option("--max-warnings", help = "Falla si supera este número").int()

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