package interpret.statements

import DeclarationNode
import Result
import interpret.registry.StatementExecutor
import language.errors.InterpreterException
import language.types.PSBoolean
import language.types.PSNumber
import language.types.PSString
import language.types.PSType
import language.types.PSValue
import runtime.core.EvaluationContext

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
