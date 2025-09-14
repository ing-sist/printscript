package ps.interpret.expr

import IdentifierNode
import Result
import ps.interpret.registry.ExpressionEvaluator
import ps.lang.errors.InterpreterException
import ps.lang.types.PSValue
import ps.runtime.core.EvaluationContext

class VariableReferenceEvaluator : ExpressionEvaluator<IdentifierNode> {
    override fun evaluateExpression(
        node: IdentifierNode,
        context: EvaluationContext,
    ): Result<PSValue, InterpreterException> =
        context.variableStore.getVariable(node.name).map { binding ->
            binding.value
        }
}
