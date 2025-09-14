package ps.interpret.stmt

import AstNode
import ConditionalNode
import Result
import ps.interpret.registry.StatementExecutor
import ps.lang.errors.InterpreterException
import ps.lang.errors.TypeMismatchError
import ps.lang.types.PSBoolean
import ps.lang.types.PSType
import ps.runtime.core.EvaluationContext
import ps.runtime.core.InterpreterRuntime

class ConditionalExecutor(
    private val runtime: InterpreterRuntime,
) : StatementExecutor<ConditionalNode> {
    override fun executeStatement(
        node: ConditionalNode,
        context: EvaluationContext,
    ): Result<Unit, InterpreterException> {
        return runtime.evaluateExpression(node.condition, PSType.BOOLEAN).flatMap { conditionValue ->
            if (conditionValue !is PSBoolean) {
                return@flatMap Result.Failure(TypeMismatchError(PSType.BOOLEAN, conditionValue.type, "if condition"))
            }

            runtime.withNewScope {
                if (conditionValue.value) {
                    executeStatements(node.thenBody)
                } else if (node.elseBody != null) {
                    executeStatements(node.elseBody!!)
                } else {
                    Result.Success(Unit)
                }
            }
        }
    }

    private fun executeStatements(statements: List<AstNode>): Result<Unit, InterpreterException> {
        for (statement in statements) {
            val result = runtime.executeStatement(statement)
            if (result.isFailure) {
                return result
            }
        }
        return Result.Success(Unit)
    }
}
