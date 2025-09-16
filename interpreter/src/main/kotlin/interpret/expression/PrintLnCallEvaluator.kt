package interpret.expression

import FunctionCallNode
import Result
import interpret.registry.ExpressionEvaluator
import language.errors.InterpreterException
import language.types.PSString
import language.types.PSValue
import runtime.core.EvaluationContext
import runtime.core.InterpreterRuntime

class PrintLnCallEvaluator(
    private val runtime: InterpreterRuntime,
) : ExpressionEvaluator<FunctionCallNode> {
    override fun evaluateExpression(
        node: FunctionCallNode,
        context: EvaluationContext,
    ): Result<PSValue, InterpreterException> {
        if (node.functionName != "println") {
            return Result.Failure(InterpreterException("This evaluator only handles println calls"))
        }

        return runtime.evaluateExpression(node.content, context).map { value ->
            val output = context.typeRules.formatForPrint(value)
            context.outputSink.print(output)
            // printLn no retorna un valor útil, pero necesitamos retornar algo
            PSString("")
        }
    }
}
