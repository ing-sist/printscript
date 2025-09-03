package interpreting.core

import Result
import interpreting.modules.InterpreterModule
import runtime.Environment
import runtime.InterpreterError
import runtime.MapEnvironment
import runtime.Output
import runtime.Value

/**
 * Factory para crear instancias del intérprete que usan Result<T, InterpreterError>.
 */
class InterpreterFactory {
    private var environment: Environment = MapEnvironment()
    private var output: Output = Output { println(it) }
    private val modules = mutableListOf<InterpreterModule>()

    fun withEnvironment(environment: Environment): InterpreterFactory {
        this.environment = environment
        return this
    }

    fun withOutput(output: Output): InterpreterFactory {
        this.output = output
        return this
    }

    fun addModule(module: InterpreterModule): InterpreterFactory {
        modules.add(module)
        return this
    }

    fun create(): Interpreter {
        val context = InterpreterContext(environment, output)
        val expressionRegistry = HandlerRegistry<Result<Value, InterpreterError>>()
        val statementRegistry = HandlerRegistry<Result<Unit, InterpreterError>>()

        val interpreter = Interpreter(context, expressionRegistry, statementRegistry)

        // Registrar todos los módulos
        modules.forEach { module ->
            module.register(interpreter)
        }

        return interpreter
    }

    companion object {
        /**
         * Punto de entrada para crear un nuevo intérprete.
         */
        fun newInterpreter(): InterpreterFactory = InterpreterFactory()
    }
}
