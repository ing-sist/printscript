package etapa1.command

import Orchestrator
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import viejos.AppContext
import etapa1.Operation
import viejos.OperationRequest

class ValidationCommand(private val orchestrator: Orchestrator) : CliktCommand(name = "Validation", help = "Valida sintaxis y semántica") {
    private val file by argument(help = "Archivo fuente")
    private val ctx by requireObject<AppContext>()

    override fun run() {
        val req = OperationRequest(
            operation = Operation.Validation,
            sourceFile = file,
            specVersion = ctx.version,
            report = ctx.reportSink,
            progress = ctx.progressSink
        )
        val res = orchestrator.run(req)
        if (res.errors > 0) currentContext.exit(1)
    }
}