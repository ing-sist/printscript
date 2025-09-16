package runtime.core

import Result
import language.errors.ConstReassignmentError
import language.errors.InterpreterException
import language.errors.UndeclaredVariableError
import language.types.PSType
import language.types.PSValue
import java.util.Stack

class ScopedVariableStore {
    private val scopeStack: Stack<MutableMap<String, VariableBinding>> = Stack()

    init {
        // Siempre comenzamos con un scope global
        scopeStack.push(mutableMapOf())
    }

    fun declareVariable(
        name: String,
        type: PSType,
        value: PSValue,
        isMutable: Boolean,
    ): Result<Unit, InterpreterException> =
        try {
            val currentScope = scopeStack.peek()
            val binding = VariableBinding(name, type, value, isMutable)
            currentScope[name] = binding
            Result.Success(Unit)
        } catch (error: IllegalStateException) {
            Result.Failure(InterpreterException("Error declaring variable '$name': ${error.message}"))
        }

    fun assignVariable(
        name: String,
        value: PSValue,
    ): Result<Unit, InterpreterException> {
        // Buscar la variable en todos los scopes desde el más interno
        for (i in scopeStack.size - 1 downTo 0) {
            val scope = scopeStack[i]
            val binding = scope[name]
            if (binding != null) {
                return if (!binding.isMutable) {
                    Result.Failure(ConstReassignmentError(name))
                } else {
                    scope[name] = binding.copy(value = value)
                    Result.Success(Unit)
                }
            }
        }
        return Result.Failure(UndeclaredVariableError(name))
    }

    fun getVariable(name: String): Result<VariableBinding, InterpreterException> {
        // Buscar la variable en todos los scopes desde el más interno
        for (i in scopeStack.size - 1 downTo 0) {
            val scope = scopeStack[i]
            val binding = scope[name]
            if (binding != null) {
                return Result.Success(binding)
            }
        }
        return Result.Failure(UndeclaredVariableError(name))
    }

    fun <T> withNewScope(block: () -> T): T {
        scopeStack.push(mutableMapOf())
        try {
            return block()
        } finally {
            scopeStack.pop()
        }
    }
}
