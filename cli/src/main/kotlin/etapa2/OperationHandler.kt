package etapa2

import viejos.OperationRequest

interface OperationHandler {
    fun run(req: OperationRequest): OperationResult
}