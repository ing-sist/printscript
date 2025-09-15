import etapa1.Operation
import etapa2.OperationHandler
import etapa2.OperationResult
import viejos.OperationRequest

class Orchestrator(
    private val validationHandler: OperationHandler,
    private val executionHandler: OperationHandler,
    private val formattingHandler: OperationHandler,
    private val analyzingHandler: OperationHandler
) {

    fun validate(req: OperationRequest) = run(req.copy(operation = Operation.Validation))
    fun execute(req: OperationRequest)  = run(req.copy(operation = Operation.Execution))
    fun format(req: OperationRequest)   = run(req.copy(operation = Operation.Formatting))
    fun analyze(req: OperationRequest)  = run(req.copy(operation = Operation.Analyzing))


    fun run(req: OperationRequest): OperationResult {
        val t0 = System.nanoTime()
        val result = when (req.operation) {
            Operation.Validation -> validationHandler.run(req)
            Operation.Execution  -> executionHandler.run(req)
            Operation.Formatting -> formattingHandler.run(req)
            Operation.Analyzing  -> analyzingHandler.run(req)
        }
        return result.copy(timeMs = (System.nanoTime() - t0) / 1_000_000)
    }
}