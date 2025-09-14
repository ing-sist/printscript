package ps.lang.errors

import ps.lang.types.PSType

open class InterpreterException(
    message: String,
) : Exception(message)

class TypeMismatchError(
    expected: PSType,
    actual: PSType,
    operation: String,
) : InterpreterException(
        "Type mismatch in $operation: expected $expected but got $actual",
    )

class ConstReassignmentError(
    variableName: String,
) : InterpreterException(
        "Cannot reassign const variable '$variableName'",
    )

class UndeclaredVariableError(
    variableName: String,
) : InterpreterException(
        "Undeclared variable '$variableName'",
    )

class MissingEnvVarError(
    variableName: String,
) : InterpreterException(
        "Environment variable '$variableName' not found",
    )

class InputParseError(
    input: String,
    expectedType: PSType,
) : InterpreterException(
        "Cannot parse input '$input' as $expectedType",
    )

class NoExpressionEvaluatorError(
    nodeType: String,
) : InterpreterException(
        "No expression evaluator registered for node type: $nodeType",
    )

class NoStatementExecutorError(
    nodeType: String,
) : InterpreterException(
        "No statement executor registered for node type: $nodeType",
    )
