package ps.interpret.expr

import BinaryOperationNode
import Result
import TokenType
import ps.interpret.registry.ExpressionEvaluator
import ps.lang.errors.InterpreterException
import ps.lang.errors.TypeMismatchError
import ps.lang.types.PSBoolean
import ps.lang.types.PSNumber
import ps.lang.types.PSString
import ps.lang.types.PSType
import ps.lang.types.PSValue
import ps.runtime.core.EvaluationContext
import ps.runtime.core.InterpreterRuntime

class BinaryOperationEvaluator(
    private val runtime: InterpreterRuntime,
) : ExpressionEvaluator<BinaryOperationNode> {
    override fun evaluateExpression(
        node: BinaryOperationNode,
        context: EvaluationContext,
    ): Result<PSValue, InterpreterException> =
        runtime.evaluateExpression(node.left, context).flatMap { left ->
            runtime.evaluateExpression(node.right, context).flatMap { right ->
                evaluateOperation(left, right, node.operator.type)
            }
        }

    private fun evaluateOperation(
        left: PSValue,
        right: PSValue,
        operator: TokenType,
    ): Result<PSValue, InterpreterException> {
        return try {
            val result =
                when (operator) {
                    TokenType.Plus -> evaluateAddition(left, right)
                    TokenType.Minus, TokenType.Multiply, TokenType.Divide ->
                        evaluateArithmeticOperation(left, right, operator)
                    TokenType.Equals, TokenType.NotEquals ->
                        evaluateEqualityOperation(left, right, operator)
                    TokenType.LessThan, TokenType.LessThanOrEqual,
                    TokenType.GreaterThan, TokenType.GreaterThanOrEqual,
                    ->
                        evaluateComparisonOperation(left, right, operator)
                    else -> return Result.Failure(InterpreterException("Unsupported binary operator: $operator"))
                }
            Result.Success(result)
        } catch (e: InterpreterException) {
            Result.Failure(e)
        } catch (error: ArithmeticException) {
            Result.Failure(InterpreterException("Arithmetic error in binary operation: ${error.message}"))
        } catch (error: IllegalArgumentException) {
            Result.Failure(InterpreterException("Invalid argument in binary operation: ${error.message}"))
        }
    }

    private fun evaluateArithmeticOperation(
        left: PSValue,
        right: PSValue,
        operator: TokenType,
    ): PSValue {
        val operation: (Double, Double) -> Double =
            when (operator) {
                TokenType.Minus -> { a, b -> a - b }
                TokenType.Multiply -> { a, b -> a * b }
                TokenType.Divide -> { a, b -> a / b }
                else -> throw InterpreterException("Invalid arithmetic operator: $operator")
            }
        return evaluateArithmetic(left, right, operation)
    }

    private fun evaluateEqualityOperation(
        left: PSValue,
        right: PSValue,
        operator: TokenType,
    ): PSValue {
        val isEqual = areEqual(left, right)
        return when (operator) {
            TokenType.Equals -> PSBoolean(isEqual)
            TokenType.NotEquals -> PSBoolean(!isEqual)
            else -> throw InterpreterException("Invalid equality operator: $operator")
        }
    }

    private fun evaluateComparisonOperation(
        left: PSValue,
        right: PSValue,
        operator: TokenType,
    ): PSValue {
        val operation: (Double, Double) -> Boolean =
            when (operator) {
                TokenType.LessThan -> { a, b -> a < b }
                TokenType.LessThanOrEqual -> { a, b -> a <= b }
                TokenType.GreaterThan -> { a, b -> a > b }
                TokenType.GreaterThanOrEqual -> { a, b -> a >= b }
                else -> throw InterpreterException("Invalid comparison operator: $operator")
            }
        return evaluateComparison(left, right, operation)
    }

    private fun evaluateAddition(
        left: PSValue,
        right: PSValue,
    ): PSValue =
        when {
            left is PSNumber && right is PSNumber -> PSNumber(left.value + right.value)
            left is PSString || right is PSString -> PSString(left.formatForPrint() + right.formatForPrint())
            else -> throw TypeMismatchError(PSType.NUMBER, left.type, "addition")
        }

    private fun evaluateArithmetic(
        left: PSValue,
        right: PSValue,
        operation: (Double, Double) -> Double,
    ): PSValue {
        if (left !is PSNumber || right !is PSNumber) {
            throw TypeMismatchError(PSType.NUMBER, left.type, "arithmetic operation")
        }
        return PSNumber(operation(left.value, right.value))
    }

    private fun evaluateComparison(
        left: PSValue,
        right: PSValue,
        operation: (Double, Double) -> Boolean,
    ): PSValue {
        if (left !is PSNumber || right !is PSNumber) {
            throw TypeMismatchError(PSType.NUMBER, left.type, "comparison")
        }
        return PSBoolean(operation(left.value, right.value))
    }

    private fun areEqual(
        left: PSValue,
        right: PSValue,
    ): Boolean =
        when {
            left is PSNumber && right is PSNumber -> left.value == right.value
            left is PSString && right is PSString -> left.value == right.value
            left is PSBoolean && right is PSBoolean -> left.value == right.value
            else -> false
        }
}
