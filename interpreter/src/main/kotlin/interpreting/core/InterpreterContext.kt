package interpreting.core

import runtime.Environment
import runtime.Output

/**
 * Contexto del intérprete que contiene las dependencias necesarias para la ejecución.
 */
data class InterpreterContext(
    val environment: Environment,
    val output: Output,
)
