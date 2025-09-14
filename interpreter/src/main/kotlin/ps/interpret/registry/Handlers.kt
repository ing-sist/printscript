package ps.interpret.registry

import AstNode
import Result
import ps.lang.errors.InterpreterException
import ps.lang.types.PSValue
import ps.runtime.core.EvaluationContext

interface ExpressionEvaluator<T : AstNode> {
    fun evaluateExpression(
        node: T,
        context: EvaluationContext,
    ): Result<PSValue, InterpreterException>
}

interface StatementExecutor<T : AstNode> {
    fun executeStatement(
        node: T,
        context: EvaluationContext,
    ): Result<Unit, InterpreterException>
}
