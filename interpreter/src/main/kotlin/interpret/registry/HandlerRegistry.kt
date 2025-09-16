package interpret.registry

import AstNode
import Result
import language.errors.InterpreterException
import language.errors.NoExpressionEvaluatorError
import language.errors.NoStatementExecutorError
import language.types.PSValue
import runtime.core.EvaluationContext

class HandlerRegistry {
    private val expressionEvaluators = mutableMapOf<Class<out AstNode>, ExpressionEvaluator<out AstNode>>()
    private val statementExecutors = mutableMapOf<Class<out AstNode>, StatementExecutor<out AstNode>>()

    fun <T : AstNode> registerExpressionEvaluator(
        nodeClass: Class<T>,
        evaluator: ExpressionEvaluator<T>,
    ) {
        expressionEvaluators[nodeClass] = evaluator
    }

    fun <T : AstNode> registerStatementExecutor(
        nodeClass: Class<T>,
        executor: StatementExecutor<T>,
    ) {
        statementExecutors[nodeClass] = executor
    }

    @Suppress("UNCHECKED_CAST")
    fun evaluateExpression(
        node: AstNode,
        context: EvaluationContext,
    ): Result<PSValue, InterpreterException> {
        val evaluator =
            expressionEvaluators[node::class.java] as? ExpressionEvaluator<AstNode>
                ?: return Result.Failure(NoExpressionEvaluatorError(node::class.simpleName ?: "Unknown"))

        return try {
            evaluator.evaluateExpression(node, context)
        } catch (e: InterpreterException) {
            Result.Failure(e)
        } catch (error: InterpreterException) {
            Result.Failure(InterpreterException("Error in expression evaluator: ${error.message}"))
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun executeStatement(
        node: AstNode,
        context: EvaluationContext,
    ): Result<Unit, InterpreterException> {
        val executor =
            statementExecutors[node::class.java] as? StatementExecutor<AstNode>
                ?: return Result.Failure(NoStatementExecutorError(node::class.simpleName ?: "Unknown"))

        return try {
            executor.executeStatement(node, context)
        } catch (error: InterpreterException) {
            Result.Failure(error)
        } catch (error: InterpreterException) {
            Result.Failure(InterpreterException("Error in statement executor: ${error.message}"))
        }
    }
}
