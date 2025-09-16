package interpret.expression

import FunctionCallNode
import Result
import interpret.registry.ExpressionEvaluator
import language.errors.InterpreterException
import language.types.PSValue
import runtime.core.EvaluationContext
import runtime.core.InterpreterRuntime

class FunctionCallDispatcher(
    private val runtime: InterpreterRuntime,
) : ExpressionEvaluator<FunctionCallNode> {
    private val readInputEvaluator = ReadInputCallEvaluator(runtime)
    private val readEnvEvaluator = ReadEnvCallEvaluator(runtime)
    private val printLnEvaluator = PrintLnCallEvaluator(runtime)

    override fun evaluateExpression(
        node: FunctionCallNode,
        context: EvaluationContext,
    ): Result<PSValue, InterpreterException> =
        when (node.functionName) {
            "println" -> printLnEvaluator.evaluateExpression(node, context)
            "readInput" -> readInputEvaluator.evaluateExpression(node, context)
            "readEnv" -> readEnvEvaluator.evaluateExpression(node, context)
            else -> Result.Failure(InterpreterException("Unknown function: ${node.functionName}"))
        }
}
