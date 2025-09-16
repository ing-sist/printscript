package ps.interpret.stmt

import DeclarationAssignmentNode
import Result
import TokenType
import ps.interpret.registry.StatementExecutor
import ps.lang.errors.InterpreterException
import ps.lang.types.PSType
import ps.runtime.core.EvaluationContext
import ps.runtime.core.InterpreterRuntime

class LetDeclarationExecutor(
    private val runtime: InterpreterRuntime,
) : StatementExecutor<DeclarationAssignmentNode> {
    override fun executeStatement(
        node: DeclarationAssignmentNode,
        context: EvaluationContext,
    ): Result<Unit, InterpreterException> {
        val psType =
            when (node.declaration.type.type) {
                TokenType.Keyword.NumberType -> PSType.NUMBER
                TokenType.Keyword.StringType -> PSType.STRING
                TokenType.Keyword.BooleanType -> PSType.BOOLEAN
                else -> return Result.Failure(InterpreterException("Unknown type: ${node.declaration.type.type}"))
            }

        return runtime.evaluateExpression(node.value, psType).flatMap { value ->
            context.typeRules.ensureAssignable(psType, value).flatMap { validatedValue ->
                context.variableStore.declareVariable(
                    node.declaration.identifier.name,
                    psType,
                    validatedValue,
                    true,
                )
            }
        }
    }
}
