package runtime

import Result

class MapEnvironment(
    private val parent: Environment? = null,
) : Environment {
    private val values = mutableMapOf<String, Value>()

    override fun define(
        name: String,
        value: Value,
    ) {
        values[name] = value
    }

    override fun get(name: String): Result<Value, InterpreterError> {
        val value = values[name]
        return if (value != null) {
            Result.Success(value)
        } else {
            parent?.get(name) ?: Result.Failure(InterpreterError("Undefined variable '$name'."))
        }
    }

    override fun assign(
        name: String,
        value: Value,
    ): Result<Unit, InterpreterError> =
        if (values.containsKey(name)) {
            values[name] = value
            Result.Success(Unit)
        } else {
            parent?.assign(name, value) ?: Result.Failure(InterpreterError("Undefined variable '$name'."))
        }

    override fun createChild(): Environment = MapEnvironment(this)
}
