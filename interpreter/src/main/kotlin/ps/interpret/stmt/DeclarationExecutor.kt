package ps.interpret.stmt

import DeclarationNode
import Result
import ps.interpret.registry.StatementExecutor
import ps.lang.errors.InterpreterException
import ps.lang.types.PSBoolean
import ps.lang.types.PSNumber
import ps.lang.types.PSString
import ps.lang.types.PSType
import ps.lang.types.PSValue
import ps.runtime.core.EvaluationContext

class DeclarationExecutor : StatementExecutor<DeclarationNode> {
    override fun executeStatement(
        node: DeclarationNode,
        context: EvaluationContext,
    ): Result<Unit, InterpreterException> {
        val psType =
            when (node.type.type) {
                TokenType.NumberType -> PSType.NUMBER
                TokenType.StringType -> PSType.STRING
                TokenType.BooleanType -> PSType.BOOLEAN
                else ->
                    return Result.Failure(
                        InterpreterException("Unknown type: ${node.type.type}"),
                    )
            }
        val default: PSValue =
            when (psType) {
                PSType.NUMBER -> PSNumber(0.0)
                PSType.STRING -> PSString("")
                PSType.BOOLEAN -> PSBoolean(false)
            }
        return context.typeRules.ensureAssignable(psType, default).flatMap { defaultValue ->
            context.variableStore.declareVariable(
                name = node.identifier.name,
                type = psType,
                value = defaultValue,
                isMutable = node.isMutable,
            )
        }
    }
}
