package interpret.expression

import IdentifierNode
import Result
import interpret.registry.ExpressionEvaluator
import language.errors.InterpreterException
import language.types.PSValue
import runtime.core.EvaluationContext

class VariableReferenceEvaluator : ExpressionEvaluator<IdentifierNode> {
    override fun evaluateExpression(
        node: IdentifierNode,
        context: EvaluationContext,
    ): Result<PSValue, InterpreterException> =
        context.variableStore.getVariable(node.name).map { binding ->
            binding.value
        }
}
