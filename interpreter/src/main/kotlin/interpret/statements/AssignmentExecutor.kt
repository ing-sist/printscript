package interpret.statements

import AssignmentNode
import Result
import interpret.registry.StatementExecutor
import language.errors.InterpreterException
import runtime.core.EvaluationContext
import runtime.core.InterpreterRuntime

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
