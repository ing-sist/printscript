package interpret.expression

import LiteralNode
import Result
import TokenType
import interpret.registry.ExpressionEvaluator
import language.errors.InterpreterException
import language.types.PSBoolean
import language.types.PSNumber
import language.types.PSString
import language.types.PSValue
import runtime.core.EvaluationContext

class LiteralExpressionEvaluator : ExpressionEvaluator<LiteralNode> {
    override fun evaluateExpression(
        node: LiteralNode,
        context: EvaluationContext,
    ): Result<PSValue, InterpreterException> {
        return try {
            val result =
                when (node.value.type) {
                    TokenType.NumberLiteral -> PSNumber(node.value.lexeme.toDouble())
                    TokenType.StringLiteral -> PSString(node.value.lexeme.removeSurrounding("\""))
                    else -> {
                        // Para boolean literals, asumimos que vienen como identificadores true/false
                        when (node.value.lexeme.lowercase()) {
                            "true" -> PSBoolean(true)
                            "false" -> PSBoolean(false)
                            else -> return Result.Failure(
                                InterpreterException("Unknown literal type: ${node.value.lexeme}"),
                            )
                        }
                    }
                }
            Result.Success(result)
        } catch (error: NumberFormatException) {
            Result.Failure(InterpreterException("Number format error in literal: ${error.message}"))
        } catch (error: IllegalArgumentException) {
            Result.Failure(InterpreterException("Invalid argument in literal: ${error.message}"))
        }
    }
}
