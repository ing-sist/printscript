package etapa1.command

import Orchestrator
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import etapa1.AppContext
import etapa1.Operation
import viejos.OperationRequest

class ExecutionCommand(private val orchestrator: Orchestrator) :
    CliktCommand(name = "Execution", help = "Valida y ejecuta el programa") {

    private val file by argument(help = "Archivo fuente")
    private val ctx by requireObject<AppContext>()

    override fun run() {
        val req = OperationRequest(
            operation = Operation.Execution,
            sourceFile = file,
            specVersion = ctx.version,
            report = ctx.reportSink,
            progress = ctx.progressSink
        )

        val res = orchestrator.run(req)
        // si hubo error de runtime, usá exit 2; si hubo errores de validación, 1; sino 0
        when {
            res.errors > 0 -> currentContext.exit(1)
            /* res.runtimeErrors > 0 */ false -> currentContext.exit(2)
        }
    }
}