package runtime

import Result

interface Environment {
    fun define(
        name: String,
        value: Value,
    )

    fun get(name: String): Result<Value, InterpreterError>

    fun assign(
        name: String,
        value: Value,
    ): Result<Unit, InterpreterError>

    fun createChild(): Environment
}
