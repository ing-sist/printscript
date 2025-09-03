package interpreting.modules

import interpreting.core.Interpreter

interface InterpreterModule {
    fun register(interpreter: Interpreter)
}
