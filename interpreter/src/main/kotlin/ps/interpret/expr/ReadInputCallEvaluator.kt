package ps.interpret.expr

import FunctionCallNode
import Result
import ps.interpret.registry.ExpressionEvaluator
import ps.lang.errors.InterpreterException
import ps.lang.types.PSString
import ps.lang.types.PSValue
import ps.runtime.core.EvaluationContext
import ps.runtime.core.InterpreterRuntime

class ReadInputCallEvaluator(
    private val runtime: InterpreterRuntime,
) : ExpressionEvaluator<FunctionCallNode> {
    override fun evaluateExpression(
        node: FunctionCallNode,
        context: EvaluationContext,
    ): Result<PSValue, InterpreterException> {
        if (node.functionName != "readInput") {
            return Result.Failure(InterpreterException("This evaluator only handles readInput calls"))
        }

        return runtime.evaluateExpression(node.content, context).flatMap { promptValue ->
            if (promptValue !is PSString) {
                return@flatMap Result.Failure(InterpreterException("readInput prompt must be a string"))
            }

            try {
                val rawInput = context.inputProvider.readInput(promptValue.value)

                // Si hay un tipo esperado en el contexto, parseamos según ese tipo
                if (context.expectedType != null) {
                    context.typeRules.parseRawTo(context.expectedType, rawInput)
                } else {
                    // Por defecto, devolvemos como string
                    Result.Success(PSString(rawInput))
                }
            } catch (error: IllegalStateException) {
                Result.Failure(InterpreterException("Input provider error: ${error.message}"))
            }
        }
    }
}
