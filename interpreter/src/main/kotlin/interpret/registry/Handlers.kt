package interpret.registry

import AstNode
import Result
import language.errors.InterpreterException
import language.types.PSValue
import runtime.core.EvaluationContext

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
