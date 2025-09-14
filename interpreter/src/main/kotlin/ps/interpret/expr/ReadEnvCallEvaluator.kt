package ps.interpret.expr

import FunctionCallNode
import Result
import ps.interpret.registry.ExpressionEvaluator
import ps.lang.errors.InterpreterException
import ps.lang.errors.MissingEnvVarError
import ps.lang.types.PSString
import ps.lang.types.PSValue
import ps.runtime.core.EvaluationContext
import ps.runtime.core.InterpreterRuntime

class ReadEnvCallEvaluator(
    private val runtime: InterpreterRuntime,
) : ExpressionEvaluator<FunctionCallNode> {
    override fun evaluateExpression(
        node: FunctionCallNode,
        context: EvaluationContext,
    ): Result<PSValue, InterpreterException> {
        if (node.functionName != "readEnv") {
            return Result.Failure(InterpreterException("This evaluator only handles readEnv calls"))
        }

        return runtime.evaluateExpression(node.content, context).flatMap { nameValue ->
            if (nameValue !is PSString) {
                return@flatMap Result.Failure(InterpreterException("readEnv variable name must be a string"))
            }

            val envValue =
                context.envProvider.getEnvVariable(nameValue.value)
                    ?: return@flatMap Result.Failure(MissingEnvVarError(nameValue.value))

            // Si hay un tipo esperado en el contexto, parseamos según ese tipo
            if (context.expectedType != null) {
                context.typeRules.parseRawTo(context.expectedType, envValue)
            } else {
                // Por defecto, devolvemos como string
                Result.Success(PSString(envValue))
            }
        }
    }
}
