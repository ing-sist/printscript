package ps.interpret.expr

import FunctionCallNode
import Result
import ps.interpret.registry.ExpressionEvaluator
import ps.lang.errors.InterpreterException
import ps.lang.types.PSString
import ps.lang.types.PSValue
import ps.runtime.core.EvaluationContext
import ps.runtime.core.InterpreterRuntime

class PrintLnCallEvaluator(
    private val runtime: InterpreterRuntime,
) : ExpressionEvaluator<FunctionCallNode> {
    override fun evaluateExpression(
        node: FunctionCallNode,
        context: EvaluationContext,
    ): Result<PSValue, InterpreterException> {
        if (node.functionName != "printLn") {
            return Result.Failure(InterpreterException("This evaluator only handles printLn calls"))
        }

        return runtime.evaluateExpression(node.content, context).map { value ->
            val output = context.typeRules.formatForPrint(value)
            context.outputSink.print(output)
            // printLn no retorna un valor útil, pero necesitamos retornar algo
            PSString("")
        }
    }
}
