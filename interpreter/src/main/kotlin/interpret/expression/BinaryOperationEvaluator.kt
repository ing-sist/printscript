package interpret.expression

import BinaryOperationNode
import Result
import TokenType
import interpret.registry.ExpressionEvaluator
import language.errors.InterpreterException
import language.errors.TypeMismatchError
import language.types.PSBoolean
import language.types.PSNumber
import language.types.PSString
import language.types.PSType
import language.types.PSValue
import runtime.core.EvaluationContext
import runtime.core.InterpreterRuntime

class BinaryOperationEvaluator(
    private val runtime: runtime.core.InterpreterRuntime,
) : interpret.registry.ExpressionEvaluator<BinaryOperationNode> {
    override fun evaluateExpression(
        node: BinaryOperationNode,
        context: runtime.core.EvaluationContext,
    ): Result<language.types.PSValue, language.errors.InterpreterException> =
        runtime.evaluateExpression(node.left, context).flatMap { left ->
            runtime.evaluateExpression(node.right, context).flatMap { right ->
                evaluateOperation(left, right, node.operator.type)
            }
        }

    private fun evaluateOperation(
        left: language.types.PSValue,
        right: language.types.PSValue,
        operator: TokenType,
    ): Result<language.types.PSValue, language.errors.InterpreterException> {
        return try {
            val result =
                when (operator) {
                    TokenType.Operator.Plus -> evaluateAddition(left, right)
                    TokenType.Operator.Minus, TokenType.Operator.Multiply, TokenType.Operator.Divide ->
                        evaluateArithmeticOperation(left, right, operator)
                    TokenType.Operator.Equals, TokenType.Operator.NotEquals ->
                        evaluateEqualityOperation(left, right, operator)
                    TokenType.Operator.LessThan, TokenType.Operator.LessThanOrEqual,
                    TokenType.Operator.GreaterThan, TokenType.Operator.GreaterThanOrEqual,
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
                TokenType.Operator.Minus -> { a, b -> a - b }
                TokenType.Operator.Multiply -> { a, b -> a * b }
                TokenType.Operator.Divide -> { a, b -> a / b }
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
            TokenType.Operator.Equals -> PSBoolean(isEqual)
            TokenType.Operator.NotEquals -> PSBoolean(!isEqual)
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
                TokenType.Operator.LessThan -> { a, b -> a < b }
                TokenType.Operator.LessThanOrEqual -> { a, b -> a <= b }
                TokenType.Operator.GreaterThan -> { a, b -> a > b }
                TokenType.Operator.GreaterThanOrEqual -> { a, b -> a >= b }
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
