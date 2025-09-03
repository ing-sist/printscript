package interpreting.modules

import Location
import Result
import runtime.Bool
import runtime.InterpreterError
import runtime.Nil
import runtime.Number
import runtime.Str
import runtime.Value

object ComparisonOperations {
    fun handleEquals(
        left: Value,
        right: Value,
    ): Result<Value, InterpreterError> = Result.Success(Bool(isEqual(left, right)))

    fun handleNotEquals(
        left: Value,
        right: Value,
    ): Result<Value, InterpreterError> = Result.Success(Bool(!isEqual(left, right)))

    fun handleLessThan(
        left: Value,
        right: Value,
        location: Location?,
    ): Result<Value, InterpreterError> = handleComparison(left, right, location) { a, b -> a < b }

    fun handleLessThanOrEqual(
        left: Value,
        right: Value,
        location: Location?,
    ): Result<Value, InterpreterError> = handleComparison(left, right, location) { a, b -> a <= b }

    fun handleGreaterThan(
        left: Value,
        right: Value,
        location: Location?,
    ): Result<Value, InterpreterError> = handleComparison(left, right, location) { a, b -> a > b }

    fun handleGreaterThanOrEqual(
        left: Value,
        right: Value,
        location: Location?,
    ): Result<Value, InterpreterError> = handleComparison(left, right, location) { a, b -> a >= b }

    private fun handleComparison(
        left: Value,
        right: Value,
        location: Location?,
        op: (Double, Double) -> Boolean,
    ): Result<Value, InterpreterError> =
        when {
            left is Number && right is Number -> Result.Success(Bool(op(left.value, right.value)))
            else -> Result.Failure(InterpreterError("Operands must be numbers", location))
        }

    private fun isEqual(
        left: Value,
        right: Value,
    ): Boolean =
        when {
            left is Nil && right is Nil -> true
            left is Nil || right is Nil -> false
            left is Number && right is Number -> left.value == right.value
            left is Str && right is Str -> left.value == right.value
            left is Bool && right is Bool -> left.value == right.value
            else -> false
        }
}
