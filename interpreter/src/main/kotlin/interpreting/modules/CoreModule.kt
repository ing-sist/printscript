package interpreting.modules

import interpreting.core.Interpreter

/**
 * Módulo CoreModule refactorizado que usa interfaces en lugar de lambdas.
 */
class CoreModule : InterpreterModule {
    override fun register(interpreter: Interpreter) {
        // Registrar todos los handlers usando las interfaces
        ExpressionHandlersModule().register(interpreter)
        StatementHandlersModule().register(interpreter)
    }
}
