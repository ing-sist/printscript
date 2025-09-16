package interpret.statements

import FunctionCallNode
import Result
import interpret.registry.StatementExecutor
import language.errors.InterpreterException
import runtime.core.EvaluationContext
import runtime.core.InterpreterRuntime

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
