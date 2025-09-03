package runtime

import Location

/**
 * Error específico del intérprete que se usa con el patrón Result.
 * Ya no extiende de RuntimeException porque usamos Result<T, InterpreterError>
 */
data class InterpreterError(
    val message: String,
    val location: Location? = null,
) {
    fun formatMessage(): String =
        if (location != null) {
            "Runtime error at line ${location.line}, column ${location.startCol}: $message"
        } else {
            "Runtime error: $message"
        }

    override fun toString(): String = formatMessage()
}
