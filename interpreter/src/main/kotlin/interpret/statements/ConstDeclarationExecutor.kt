package interpret.statements

import DeclarationAssignmentNode
import Result
import TokenType
import interpret.registry.StatementExecutor
import language.errors.InterpreterException
import language.types.PSType
import runtime.core.EvaluationContext
import runtime.core.InterpreterRuntime

class ConstDeclarationExecutor(
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
                    false, // const es inmutable
                )
            }
        }
    }
}
