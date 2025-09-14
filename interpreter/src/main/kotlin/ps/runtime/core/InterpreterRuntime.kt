package ps.runtime.core

import AstNode
import Result
import ps.interpret.registry.HandlerRegistry
import ps.lang.errors.InterpreterException
import ps.lang.types.PSType
import ps.lang.types.PSValue

class InterpreterRuntime(
    private val evaluationContext: EvaluationContext,
    private val expressionRegistry: HandlerRegistry,
    private val statementRegistry: HandlerRegistry,
) : Interpreter {
    override fun execute(statement: AstNode): Result<Unit, InterpreterException> = executeStatement(statement)

    internal fun evaluateExpression(
        node: AstNode,
        expectedType: PSType? = null,
    ): Result<PSValue, InterpreterException> {
        val contextWithType = evaluationContext.copy(expectedType = expectedType)
        return expressionRegistry.evaluateExpression(node, contextWithType)
    }

    internal fun evaluateExpression(
        node: AstNode,
        context: EvaluationContext,
    ): Result<PSValue, InterpreterException> = expressionRegistry.evaluateExpression(node, context)

    internal fun executeStatement(node: AstNode): Result<Unit, InterpreterException> =
        statementRegistry.executeStatement(node, evaluationContext)

    internal fun <T> withNewScope(block: () -> Result<T, InterpreterException>): Result<T, InterpreterException> =
        try {
            val result =
                evaluationContext.variableStore.withNewScope {
                    block()
                }
            result
        } catch (error: InterpreterException) {
            Result.Failure(error)
        } catch (error: IllegalStateException) {
            Result.Failure(InterpreterException("Invalid state error in scope: ${error.message}"))
        }
}
