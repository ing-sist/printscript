package interpreting.modules

import Location
import Result
import runtime.InterpreterError
import runtime.Number
import runtime.Str
import runtime.Value

object ArithmeticOperations {
    fun handlePlus(
        left: Value,
        right: Value,
        location: Location?,
    ): Result<Value, InterpreterError> =
        when {
            left is Number && right is Number -> Result.Success(Number(left.value + right.value))
            left is Str || right is Str -> Result.Success(Str(left.stringify() + right.stringify()))
            else -> Result.Failure(InterpreterError("Operands must be numbers or at least one string", location))
        }

    fun handleMinus(
        left: Value,
        right: Value,
        location: Location?,
    ): Result<Value, InterpreterError> =
        when {
            left is Number && right is Number -> Result.Success(Number(left.value - right.value))
            else -> Result.Failure(InterpreterError("Operands must be numbers", location))
        }

    fun handleMultiply(
        left: Value,
        right: Value,
        location: Location?,
    ): Result<Value, InterpreterError> =
        when {
            left is Number && right is Number -> Result.Success(Number(left.value * right.value))
            else -> Result.Failure(InterpreterError("Operands must be numbers", location))
        }

    fun handleDivide(
        left: Value,
        right: Value,
        location: Location?,
    ): Result<Value, InterpreterError> =
        when {
            left is Number && right is Number -> {
                if (right.value == 0.0) {
                    Result.Failure(InterpreterError("Division by zero", location))
                } else {
                    Result.Success(Number(left.value / right.value))
                }
            }
            else -> Result.Failure(InterpreterError("Operands must be numbers", location))
        }
}
