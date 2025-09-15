package etapa1.command

import Orchestrator
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import etapa1.AppContext
import etapa1.Operation
import viejos.OperationRequest

class FormattingCommand(private val orchestrator: Orchestrator) :
    CliktCommand(name = "Formatting", help = "Formatea el archivo según las reglas de estilo") {

    private val file by argument(help = "Archivo fuente a formatear")
    private val ctx by requireObject<AppContext>()

    override fun run() {

        val req = OperationRequest(
            operation = Operation.Formatting,
            sourceFile = file,
            specVersion = ctx.version,
            report = ctx.reportSink,
            progress = ctx.progressSink,
        )

        val res = orchestrator.run(req)

        // Exit code recomendado:
        // - errores de parsing/semántica/config → 1
        // - en modo --check, si hay diferencias → 1 (el orquestador debería contar eso como error/violación)
        if (res.errors > 0) currentContext.exit(1)
    }
}