package interpreting.core.handlers

import LiteralNode
import Result
import TokenType
import interpreting.core.ExpressionHandler
import interpreting.core.Interpreter
import runtime.InterpreterError
import runtime.Number
import runtime.Str
import runtime.Value

class LiteralHandler : ExpressionHandler<LiteralNode> {
    override fun handle(
        node: LiteralNode,
        interpreter: Interpreter,
    ): Result<Value, InterpreterError> =
        when (node.value.type) {
            TokenType.NumberLiteral -> {
                try {
                    Result.Success(Number(node.value.lexeme.toDouble()))
                } catch (_: NumberFormatException) {
                    Result.Failure(InterpreterError("Invalid number format: ${node.value.lexeme}", node.value.location))
                }
            }
            TokenType.StringLiteral -> Result.Success(Str(node.value.lexeme))
            else ->
                Result.Failure(
                    InterpreterError("Unsupported literal type: ${node.value.type}", node.value.location),
                )
        }
}
