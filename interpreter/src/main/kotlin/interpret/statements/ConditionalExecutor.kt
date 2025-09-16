package interpret.statements

import AstNode
import ConditionalNode
import Result
import interpret.registry.StatementExecutor
import language.errors.InterpreterException
import language.errors.TypeMismatchError
import language.types.PSBoolean
import language.types.PSType
import runtime.core.EvaluationContext
import runtime.core.InterpreterRuntime

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
