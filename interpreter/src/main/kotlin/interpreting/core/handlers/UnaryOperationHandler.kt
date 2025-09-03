package interpreting.core.handlers

import Result
import TokenType
import UnaryOperationNode
import interpreting.core.ExpressionHandler
import interpreting.core.Interpreter
import runtime.InterpreterError
import runtime.Number
import runtime.Value

class UnaryOperationHandler : ExpressionHandler<UnaryOperationNode> {
    override fun handle(
        node: UnaryOperationNode,
        interpreter: Interpreter,
    ): Result<Value, InterpreterError> {
        val operandResult = interpreter.evaluateExpression(node.operand)
        if (operandResult.isFailure) return operandResult

        val operand = operandResult.getOrNull()!!

        return when (node.operator.type) {
            TokenType.Minus -> {
                when (operand) {
                    is Number -> Result.Success(Number(-operand.value))
                    else -> Result.Failure(InterpreterError("Operand must be a number", node.operator.location))
                }
            }
            else ->
                Result.Failure(
                    InterpreterError("Unsupported unary operator: ${node.operator.type}", node.operator.location),
                )
        }
    }
}
