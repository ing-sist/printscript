package language.types

import Result
import language.errors.InputParseError
import language.errors.InterpreterException
import language.errors.TypeMismatchError

interface TypeRules {
    fun parseRawTo(
        expectedType: PSType,
        rawInput: String,
    ): Result<PSValue, InterpreterException>

    fun ensureAssignable(
        expectedType: PSType,
        value: PSValue,
    ): Result<PSValue, InterpreterException>

    fun formatForPrint(value: PSValue): String
}

class DefaultTypeRules : TypeRules {
    override fun parseRawTo(
        expectedType: PSType,
        rawInput: String,
    ): Result<PSValue, InterpreterException> =
        try {
            when (expectedType) {
                PSType.NUMBER -> {
                    val doubleValue = rawInput.toDoubleOrNull()
                    if (doubleValue != null) {
                        Result.Success(PSNumber(doubleValue))
                    } else {
                        Result.Failure(InputParseError(rawInput, expectedType))
                    }
                }
                PSType.BOOLEAN -> {
                    when (rawInput.lowercase()) {
                        "true", "1" -> Result.Success(PSBoolean(true))
                        "false", "0" -> Result.Success(PSBoolean(false))
                        else -> Result.Failure(InputParseError(rawInput, expectedType))
                    }
                }
                PSType.STRING -> Result.Success(PSString(rawInput))
            }
        } catch (error: NumberFormatException) {
            Result.Failure(
                InterpreterException(
                    "Number format error parsing '$rawInput' to $expectedType: ${error.message}",
                ),
            )
        } catch (error: IllegalArgumentException) {
            Result.Failure(
                InterpreterException(
                    "Invalid argument parsing '$rawInput' to $expectedType: ${error.message}",
                ),
            )
        }

    override fun ensureAssignable(
        expectedType: PSType,
        value: PSValue,
    ): Result<PSValue, InterpreterException> =
        if (value.type != expectedType) {
            Result.Failure(TypeMismatchError(expectedType, value.type, "assignment"))
        } else {
            Result.Success(value)
        }

    override fun formatForPrint(value: PSValue): String = value.formatForPrint()
}
