package interpreting.core.handlers

import BinaryOperationNode
import Result
import TokenType
import interpreting.core.ExpressionHandler
import interpreting.core.Interpreter
import interpreting.modules.ArithmeticOperations
import interpreting.modules.ComparisonOperations
import runtime.InterpreterError
import runtime.Value

class BinaryOperationHandler : ExpressionHandler<BinaryOperationNode> {
    override fun handle(
        node: BinaryOperationNode,
        interpreter: Interpreter,
    ): Result<Value, InterpreterError> {
        val leftResult = interpreter.evaluateExpression(node.left)
        val rightResult = interpreter.evaluateExpression(node.right)

        return when {
            leftResult.isFailure -> leftResult
            rightResult.isFailure -> rightResult
            else -> executeOperation(node, leftResult.getOrNull()!!, rightResult.getOrNull()!!)
        }
    }

    private fun executeOperation(
        node: BinaryOperationNode,
        left: Value,
        right: Value,
    ): Result<Value, InterpreterError> =
        when (node.operator.type) {
            TokenType.Plus ->
                ArithmeticOperations.handlePlus(
                    left,
                    right,
                    node.operator.location,
                )
            TokenType.Minus ->
                ArithmeticOperations.handleMinus(
                    left,
                    right,
                    node.operator.location,
                )
            TokenType.Multiply ->
                ArithmeticOperations.handleMultiply(
                    left,
                    right,
                    node.operator.location,
                )
            TokenType.Divide ->
                ArithmeticOperations.handleDivide(
                    left,
                    right,
                    node.operator.location,
                )
            TokenType.Equals -> ComparisonOperations.handleEquals(left, right)
            TokenType.NotEquals -> ComparisonOperations.handleNotEquals(left, right)
            TokenType.LessThan ->
                ComparisonOperations.handleLessThan(
                    left,
                    right,
                    node.operator.location,
                )
            TokenType.LessThanOrEqual ->
                ComparisonOperations.handleLessThanOrEqual(
                    left,
                    right,
                    node.operator.location,
                )
            TokenType.GreaterThan ->
                ComparisonOperations.handleGreaterThan(
                    left,
                    right,
                    node.operator.location,
                )
            TokenType.GreaterThanOrEqual ->
                ComparisonOperations.handleGreaterThanOrEqual(
                    left,
                    right,
                    node.operator.location,
                )
            else ->
                Result.Failure(
                    InterpreterError("Unsupported binary operator: ${node.operator.type}", node.operator.location),
                )
        }
}
