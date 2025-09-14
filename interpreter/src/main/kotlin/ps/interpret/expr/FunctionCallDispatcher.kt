package ps.interpret.expr

import FunctionCallNode
import Result
import ps.interpret.registry.ExpressionEvaluator
import ps.lang.errors.InterpreterException
import ps.lang.types.PSValue
import ps.runtime.core.EvaluationContext
import ps.runtime.core.InterpreterRuntime

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
            "printLn" -> printLnEvaluator.evaluateExpression(node, context)
            "readInput" -> readInputEvaluator.evaluateExpression(node, context)
            "readEnv" -> readEnvEvaluator.evaluateExpression(node, context)
            else -> Result.Failure(InterpreterException("Unknown function: ${node.functionName}"))
        }
}
