package ps.interpret.stmt

import AssignmentNode
import Result
import ps.interpret.registry.StatementExecutor
import ps.lang.errors.InterpreterException
import ps.runtime.core.EvaluationContext
import ps.runtime.core.InterpreterRuntime

class AssignmentExecutor(
    private val runtime: InterpreterRuntime,
) : StatementExecutor<AssignmentNode> {
    override fun executeStatement(
        node: AssignmentNode,
        context: EvaluationContext,
    ): Result<Unit, InterpreterException> =
        context.variableStore
            .getVariable(node.identifier.name)
            .flatMap { existingBinding ->
                runtime
                    .evaluateExpression(node.expression, existingBinding.type)
                    .flatMap { newValue ->
                        context.typeRules
                            .ensureAssignable(existingBinding.type, newValue)
                            .flatMap { validatedValue ->
                                context.variableStore.assignVariable(node.identifier.name, validatedValue)
                            }
                    }
            }
}
