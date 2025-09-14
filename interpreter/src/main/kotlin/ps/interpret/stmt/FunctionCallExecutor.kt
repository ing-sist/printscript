package ps.interpret.stmt

import FunctionCallNode
import Result
import ps.interpret.registry.StatementExecutor
import ps.lang.errors.InterpreterException
import ps.runtime.core.EvaluationContext
import ps.runtime.core.InterpreterRuntime

class FunctionCallExecutor(
    private val runtime: InterpreterRuntime,
) : StatementExecutor<FunctionCallNode> {
    override fun executeStatement(
        node: FunctionCallNode,
        context: EvaluationContext,
    ): Result<Unit, InterpreterException> {
        // Evalúa la función como expresión y descarta el resultado
        return runtime.evaluateExpression(node, context).map { }
    }
}
